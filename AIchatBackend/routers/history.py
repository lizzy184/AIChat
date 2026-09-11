from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database.database import get_db
from database.models import Message
from database.models import User
from utils.auth import get_current_user

router = APIRouter()



# 查询历史记录

@router.get("/history")
def get_history(
      current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):


    messages = (

        db.query(Message)

       .filter(
            Message.user_id==current_user.id
        )
    
        .all()

    )


    return messages





# 删除历史记录

@router.delete("/history")
def clear_history(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    db.query(Message).filter(
        Message.user_id == current_user.id
    ).delete(
        synchronize_session=False
    )

    db.commit()

    return {
        "message": "聊天记录已清空"
    }