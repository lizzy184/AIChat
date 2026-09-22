import logging
import httpx

from openai import AsyncOpenAI

from config import settings
from prompt.system_prompt import SYSTEM_PROMPT
from rag.rag_service import rag_chat


logger = logging.getLogger(__name__)


# DeepSeek / SiliconFlow client
client = AsyncOpenAI(

    api_key=settings.SILICON_API_KEY,

    base_url="https://api.siliconflow.cn/v1",

    timeout=httpx.Timeout(
        connect=30.0,   # 建立连接时间
        read=300.0,     # 等待模型返回时间
        write=30.0,
        pool=30.0
    ),

    max_retries=3
)


async def chat_ai_stream(
        message: str,
        history: list
):

    try:

        logger.info(
            f"用户问题: {message}"
        )


        # ==========================
        # RAG 检索
        # ==========================

        rag_result = await rag_chat(message)


        rag_prompt = rag_result["prompt"]


        logger.info(
            f"RAG prompt长度: {len(rag_prompt)}"
        )


        messages = [

            {
                "role": "system",
                "content": SYSTEM_PROMPT
            },

            *history,

            {
                "role": "user",
                "content": rag_prompt
            }

        ]


        logger.info(
            f"发送给模型消息数量: {len(messages)}"
        )


        # ==========================
        # DeepSeek Streaming
        # ==========================

        response = await client.chat.completions.create(

            model="deepseek-ai/DeepSeek-V3",

            messages=messages,


            max_tokens=2000,

            temperature=0.7,


            stream=True,

            stream_options={
                "include_usage": True
            }

        )


        logger.info(
            "DeepSeek连接成功，开始输出"
        )


        async for chunk in response:


            # 最后一个usage chunk
            if not chunk.choices:
                continue


            delta = chunk.choices[0].delta


            content = delta.content


            if not content:
                continue


            logger.debug(
                f"token: {content}"
            )


            yield content



    except httpx.ReadTimeout:


        logger.error(
            "DeepSeek响应超时"
        )

        yield "\n[模型响应超时，请稍后重试]"



    except Exception as e:


        logger.exception(
            f"DeepSeek stream error: {e}"
        )

        raise