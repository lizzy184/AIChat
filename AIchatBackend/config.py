from dotenv import load_dotenv
import os


load_dotenv()


class Settings:

    SILICON_API_KEY = os.getenv(
        "SILICON_API_KEY"
    )
    JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY")
    JWT_ALGORITHM = os.getenv("JWT_ALGORITHM", "HS256")
    JWT_EXPIRE_MINUTES = int(
    os.getenv("JWT_EXPIRE_MINUTES", "15")
)

    REFRESH_TOKEN_EXPIRE_DAYS = int(
    os.getenv("REFRESH_TOKEN_EXPIRE_DAYS", "7")
)
settings = Settings()
print(
    "JWT_SECRET_KEY type:",
    type(settings.JWT_SECRET_KEY).__name__
)

print(
    "JWT_SECRET_KEY exists:",
    bool(settings.JWT_SECRET_KEY)
)

print(
    "JWT_SECRET_KEY length:",
    len(settings.JWT_SECRET_KEY)
    if settings.JWT_SECRET_KEY
    else None
)

print(
    "JWT_ALGORITHM:",
    settings.JWT_ALGORITHM
)

print(
    "JWT_EXPIRE_MINUTES:",
    settings.JWT_EXPIRE_MINUTES
)

print(
    "REFRESH_TOKEN_EXPIRE_DAYS:",
    settings.REFRESH_TOKEN_EXPIRE_DAYS
)