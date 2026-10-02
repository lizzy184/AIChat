from sqlalchemy.orm import Session
import logging
from database.models import Message
logger = logging.getLogger(__name__)


class MemoryService:


    def __init__(
        self,
        db:Session
    ):

        self.db=db



    def get_history(
        self,
        conversation_id:int,
        limit:int=20
    ):
        logger.info(
    f"Memory开始获取历史消息: "
    f"conversation_id={conversation_id}, "
    f"limit={limit}"
)


        messages=(


            self.db.query(Message)


            .filter(

                Message.conversation_id
                ==
                conversation_id

            )


            .order_by(

                Message.id.desc()

            )


            .limit(limit)


            .all()

        )
        logger.info(
    f"Memory获取历史消息完成: "
    f"conversation_id={conversation_id}, "
    f"history_count={len(messages)}"
)


        messages.reverse()


        return [


            {

            "role":msg.role,

            "content":msg.content

            }


            for msg in messages

        ]
