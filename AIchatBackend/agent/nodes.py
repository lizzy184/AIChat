
import json
import time

from openai.types.chat import ChatCompletionMessage
from openai.types.chat.chat_completion_message_function_tool_call import (
    ChatCompletionMessageFunctionToolCall,
    Function,
)
from langgraph.config import get_stream_writer

from utils.logger import logger
from agent.tools.executor import execute_tool


# ==================================================
# Agent Node
# ==================================================

def create_agent_node(
    llm_client,
    model,
    tools,
):
    async def agent_node(state):
        logger.info("[NODE] Agent node start")

        # 获取 LangGraph 自定义流式事件发送器
        writer = get_stream_writer()

        # 保存模型逐段生成的普通文本
        content_parts = []

        # 保存分段返回的工具调用信息
        tool_call_parts = {}

        # 流式诊断：记录开始时间和首个文本片段状态
        stream_start = time.monotonic()
        first_token_logged = False

        try:
            stream = await llm_client.chat.completions.create(
                model=model,
                messages=state["messages"],
                tools=tools,
                tool_choice="auto",
                stream=True,
            )

            logger.info("[STREAM] Model stream started")

            async for chunk in stream:
                if not chunk.choices:
                    continue

                delta = chunk.choices[0].delta

                # 普通文本：收集并立即向外发送
                if delta.content:
                    # 记录第一个文本片段到达的时间
                    if not first_token_logged:
                        elapsed = time.monotonic() - stream_start

                        logger.info(
                            "[STREAM] First token after %.3fs",
                            elapsed,
                        )

                        first_token_logged = True

                    # 记录每个文本片段的到达时间和长度
                    elapsed = time.monotonic() - stream_start

                    logger.info(
                        "[STREAM] t=+%.3fs, chunk_length=%d, content=%r",
                        elapsed,
                        len(delta.content),
                        delta.content[:30],
                    )

                    content_parts.append(delta.content)

                    # 通过 LangGraph custom stream 发布文本片段
                    writer({
                        "type": "token",
                        "content": delta.content,
                    })

                # 工具调用：逐段拼接名称、ID 和参数
                if delta.tool_calls:
                    for tool_delta in delta.tool_calls:
                        index = tool_delta.index

                        item = tool_call_parts.setdefault(
                            index,
                            {
                                "id": "",
                                "name": "",
                                "arguments": "",
                            },
                        )

                        if tool_delta.id:
                            item["id"] = tool_delta.id

                        if tool_delta.function:
                            if tool_delta.function.name:
                                item["name"] += tool_delta.function.name

                            if tool_delta.function.arguments:
                                item["arguments"] += (
                                    tool_delta.function.arguments
                                )

            total_elapsed = time.monotonic() - stream_start

            logger.info(
                "[STREAM] Model stream finished after %.3fs; "
                "text_length=%d; tool_call_count=%d",
                total_elapsed,
                sum(len(part) for part in content_parts),
                len(tool_call_parts),
            )

            if not first_token_logged and not tool_call_parts:
                logger.warning(
                    "[STREAM] Model finished without text or tool calls"
                )

        except Exception as e:
            logger.exception("[NODE] LLM failed: %s", e)

            error_text = f"LLM request failed: {str(e)}"

            # 保留原有行为：将错误文本作为 assistant 消息返回。
            # 注意：如果前面已经发出部分文本，这里会产生混合内容。
            writer({
                "type": "token",
                "content": error_text,
            })

            return {
                "messages": [
                    ChatCompletionMessage(
                        role="assistant",
                        content=error_text,
                    )
                ]
            }

        # 把分段收集的工具调用重新组装成完整对象
        tool_calls = [
            ChatCompletionMessageFunctionToolCall(
                id=item["id"],
                type="function",
                function=Function(
                    name=item["name"],
                    arguments=item["arguments"],
                ),
            )
            for _, item in sorted(tool_call_parts.items())
        ]

        # 返回完整的 Assistant 消息，供 LangGraph 判断是否调用工具
        message = ChatCompletionMessage(
            role="assistant",
            content="".join(content_parts) or None,
            tool_calls=tool_calls or None,
        )

        logger.info("[NODE] Tool calls: %s", message.tool_calls)

        return {"messages": [message]}

    return agent_node


# ==================================================
# Tool Node
# ==================================================

async def tool_node(state):

    logger.info(
        "[NODE] Tool node start"
    )

    last_message = (
        state["messages"][-1]
    )

    tool_calls = (
        last_message.tool_calls
    )

    if not tool_calls:
        logger.warning(
            "[TOOL NODE] No tool calls found"
        )

        return {
            "messages": []
        }

    tool_messages = []

    for tool_call in tool_calls:

        tool_name = (
            tool_call
            .function
            .name
        )

        logger.info(
            f"[TOOL] Selected: {tool_name}"
        )

        # ------------------------------
        # Parse arguments
        # ------------------------------

        try:
            arguments = json.loads(
                tool_call
                .function
                .arguments
            )

            logger.info(
                f"[TOOL] Arguments: {arguments}"
            )

        except Exception as e:
            logger.error(
                (
                    "[TOOL] "
                    "Argument parsing failed: "
                    f"{e}"
                )
            )

            tool_result = {
                "success": False,
                "error": {
                    "code": "INVALID_ARGUMENT",
                    "message": str(e),
                },
            }

        else:
            # ------------------------------
            # Execute Tool
            # ------------------------------

            try:
                logger.info(
                    (
                        "[TOOL] "
                        f"Executing: {tool_name}"
                    )
                )

                tool_result = await execute_tool(
                    tool_name=tool_name,
                    arguments=arguments
                )

                if tool_result.get("success"):
                    logger.info(
                        (
                            "[TOOL] "
                            f"Success: {tool_name}"
                        )
                    )
                else:
                    logger.warning(
                        (
                            "[TOOL] "
                            f"Failed: {tool_name}"
                        )
                    )

            except Exception as e:
                logger.error(
                    (
                        "[TOOL] "
                        f"Execution error: {e}"
                    )
                )

                tool_result = {
                    "success": False,
                    "error": {
                        "code": "TOOL_EXECUTION_FAILED",
                        "message": str(e),
                    },
                }

        # ------------------------------
        # Convert to Tool Message
        # ------------------------------

        tool_messages.append(
            {
                "role": "tool",
                "tool_call_id": tool_call.id,
                "content": json.dumps(
                    tool_result,
                    ensure_ascii=False
                )
            }
        )

    logger.info(
        "[TOOL NODE] Finished"
    )

    return {
        "messages": tool_messages
    }
