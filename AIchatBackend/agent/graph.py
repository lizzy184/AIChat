from langgraph.graph import (
    StateGraph,
    END,
)


from agent.state import AgentState


from agent.nodes import (
    create_agent_node,
    tool_node,
)



def should_continue(
    state: AgentState
):

    """
    Decide next node.

    agent
       |
       |
    tool_call?
       |
    yes -> tool
    no  -> END
    """


    last_message = (
        state["messages"][-1]
    )


    if (
        hasattr(
            last_message,
            "tool_calls"
        )
        and last_message.tool_calls
    ):

        return "tool"


    return "end"




def create_graph(
    llm_client,
    model,
    tools,
):


    # =====================================
    # Create nodes
    # =====================================


    agent_node = create_agent_node(
        llm_client=llm_client,
        model=model,
        tools=tools,
    )



    workflow = StateGraph(
        AgentState
    )



    # =====================================
    # Register nodes
    # =====================================


    workflow.add_node(
        "agent",
        agent_node
    )


    workflow.add_node(
        "tool",
        tool_node
    )



    # =====================================
    # START
    # =====================================


    workflow.set_entry_point(
        "agent"
    )



    # =====================================
    # Conditional Edge
    #
    # agent
    #
    #    |
    #
    # tool ?
    #
    # yes -> tool
    #
    # no  -> END
    #
    # =====================================


    workflow.add_conditional_edges(

        "agent",

        should_continue,

        {

            "tool":"tool",

            "end":END,

        }

    )



    # =====================================
    # Tool finished
    #
    # tool
    #   |
    #   |
    # agent
    #
    # =====================================


    workflow.add_edge(

        "tool",

        "agent"

    )



    return workflow.compile()