from utils.logger import logger

from agent.tools.calculator_tool import calculator
from agent.tools.knowledge_tool import search_knowledge_base



async def execute_tool(
    tool_name: str,
    arguments: dict,
):
    """
    Tool dispatcher.

    Responsibility:

    1. Receive tool name from Tool Node
    2. Validate arguments
    3. Execute real tool
    4. Return structured result

    This function should never crash Agent.
    """


    logger.info(
        f"[TOOL] Start execution: {tool_name}"
    )


    try:

        # ==================================================
        # 1. Knowledge Base Tool
        # ==================================================

        if tool_name == "search_knowledge_base":


            query = arguments.get(
                "query"
            )


            logger.info(
                f"[TOOL] Query: {query}"
            )


            if not query:


                logger.warning(
                    "[TOOL] Missing query parameter"
                )


                return {

                    "success": False,

                    "error": {

                        "code": "INVALID_ARGUMENT",

                        "message":
                        "Missing query parameter"

                    }

                }



            result = await search_knowledge_base(
                query=query
            )


            if result.get("success"):


                logger.info(
                    "[TOOL] Knowledge search success"
                )


            else:


                logger.warning(
                    f"[TOOL] Knowledge search failed: {result}"
                )


            return result



        # ==================================================
        # 2. Calculator Tool
        # ==================================================

        elif tool_name == "calculator":


            expression = arguments.get(
                "expression"
            )


            logger.info(
                f"[TOOL] Expression: {expression}"
            )



            if expression is None:


                logger.warning(
                    "[TOOL] Missing expression"
                )


                return {


                    "success": False,


                    "error": {


                        "code":
                        "INVALID_ARGUMENT",


                        "message":
                        "Missing expression parameter"

                    }

                }



            result = calculator(
                expression=expression
            )



            if result.get("success"):


                logger.info(
                    "[TOOL] Calculator success"
                )


            else:


                logger.warning(
                    f"[TOOL] Calculator failed: {result}"
                )



            return result




        # ==================================================
        # 3. Unknown Tool
        # ==================================================

        else:


            logger.warning(
                f"[TOOL] Unknown tool: {tool_name}"
            )


            return {


                "success": False,


                "error": {


                    "code":
                    "UNKNOWN_TOOL",


                    "message":
                    f"Unknown tool: {tool_name}"

                }

            }



    except Exception as e:


        logger.exception(
            f"[TOOL] Execution exception: {tool_name}"
        )


        return {


            "success": False,


            "error": {


                "code":
                "TOOL_EXECUTION_FAILED",


                "message":
                str(e)

            }

        }