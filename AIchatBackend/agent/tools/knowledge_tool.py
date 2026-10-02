from rag.retriever import Retriever
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent.parent.parent / "rag"


retriever = Retriever(
    index_file=BASE_DIR / "rag_index.json",
    vector_file=BASE_DIR / "vectors.npy"
)


async def search_knowledge_base(query: str):
    """
    Search the project knowledge base.
    """

    # 1. 空查询
    if not query or not query.strip():

        return {
            "success": False,
            "error": "empty query"
        }


    try:

        # 2. RAG检索
        results = await retriever.search(
            query=query,
            top_k=3,
            threshold=0.5
        )


        # 3. 没有结果
        if not results:

            return {
                "success": False,
                "error": "No relevant documents found"
            }


        # 4. 成功
        return {
            "success": True,
            "results": results
        }


    except Exception as e:

        return {
            "success": False,
            "error": f"knowledge search failed: {str(e)}"
        }



KNOWLEDGE_BASE_TOOL = {
    "type": "function",
    "function": {
        "name": "search_knowledge_base",

        "description": (
            "Search the project knowledge base for "
            "project-specific technical information."
        ),

        "parameters": {

            "type": "object",

            "properties": {

                "query": {

                    "type": "string",

                    "description":
                    "The search query"
                }
            },

            "required": [
                "query"
            ]
        }
    }
}