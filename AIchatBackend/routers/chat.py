from fastapi import APIRouter, HTTPException, Depends
from sqlalchemy.orm import Session

from model.schema import ChatRequest, ChatResponse
from services.chat import chat_ai_stream

from database.database import get_db
from database.models import Message
from database.models import User
from utils.auth import get_current_user
from utils.logger import logger
from fastapi.responses import StreamingResponse
router = APIRouter()



@router.post(
    "/chat",
    
    responses={
        400: {
            "description": "请求参数错误"
        },
        500: {
            "description": "AI服务异常"
        }
    }
)
async def chat(
    request: ChatRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):

    if not request.message.strip():
        raise HTTPException(
            status_code=400,
            detail="message不能为空"
        )

    async def event_generator():

        answer_parts = []

        try:

            async for chunk in chat_ai_stream(request.message):

                answer_parts.append(chunk)

                yield f"data: {chunk}\n\n"

        except Exception:

            logger.exception("AI服务调用失败")

            yield "event: error\ndata: AI服务暂时不可用\n\n"

            return

        result = "".join(answer_parts)

        user_message = Message(
            user_id=current_user.id,
            role="user",
            content=request.message
        )

        db.add(user_message)

        ai_message = Message(
            user_id=current_user.id,
            role="assistant",
            content=result
        )

        db.add(ai_message)

        db.commit()

        yield "event: done\ndata: [DONE]\n\n"

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream"
    )