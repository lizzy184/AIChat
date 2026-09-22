import json
import uuid
import logging

from datetime import datetime

from fastapi import (
    APIRouter,
    Request,
    Depends,
    HTTPException,
    status
)

from fastapi.responses import StreamingResponse

from sqlalchemy.orm import Session

from database.database import get_db
from database.models import Message, Conversation

from model.schema import ChatRequest

from services.chat import chat_ai_stream
from services.memory import MemoryService

from utils.auth import get_current_user


router = APIRouter()

logger = logging.getLogger(__name__)


# =========================================================
# POST /chat
# =========================================================
#
# 创建新会话：
#
# {
#     "message": "你好",
#     "conversation_id": null
# }
#
#
# 继续已有会话：
#
# {
#     "message": "继续刚才的话题",
#     "conversation_id": 1
# }
#
# =========================================================

@router.post("/chat")
async def chat(
    request: Request,
    chat_request: ChatRequest,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):

    # =====================================================
    # 1. 检查消息内容
    # =====================================================

    if not chat_request.message.strip():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="message不能为空"
        )

    # =====================================================
    # 2. 判断是新会话还是继续已有会话
    # =====================================================

    if chat_request.conversation_id is None:

        # -------------------------------------------------
        # 创建新会话
        # -------------------------------------------------

        conversation = Conversation(
            user_id=current_user.id,
            title="新聊天"
        )

        db.add(conversation)

        db.commit()

        db.refresh(conversation)

        conversation_id = conversation.id

    else:

        # -------------------------------------------------
        # 继续已有会话
        #
        # 非常重要：
        # 必须确认这个 Conversation 属于当前用户
        # -------------------------------------------------

        conversation = (
            db.query(Conversation)
            .filter(
                Conversation.id == chat_request.conversation_id,
                Conversation.user_id == current_user.id
            )
            .first()
        )

        if conversation is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="会话不存在"
            )

        conversation_id = conversation.id

    # =====================================================
    # 3. 获取当前会话历史
    # =====================================================

    memory_service = MemoryService(db)

    history = memory_service.get_history(
        conversation_id=conversation_id
    )

    # =====================================================
    # 4. 创建请求 ID
    #
    # 用于日志追踪
    # =====================================================

    request_id = str(uuid.uuid4())

    # =====================================================
    # 5. SSE Generator
    # =====================================================

    async def event_generator():

        answer = []

        try:

            # =================================================
            # 发送 Conversation ID
            #
            # Android 收到这个事件以后：
            #
            # currentConversationId = conversation_id
            #
            # =================================================

            yield (
                "event:conversation\n"
                f"data:{json.dumps({'conversation_id': conversation_id})}\n\n"
            )

            # =================================================
            # 调用 AI
            # =================================================

            async for chunk in chat_ai_stream(
                chat_request.message,
                history
            ):

                # -------------------------------------------------
                # 检查客户端是否断开
                # -------------------------------------------------

                if await request.is_disconnected():

                    logger.warning(
                        f"Client disconnected, request_id={request_id}"
                    )

                    return

                # -------------------------------------------------
                # 保存 AI 输出
                # -------------------------------------------------

                answer.append(chunk)

                # -------------------------------------------------
                # SSE 安全处理
                #
                # 如果 chunk 中存在换行：
                #
                # hello
                # world
                #
                # 需要变成：
                #
                # data:hello
                # data:world
                #
                # -------------------------------------------------

                safe_chunk = chunk.replace(
                    "\n",
                    "\ndata:"
                )

                yield (
                    "event:message\n"
                    f"data:{safe_chunk}\n\n"
                )

            # =================================================
            # 6. 拼接完整 AI 回答
            # =================================================

            result = "".join(answer)

            # =================================================
            # 7. 保存聊天记录
            # =================================================

            if result.strip():

                # -------------------------------------------------
                # 用户消息
                # -------------------------------------------------

                user_message = Message(
                    user_id=current_user.id,
                    conversation_id=conversation_id,
                    role="user",
                    content=chat_request.message
                )

                # -------------------------------------------------
                # AI 消息
                # -------------------------------------------------

                assistant_message = Message(
                    user_id=current_user.id,
                    conversation_id=conversation_id,
                    role="assistant",
                    content=result
                )

                db.add(user_message)

                db.add(assistant_message)

                # -------------------------------------------------
                # 更新会话最后活跃时间
                # -------------------------------------------------

                conversation.updated_time = datetime.utcnow()

                # -------------------------------------------------
                # 提交数据库
                # -------------------------------------------------

                db.commit()

            # =================================================
            # 8. SSE 完成事件
            # =================================================

            yield (
                "event:done\n"
                "data:[DONE]\n\n"
            )

        except Exception as e:

            logger.exception(
                f"Chat error, request_id={request_id}: {e}"
            )

            # -------------------------------------------------
            # 数据库异常以后回滚
            # -------------------------------------------------

            db.rollback()

            yield (
                "event:error\n"
                "data:AI服务异常\n\n"
            )

    # =========================================================
    # 9. 返回 SSE
    # =========================================================

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no"
        }
    )