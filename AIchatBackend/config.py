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
