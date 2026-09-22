from sqlalchemy.orm import Session

from database.models import Message



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


        messages.reverse()


        return [


            {

            "role":msg.role,

            "content":msg.content

            }


            for msg in messages

        ]
