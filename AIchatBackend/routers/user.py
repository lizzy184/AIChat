from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from fastapi import HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from services.redis_service import redis_service
from database.database import get_db
from database.models import User
from model.schema import (
    RegisterRequest,
    LoginRequest,
    TokenResponse,
    RefreshRequest,
    RefreshResponse,
    LogoutRequest
)
from utils.security import (
    create_access_token,
    hash_password,
    verify_password,
    create_refresh_token,
    decode_refresh_token,
    decode_access_token
)
from datetime import datetime, timezone

router = APIRouter()
security = HTTPBearer()

def get_remaining_time(payload):
    exp=payload.get("exp")
    if exp is None:
        return 0
    now=datetime.now(timezone.utc)
    expire_time=datetime.fromtimestamp(exp,timezone.utc)
    remaining = (expire_time-now).total_seconds()
    return max(0,int(remaining))






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


@router.post("/login", response_model=TokenResponse)
def login(
    data: LoginRequest,
    db: Session = Depends(get_db)
):
    user = db.query(User).filter(
        User.username == data.username
    ).first()

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
    refresh_token = data.refresh_token

    if redis_service.is_blacklisted(
        f"blacklist:refresh:{refresh_token}"
    ):
        raise HTTPException(
            status_code=401,
            detail="Refresh Token 已注销"
        )

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
@router.post("/logout")
def logout(data:LogoutRequest,
           credentials:HTTPAuthorizationCredentials = Depends(security)):
    access_token=credentials.credentials
    access_payload=decode_access_token(access_token)
    if access_payload is not None:
        remaining_time=get_remaining_time(access_payload)

        if remaining_time>0:
            redis_service.add_blacklist(
                f"blacklist:access:{access_token}",
                remaining_time
            )
    refresh_token=data.refresh_token
    refresh_payload=decode_refresh_token(refresh_token)
    if refresh_payload is not None:
        remaining_time=get_remaining_time(refresh_payload)

        if remaining_time>0:
            redis_service.add_blacklist(
                f"blacklist:refresh:{refresh_token}",
                remaining_time
            )
        
    return{


        "message":"退出登录成功"
    }


