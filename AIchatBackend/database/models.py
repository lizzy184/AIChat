from sqlalchemy import Column
from sqlalchemy import Integer
from sqlalchemy import String
from sqlalchemy import DateTime
from sqlalchemy import Text
from sqlalchemy.sql import func
from sqlalchemy import ForeignKey
from database.database import Base
from datetime import datetime


class Message(Base):

    __tablename__="messages"

    conversation_id = Column(

    Integer,

    ForeignKey(
        "conversations.id"
    ),

    nullable=False

)
    id = Column(
          Integer,
          primary_key=True,
          index=True
    )


    user_id = Column(
        Integer,
        ForeignKey("users.id"),
        nullable=False
      
    )


    role = Column(
        String,
        nullable=False
    )


    content = Column(
         Text,
         nullable=False
    )


    created_time = Column(
        DateTime,
        default=datetime.utcnow
    )
class User(Base):
    __tablename__="users"
    id = Column(Integer, primary_key=True)


    username = Column(
        String(50),
        unique=True,
        nullable=False
    )


    password_hash = Column(
        String(255),
        nullable=False
    )


    created_time = Column(
        DateTime,
        default=datetime.utcnow
    )
class Conversation(Base):

    __tablename__="conversations"


    id = Column(
        Integer,
        primary_key=True,
        index=True
    )


    user_id = Column(
        Integer,
        ForeignKey("users.id"),
        nullable=False
    )


    title = Column(
        String(100),
        default="新聊天"
    )


    created_time = Column(
        DateTime,
        default=datetime.utcnow
    )
    updated_time = Column(
    DateTime,
    default=datetime.utcnow,
    onupdate=datetime.utcnow
    ) 