from pathlib import Path

from fastapi import (
    APIRouter,
    UploadFile,
    File,
    HTTPException
)

from rag.loader import load_pdf
from rag.splitter import split_pages
from rag.build_index import build_index
from rag.rag_service import retriever


router = APIRouter()


# ==========================================
# PDF 保存目录
# ==========================================

BASE_DIR = Path(__file__).resolve().parent.parent

DOCUMENTS_DIR = (
    BASE_DIR / "rag" / "documents"
)

DOCUMENTS_DIR.mkdir(
    parents=True,
    exist_ok=True
)


@router.post("/documents/upload")
async def upload_document(
    file: UploadFile = File(...)
):

    # ==========================================
    # 1. 检查文件名
    # ==========================================

    if not file.filename:

        raise HTTPException(
            status_code=400,
            detail="No file provided"
        )


    # ==========================================
    # 2. 检查 PDF
    # ==========================================

    if not file.filename.lower().endswith(".pdf"):

        raise HTTPException(
            status_code=400,
            detail="Only PDF files are allowed"
        )


    # ==========================================
    # 3. 读取文件
    # ==========================================

    content = await file.read()


    if not content:

        raise HTTPException(
            status_code=400,
            detail="Uploaded file is empty"
        )


    # ==========================================
    # 4. 防止文件名带路径
    # ==========================================

    filename = Path(
        file.filename
    ).name


    file_path = (
        DOCUMENTS_DIR / filename
    )


    # ==========================================
    # 5. 保存 PDF
    # ==========================================

    try:

        with open(
            file_path,
            "wb"
        ) as buffer:

            buffer.write(content)

    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Failed to save file: {str(e)}"
        )


    # ==========================================
    # 6. 先解析一次
    #    用来得到页数和 chunk 数量
    # ==========================================

    try:

        pages = load_pdf(
            file_path
        )

        chunks = split_pages(
            pages
        )

    except Exception as e:

        raise HTTPException(
            status_code=400,
            detail=f"Failed to parse PDF: {str(e)}"
        )


    # ==========================================
    # 7. 重新建立整个向量索引
    # ==========================================

    try:

        print(
            f"开始为 {filename} 建立 RAG 索引..."
        )

        await build_index()


    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Failed to build index: {str(e)}"
        )


    # ==========================================
    # 8. 重新加载 Retriever
    # ==========================================

    try:

        retriever.reload()

    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Failed to reload retriever: {str(e)}"
        )


    # ==========================================
    # 9. 返回结果
    # ==========================================

    return {

        "filename": filename,

        "content_type": (
            file.content_type
            or "application/pdf"
        ),

        "size": len(content),

        "pages": len(pages),

        "chunks": len(chunks),

        "status": "indexed"

    }