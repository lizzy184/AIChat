from fastapi import APIRouter, Depends

from sqlalchemy.orm import Session

from database.database import get_db
from utils.security import create_access_token
from database.models import User
from sqlalchemy.exc import IntegrityError
from model.schema import RegisterRequest,LoginRequest,TokenResponse,RefreshRequest,RefreshResponse
from fastapi import HTTPException
from utils.security import hash_password
from utils.security import verify_password
from utils.security import create_refresh_token
from utils.security import decode_refresh_token
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

    try:
        db.add(new_user)
        db.commit()
        db.refresh(new_user)

    except IntegrityError:
        db.rollback()

        raise HTTPException(
            status_code=409,
            detail="用户名已存在"
        )

    return {
        "user_id": new_user.id
    }
@router.post("/login",response_model=TokenResponse)
def login(
data:LoginRequest,
db: Session = Depends(get_db)
):


    user = db.query(User).filter(User.username==data.username).first()


    if user is None:


        raise HTTPException(

            status_code=401,

            detail="用户名或密码错误"

        )
    password_correct = verify_password(
       data.password,
        user.password_hash
    )
    if not password_correct:

        raise HTTPException(
            status_code=401,
            detail="用户名或密码错误"
        )




    access_token = create_access_token(
    data={
        "sub": str(user.id)
    }
)

    refresh_token = create_refresh_token({
    "sub": str(user.id)
})


    return {
    "access_token": access_token,
    "refresh_token": refresh_token,
    "token_type": "bearer"
}
@router.post("/refresh", response_model=RefreshResponse)
def refresh(
    data: RefreshRequest,
    db: Session = Depends(get_db)
):
    payload = decode_refresh_token(
        data.refresh_token
    )

    if payload is None:
        raise HTTPException(
            status_code=401,
            detail="Refresh Token 无效或已过期"
        )

    user_id = payload.get("sub")

    if user_id is None:
        raise HTTPException(
            status_code=401,
            detail="Refresh Token 无效"
        )

    user = db.query(User).filter(
        User.id == int(user_id)
    ).first()

    if user is None:
        raise HTTPException(
            status_code=401,
            detail="Refresh Token 无效"
        )

    access_token = create_access_token({
        "sub": str(user.id)
    })

    return {
        "access_token": access_token,
        "token_type": "bearer"
    }