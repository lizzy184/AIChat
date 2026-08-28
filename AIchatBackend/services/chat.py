import logging

from openai import AsyncOpenAI

from config import settings


logger = logging.getLogger(__name__)


client = AsyncOpenAI(
    api_key=settings.SILICON_API_KEY,
    base_url="https://api.siliconflow.cn/v1"
)


async def chat_ai(message: str):

    try:

        response = await client.chat.completions.create(

            model="deepseek-ai/DeepSeek-V3",

            messages=[
                {
                    "role": "user",
                    "content": message
                }
            ],

            max_tokens=500
        )


        return response.choices[0].message.content


    except Exception as e:

        logger.error(
            f"AI调用失败:{e}"
        )

        raise