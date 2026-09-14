from fastapi import FastAPI
from routers import chat

from database.database import engine
from database.database import Base
from routers import history
from routers import user
# 非常重要
# 导入模型，让SQLAlchemy知道有哪些表
from database.models import Message
from utils.logger import logger



# 创建数据库和表
logger.info("==========开始创建数据库==========")





Base.metadata.create_all(bind=engine)


logger.info("==========数据库创建完成==========")



app = FastAPI()
app.include_router(
    chat.router
)
app.include_router(
    history.router
)
app.include_router(
    user.router
)
@app.get("/")
def root():

    return {
        "message": "backend running"
    }