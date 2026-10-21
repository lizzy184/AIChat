
import json
import uuid
import logging
import time

from datetime import datetime

from fastapi import (
    APIRouter,
    Request,
    Depends,
    HTTPException,
    status,
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


def stream_log(message: str):
    """立即输出 SSE 调试日志，避免日志缓冲。"""
    print(f"[SSE_DEBUG] {message}", flush=True)


@router.post("/chat")

async def chat(
    request: Request,
    chat_request: ChatRequest,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    # 1. 检查消息内容
    if not chat_request.message.strip():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="message不能为空",
        )

    # 2. 创建新会话或获取已有会话
    if chat_request.conversation_id is None:
        conversation = Conversation(
            user_id=current_user.id,
            title="新聊天",
        )

        db.add(conversation)
        db.commit()
        db.refresh(conversation)

        conversation_id = conversation.id
    else:
        conversation = (
            db.query(Conversation)
            .filter(
                Conversation.id == chat_request.conversation_id,
                Conversation.user_id == current_user.id,
            )
            .first()
        )

        if conversation is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="会话不存在",
            )

        conversation_id = conversation.id

    # 3. 获取会话历史
    memory_service = MemoryService(db)

    history = memory_service.get_history(
        conversation_id=conversation_id,
    )

    # 4. 创建请求 ID
    request_id = str(uuid.uuid4())

    stream_log(
        f"REQUEST_STARTED "
        f"request_id={request_id} "
        f"conversation_id={conversation_id}"
    )

    logger.info(
        "[SSE] Request started: request_id=%s, conversation_id=%s",
        request_id,
        conversation_id,
    )

    # 5. SSE 生成器
    async def event_generator():
        answer = []
        stream_start = time.monotonic()

        chunk_count = 0
        event_count = 0
        total_characters = 0

        stream_log(
            f"GENERATOR_ENTERED "
            f"request_id={request_id} "
            f"t=+{time.monotonic() - stream_start:.3f}s"
        )

        try:
            # 5.1 发送 conversation 事件
            conversation_data = json.dumps(
                {"conversation_id": conversation_id},
                ensure_ascii=False,
            )

            conversation_event = (
                "event: conversation\n"
                f"data: {conversation_data}\n\n"
            )

            stream_log(
                f"BEFORE_YIELD_CONVERSATION "
                f"request_id={request_id} "
                f"t=+{time.monotonic() - stream_start:.3f}s"
            )

            yield conversation_event.encode("utf-8")
            event_count += 1

            stream_log(
                f"AFTER_YIELD_CONVERSATION "
                f"request_id={request_id} "
                f"t=+{time.monotonic() - stream_start:.3f}s"
            )

            # 5.2 调用 AI，逐段交付 SSE 数据
            async for chunk in chat_ai_stream(
                chat_request.message,
                history,
            ):
                elapsed = time.monotonic() - stream_start

                if await request.is_disconnected():
                    stream_log(
                        f"CLIENT_DISCONNECTED "
                        f"request_id={request_id} "
                        f"t=+{elapsed:.3f}s "
                        f"chunks={chunk_count}"
                    )
                    return

                if not chunk:
                    continue

                chunk_count += 1
                total_characters += len(chunk)
                answer.append(chunk)

                stream_log(
                    f"CHUNK_RECEIVED "
                    f"request_id={request_id} "
                    f"t=+{elapsed:.3f}s "
                    f"chunk={chunk_count} "
                    f"length={len(chunk)} "
                    f"total={total_characters} "
                    f"text={chunk[:30]!r}"
                )

                # 防止内容中的换行破坏 SSE 数据格式
                safe_chunk = (
                    chunk.replace("\r\n", "\n")
                    .replace("\r", "\n")
                    .replace("\n", "\ndata: ")
                )

                sse_message = (
                    "event: message\n"
                    f"data: {safe_chunk}\n\n"
                )

                # 记录交付生成器的时间
                stream_log(
                    f"BEFORE_YIELD_MESSAGE "
                    f"request_id={request_id} "
                    f"t=+{time.monotonic() - stream_start:.3f}s "
                    f"chunk={chunk_count} "
                    f"length={len(chunk)}"
                )

                # 交付 UTF-8 字节，避免隐式编码差异
                yield sse_message.encode("utf-8")
                event_count += 1

                # 注意：此日志不代表 Android 已收到数据
                stream_log(
                    f"AFTER_YIELD_MESSAGE "
                    f"request_id={request_id} "
                    f"t=+{time.monotonic() - stream_start:.3f}s "
                    f"chunk={chunk_count} "
                    f"events={event_count}"
                )

            # 6. 拼接完整回答
            result = "".join(answer)

            stream_log(
                f"STREAM_FINISHED "
                f"request_id={request_id} "
                f"elapsed={time.monotonic() - stream_start:.3f}s "
                f"chunks={chunk_count} "
                f"characters={len(result)}"
            )

            # 7. 保存聊天记录
            if result.strip():
                user_message = Message(
                    user_id=current_user.id,
                    conversation_id=conversation_id,
                    role="user",
                    content=chat_request.message,
                )

                assistant_message = Message(
                    user_id=current_user.id,
                    conversation_id=conversation_id,
                    role="assistant",
                    content=result,
                )

                db.add(user_message)
                db.add(assistant_message)

                conversation.updated_time = datetime.utcnow()

                db.commit()

                stream_log(
                    f"MESSAGES_SAVED "
                    f"request_id={request_id} "
                    f"t=+{time.monotonic() - stream_start:.3f}s"
                )

            # 8. 发送完成事件
            stream_log(
                f"BEFORE_YIELD_DONE "
                f"request_id={request_id} "
                f"t=+{time.monotonic() - stream_start:.3f}s"
            )

            yield b"event: done\ndata: [DONE]\n\n"
            event_count += 1

            stream_log(
                f"STREAM_DONE "
                f"request_id={request_id} "
                f"events={event_count} "
                f"elapsed={time.monotonic() - stream_start:.3f}s"
            )

        except Exception as exc:
            logger.exception(
                "[SSE_ERROR] request_id=%s",
                request_id,
            )

            stream_log(
                f"STREAM_ERROR "
                f"request_id={request_id} "
                f"error={exc!r}"
            )

            db.rollback()

            # 如果连接仍然可用，发送 SSE 错误事件
            try:
                if not await request.is_disconnected():
                    error_data = json.dumps(
                        {"detail": "AI服务异常"},
                        ensure_ascii=False,
                    )
                    yield (
                        "event: error\n"
                        f"data: {error_data}\n\n"
                    ).encode("utf-8")
            except Exception:
                logger.exception(
                    "[SSE_ERROR_EVENT_FAILED] request_id=%s",
                    request_id,
                )

    # 9. 返回流式响应
    stream_log(
        f"RETURN_STREAMING_RESPONSE "
        f"request_id={request_id}"
    )

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache, no-transform",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no",
        },
    )
