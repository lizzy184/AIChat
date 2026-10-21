
from agent.state import AgentState

from agent.graph import create_graph

from agent.tools import (
    KNOWLEDGE_BASE_TOOL,
    CALCULATOR_TOOL,
)


class Agent:

    def __init__(
        self,
        llm_client,
        model
    ):
        self.llm_client = llm_client
        self.model = model

        self.tools = [
            KNOWLEDGE_BASE_TOOL,
            CALCULATOR_TOOL
        ]

        # ==================================
        # Create LangGraph workflow
        # ==================================

        self.graph = create_graph(
            llm_client=self.llm_client,
            model=self.model,
            tools=self.tools
        )

    async def run(
        self,
        messages: list,
    ):
        state = {
            "messages": messages
        }

        result = await self.graph.ainvoke(
            state
        )

        return result["messages"][-1].content

    async def run_stream(
        self,
        messages: list,
    ):
        state = {
            "messages": messages
        }

        async for event in self.graph.astream(
            state,
            stream_mode="custom",
        ):
            if not isinstance(event, dict):
                continue

            if event.get("type") == "token":
                content = event.get("content")

                if content:
                    yield content

