from passlib.context import CryptContext
from datetime import datetime, timedelta, timezone

import jwt

from config import settings


pwd_context = CryptContext(
    schemes=["bcrypt"],
    deprecated="auto"
)


def hash_password(password: str):
    return pwd_context.hash(password)


def verify_password(
    plain_password,
    hashed_password
):
    return pwd_context.verify(
        plain_password,
        hashed_password
    )


# =========================================================
# Access Token
# =========================================================

def create_access_token(data: dict):
    to_encode = data.copy()

    now = datetime.now(timezone.utc)

    expire = now + timedelta(
        minutes=settings.JWT_EXPIRE_MINUTES
    )

    to_encode.update({
        "iat": now,
        "exp": expire
    })

    return jwt.encode(
        to_encode,
        settings.JWT_SECRET_KEY,
        algorithm=settings.JWT_ALGORITHM
    )


# =========================================================
# Refresh Token
# =========================================================

def create_refresh_token(data: dict):
    to_encode = data.copy()

    now = datetime.now(timezone.utc)

    expire = now + timedelta(
        days=settings.REFRESH_TOKEN_EXPIRE_DAYS
    )

    to_encode.update({
        "iat": now,
        "exp": expire,
        "type": "refresh"
    })

    return jwt.encode(
        to_encode,
        settings.JWT_SECRET_KEY,
        algorithm=settings.JWT_ALGORITHM
    )


# =========================================================
# Decode Access Token
# =========================================================

def decode_access_token(token: str):
    try:
        return jwt.decode(
            token,
            settings.JWT_SECRET_KEY,
            algorithms=[settings.JWT_ALGORITHM]
        )

    except jwt.ExpiredSignatureError:
        return None

    except jwt.PyJWTError:
        return None


# =========================================================
# Decode Refresh Token
# =========================================================

def decode_refresh_token(token: str):
    try:
        payload = jwt.decode(
            token,
            settings.JWT_SECRET_KEY,
            algorithms=[settings.JWT_ALGORITHM]
        )

        if payload.get("type") != "refresh":
            return None

        return payload

    except jwt.PyJWTError:
        return None