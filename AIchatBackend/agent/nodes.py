import json

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


        logger.info(
            "[NODE] Agent node start"
        )


        try:


            response = await (
                llm_client
                .chat
                .completions
                .create(

                    model=model,

                    messages=state["messages"],

                    tools=tools,

                    tool_choice="auto"

                )
            )


        except Exception as e:


            logger.error(
                f"[NODE] LLM failed: {e}"
            )


            return {

                "messages":[

                    {
                        "role":"assistant",

                        "content":
                        (
                            "LLM request failed: "
                            f"{str(e)}"
                        )
                    }

                ]

            }



        message = response.choices[0].message



        logger.info(
            (
                "[NODE] Tool calls: "
                f"{message.tool_calls}"
            )
        )


        return {

            "messages":[

                message

            ]

        }



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

            "messages":[]

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

                "success":False,

                "error":{

                    "code":
                    "INVALID_ARGUMENT",

                    "message":
                    str(e)

                }

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



                if tool_result.get(
                    "success"
                ):


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

                    "success":False,

                    "error":{

                        "code":
                        "TOOL_EXECUTION_FAILED",

                        "message":
                        str(e)

                    }

                }



        # ------------------------------
        # Convert to Tool Message
        # ------------------------------

        tool_messages.append(

            {

                "role":"tool",

                "tool_call_id":
                tool_call.id,


                "content":
                json.dumps(

                    tool_result,

                    ensure_ascii=False

                )

            }

        )



    logger.info(
        "[TOOL NODE] Finished"
    )



    return {

        "messages":
        tool_messages

    }