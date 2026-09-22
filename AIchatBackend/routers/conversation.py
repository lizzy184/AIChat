from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    status
)

from sqlalchemy.orm import Session

from database.database import get_db
from database.models import Conversation, Message

from utils.auth import get_current_user


router = APIRouter(
    prefix="/conversations",
    tags=["Conversations"]
)


# =========================================================
# GET /conversations
#
# 获取当前用户的所有会话
# =========================================================

@router.get("")
def get_conversations(
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):

    conversations = (
        db.query(Conversation)
        .filter(
            Conversation.user_id == current_user.id
        )
        .order_by(
            Conversation.updated_time.desc()
        )
        .all()
    )

    return [
        {
            "id": conversation.id,
            "title": conversation.title,
            "created_time": conversation.created_time,
            "updated_time": conversation.updated_time
        }
        for conversation in conversations
    ]


# =========================================================
# GET /conversations/{conversation_id}/messages
#
# 获取某个会话的历史消息
# =========================================================

@router.get("/{conversation_id}/messages")
def get_conversation_messages(
    conversation_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):

    # =====================================================
    # 1. 查询会话
    #
    # 同时验证：
    #
    # conversation_id
    # +
    # current_user.id
    #
    # =====================================================

    conversation = (
        db.query(Conversation)
        .filter(
            Conversation.id == conversation_id,
            Conversation.user_id == current_user.id
        )
        .first()
    )

    if conversation is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="会话不存在"
        )

    # =====================================================
    # 2. 查询消息
    # =====================================================

    messages = (
        db.query(Message)
        .filter(
            Message.conversation_id == conversation_id
        )
        .order_by(
            Message.id.asc()
        )
        .all()
    )

    # =====================================================
    # 3. 返回
    # =====================================================

    return [
        {
            "id": message.id,
            "role": message.role,
            "content": message.content,
            "created_time": message.created_time
        }
        for message in messages
    ]


# =========================================================
# DELETE /conversations/{conversation_id}
#
# 删除会话以及该会话下的所有消息
# =========================================================

@router.delete("/{conversation_id}")
def delete_conversation(
    conversation_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user)
):

    # =====================================================
    # 1. 查询会话
    # =====================================================

    conversation = (
        db.query(Conversation)
        .filter(
            Conversation.id == conversation_id,
            Conversation.user_id == current_user.id
        )
        .first()
    )

    if conversation is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="会话不存在"
        )

    # =====================================================
    # 2. 删除消息
    # =====================================================

    db.query(Message).filter(
        Message.conversation_id == conversation_id
    ).delete(
        synchronize_session=False
    )

    # =====================================================
    # 3. 删除会话
    # =====================================================

    db.delete(conversation)

    # =====================================================
    # 4. 提交
    # =====================================================

    db.commit()

    return {
        "message": "会话删除成功"
    }