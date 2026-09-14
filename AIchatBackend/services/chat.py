import logging

from openai import AsyncOpenAI

from config import settings


logger = logging.getLogger(__name__)


client = AsyncOpenAI(
    api_key=settings.SILICON_API_KEY,
    base_url="https://api.siliconflow.cn/v1"
)


async def chat_ai_stream(message: str):

    try:

        response = await client.chat.completions.create(

            model="deepseek-ai/DeepSeek-V3",

            messages=[
                {
                    "role": "user",
                    "content": message
                }
            ],

            max_tokens=500,
            stream=True
        )
        async for chunk in response:
            content = chunk.choices[0].delta.content
            if content:
                yield content
        


    except Exception as e:

        logger.exception(
            f"AI调用失败:{e}"
        )

        raise