from fastapi import APIRouter, HTTPException, Depends
from sqlalchemy.orm import Session

from model.schema import ChatRequest, ChatResponse
from services.chat import chat_ai

from database.database import get_db
from database.models import Message
from database.models import User
from utils.auth import get_current_user
router = APIRouter()


@router.post(
    "/chat",
    response_model=ChatResponse,
    responses={
        400: {
            "description": "请求参数错误"
        },
        500: {
            "description": "AI服务异常"
        }
    }
)
async def chat(request: ChatRequest,
               db: Session = Depends(get_db)
               , current_user: User = Depends(get_current_user)
               ):

    # =========================
    # 1. 参数校验
    # =========================
    print(db)
    if not request.message.strip():

        raise HTTPException(
            status_code=400,
            detail="message不能为空"
        )


    # =========================
    # 2. 调用AI服务
    # =========================

    try:

        result = await chat_ai(
            request.message
        )


    except Exception as e:

        # 后面Day8日志会替换这里
        print(
            f"AI调用失败: {e}"
        )


        raise HTTPException(
            status_code=500,
            detail="AI服务暂时不可用"
        )
    
    user_message = Message(
    user_id=current_user.id,
    role="user",
    content=request.message
)


    db.add(user_message)



# 保存AI消息

    ai_message = Message(
    user_id=current_user.id,
    role="assistant",
    content=result
)


    db.add(ai_message)



    db.commit()

    # =========================
    # 3. 返回结果
    # =========================

    return ChatResponse(
        reply=result
    )
