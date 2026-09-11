from passlib.context import CryptContext
from datetime import datetime, timedelta, timezone
import jwt
from config import settings


print("SECURITY FILE:", __file__)
pwd_context = CryptContext(
    schemes=["bcrypt"],
    deprecated="auto"
)



def hash_password(password:str):

    return pwd_context.hash(password)



def verify_password(
        plain_password,
        hashed_password
):

    return pwd_context.verify(
        plain_password,
        hashed_password
    )
def create_access_token(data: dict):
    to_encode = data.copy()

    expire = datetime.now(timezone.utc) + timedelta(
        minutes=settings.JWT_EXPIRE_MINUTES
    )

    to_encode.update({
        "exp": expire
    })

    return jwt.encode(
        to_encode,
        settings.JWT_SECRET_KEY,
        algorithm=settings.JWT_ALGORITHM
    )

def create_refresh_token(data: dict):
    to_encode = data.copy()

    expire = datetime.now(timezone.utc) + timedelta(
        days=settings.REFRESH_TOKEN_EXPIRE_DAYS
    )

    to_encode.update({
        "exp": expire,
        "type": "refresh"
    })

    return jwt.encode(
        to_encode,
        settings.JWT_SECRET_KEY,
        algorithm=settings.JWT_ALGORITHM
    )





def decode_access_token(token: str):
    return jwt.decode(
        token,
        settings.JWT_SECRET_KEY,
        algorithms=[settings.JWT_ALGORITHM]
    )

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

