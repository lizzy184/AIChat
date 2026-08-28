from fastapi import APIRouter, Depends

from sqlalchemy.orm import Session

from database.database import get_db

from database.models import User

from model.schema import RegisterRequest,LoginRequest
from fastapi import HTTPException
from utils.security import hash_password
from utils.security import verify_password
router = APIRouter()



@router.post("/register")
def register(
    data: RegisterRequest,
    db: Session = Depends(get_db)
):


    new_user = User(

        username=data.username,
        password_hash=hash_password(data.password)
    )


    db.add(new_user)


    db.commit()


    db.refresh(new_user)



    return {

        "user_id": new_user.id

     

    }
@router.post("/login")
def login(
data:LoginRequest,
db: Session = Depends(get_db)
):


    user = db.query(User).filter(User.username==data.username).first()


    if user is None:


        raise HTTPException(

            status_code=404,

            detail="用户不存在"

        )
    password_correct = verify_password(
       data.password,
        user.password_hash
    )
    if not password_correct:

        raise HTTPException(
            status_code=401,
            detail="密码错误"
        )





    return {

        "user_id":user.id

    }
