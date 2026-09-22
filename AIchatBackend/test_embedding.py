import asyncio

from rag.embedding import embed_text


async def main():
    text = "JVM 是 Java 虚拟机，用于运行 Java 字节码。"

    vector = await embed_text(text)

    print("向量维度：", len(vector))
    print("前 10 个值：")
    print(vector[:10])


if __name__ == "__main__":
    asyncio.run(main())