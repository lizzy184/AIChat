from pydantic import BaseModel


class ChatRequest(BaseModel):
    user_id:int
    message: str


class ChatResponse(BaseModel):
    reply: str
class RegisterRequest(BaseModel):

    username:str
    password:str
class LoginRequest(BaseModel):

    username:str
    password:str