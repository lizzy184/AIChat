from dotenv import load_dotenv
import os


load_dotenv()


class Settings:

    SILICON_API_KEY = os.getenv(
        "SILICON_API_KEY"
    )


settings = Settings()