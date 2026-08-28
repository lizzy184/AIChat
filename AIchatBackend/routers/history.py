from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database.database import get_db
from database.models import Message


router = APIRouter()



# 查询历史记录

@router.get("/history")
def get_history(
      user_id:int,
    db: Session = Depends(get_db)
):


    messages = (

        db.query(Message)

       .filter(
            Message.user_id==user_id
        )
    
        .all()

    )


    return messages





# 删除历史记录

@router.delete("/history")
def clear_history(
    db: Session = Depends(get_db)
):


    db.query(Message).delete()


    db.commit()



    return {

        "message":
        "聊天记录已清空"

    }