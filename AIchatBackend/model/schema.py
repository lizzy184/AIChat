from pydantic import BaseModel


class ChatRequest(BaseModel):
    
    message: str


class ChatResponse(BaseModel):
    reply: str
class RegisterRequest(BaseModel):

    username:str
    password:str
class LoginRequest(BaseModel):

    username:str
    password:str
class RefreshRequest(BaseModel):
    refresh_token: str
class TokenResponse(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str = "bearer"
class RefreshResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"