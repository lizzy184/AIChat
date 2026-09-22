from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session

from database.database import get_db
from database.models import User

from utils.security import decode_access_token


# =========================================================
# HTTP Bearer 认证
# =========================================================

security = HTTPBearer()


# =========================================================
# 获取当前登录用户
# =========================================================

def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db)
):
    """
    从请求头中获取：

        Authorization: Bearer <access_token>

    然后解析 JWT，获取 user.id。
    """

    # =====================================================
    # 1. 获取 Bearer Token
    # =====================================================

    token = credentials.credentials

    # =====================================================
    # 2. 解码 Access Token
    # =====================================================

    payload = decode_access_token(token)

    if payload is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Access Token 无效或已过期",
            headers={
                "WWW-Authenticate": "Bearer"
            }
        )

    # =====================================================
    # 3. 获取 sub
    #
    # 你的 login 里面：
    #
    # create_access_token({
    #     "sub": str(user.id)
    # })
    #
    # 所以这里的 sub 就是 user.id
    # =====================================================

    user_id = payload.get("sub")

    if user_id is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token 中缺少用户信息",
            headers={
                "WWW-Authenticate": "Bearer"
            }
        )

    # =====================================================
    # 4. 查询用户
    # =====================================================

    try:
        user_id = int(user_id)

    except (TypeError, ValueError):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token 中的用户ID无效",
            headers={
                "WWW-Authenticate": "Bearer"
            }
        )

    user = (
        db.query(User)
        .filter(User.id == user_id)
        .first()
    )

    # =====================================================
    # 5. 用户不存在
    # =====================================================

    if user is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="用户不存在",
            headers={
                "WWW-Authenticate": "Bearer"
            }
        )

    # =====================================================
    # 6. 返回当前用户
    # =====================================================

    return user