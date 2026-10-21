import logging
import httpx

from openai import AsyncOpenAI

from agent.agent import Agent

from config import settings
from prompt.system_prompt import SYSTEM_PROMPT


logger = logging.getLogger(__name__)


# ==========================
# LLM Client
# ==========================

client = AsyncOpenAI(

    api_key=settings.SILICON_API_KEY,

    base_url="https://api.siliconflow.cn/v1",

    timeout=httpx.Timeout(
        connect=30.0,
        read=300.0,
        write=30.0,
        pool=30.0
    ),

    max_retries=3
)



# ==========================
# Agent 初始化
# ==========================

agent = Agent(

    llm_client=client,

    model="deepseek-ai/DeepSeek-V3"

)



# ==========================
# Agent Chat
# ==========================

async def chat_ai_stream(
        message: str,
        history: list
):

    try:

        logger.info(
            f"用户问题: {message}"
        )
        logger.info(
    f"Chat接收到History: "
    f"history_count={len(history)}"
)


        # ==========================
        # 构造 Agent messages
        # ==========================

        messages = [

            {
                "role": "system",
                "content": SYSTEM_PROMPT
            },


            *history,


            {
                "role": "user",
                "content": message
            }

        ]


        logger.info(
    f"Agent Context构建完成: "
    f"history_count={len(history)}, "
    f"context_message_count={len(messages)}"
)


       
        # ==========================
        # 调用 Agent：真正的流式输出
        # ==========================

        full_answer = []

        async for text in agent.run_stream(
            messages=messages
        ):
            if not text:
                continue

            full_answer.append(text)

            # 逐段交给上层路由
            yield text

        logger.info(
            "Agent流式回答完成: 长度=%s",
            len("".join(full_answer))
        )



    except httpx.ReadTimeout:


        logger.error(

            "DeepSeek响应超时"

        )


        yield "\n[模型响应超时，请稍后重试]"



    except Exception as e:


        logger.exception(

            f"Agent chat error: {e}"

        )


        raise