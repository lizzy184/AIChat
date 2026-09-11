# AIChat

> 一个基于 Android Compose + FastAPI + DeepSeek 的前后端分离 AI 聊天应用。

AIChat 是一个前后端分离的 AI 聊天项目。

项目使用 Android 原生技术开发移动端，使用 Kotlin + Jetpack Compose 构建 UI，采用 MVVM 架构组织 Android 客户端；后端使用 Python FastAPI 构建 REST API，并通过 DeepSeek API 提供 AI 对话能力。

项目目前已经实现用户注册、登录、JWT 身份认证、Access Token / Refresh Token、Access Token 自动刷新、AI 对话、聊天历史管理以及 Docker 容器化等功能。

项目主要用于学习和实践：

- Android 开发
- Jetpack Compose
- MVVM 架构
- Kotlin Coroutines
- Retrofit 网络请求
- Room 本地数据库
- DataStore 本地数据存储
- Hilt / Dependency Injection
- JWT 身份认证
- Access Token / Refresh Token
- FastAPI 后端开发
- Pydantic 数据校验
- SQLAlchemy ORM
- SQLite
- DeepSeek API
- Docker 容器化
- Git / GitHub

---

# 📱 项目简介

AIChat 是一个 Android AI 聊天应用。

用户可以通过 Android 客户端进行：

- 用户注册
- 用户登录
- JWT 身份认证
- AI 对话
- 查看聊天历史
- 清空聊天历史
- 用户退出登录

项目整体采用前后端分离架构：

```text
┌─────────────────────────────┐
│        Android App          │
│                             │
│ Kotlin + Jetpack Compose    │
│ MVVM + Coroutines           │
│ Retrofit + Room             │
│ DataStore + JWT             │
└──────────────┬──────────────┘
               │
               │ HTTP
               ↓
┌─────────────────────────────┐
│       FastAPI Backend       │
│                             │
│ Python + FastAPI            │
│ Pydantic + SQLAlchemy       │
│ JWT Authentication          │
│ 用户认证 + 聊天接口          │
└──────────────┬──────────────┘
               │
               ↓
┌─────────────────────────────┐
│         DeepSeek API        │
│                             │
│          AI 对话服务         │
└─────────────────────────────┘
✨ 项目功能
Android 客户端
用户相关
 用户注册
 用户登录
 JWT 身份认证
 Access Token
 Refresh Token
 Access Token 自动刷新
 登录状态保存
 用户退出登录
 启动页面
 登录页面
 注册页面
AI 聊天
 AI 对话
 用户消息展示
 AI 消息展示
 消息加载状态
 空聊天状态
 聊天输入框
 聊天消息列表
 网络请求状态处理
 JWT Token 自动认证
聊天记录
 聊天记录本地保存
 聊天历史查看
 历史消息展示
 清空聊天历史
设置
 设置页面
 关于页面
 用户相关设置
🏗️ 项目架构

整个项目采用前后端分离架构。

                         AIChat
                            │
              ┌─────────────┴─────────────┐
              │                           │
              ↓                           ↓
       AIchatAndroid                AIchatBackend
          Android                     FastAPI
              │                           │
              │                           │
              │          HTTP             │
              └──────────────────────────→│
                                          │
                                          ↓
                                    DeepSeek API
📂 项目结构
project/
│
├── AIchatAndroid/
│   │
│   ├── app/
│   │   └── src/
│   │       └── main/
│   │           └── java/
│   │               └── com/example/aichatapp/
│   │                   │
│   │                   ├── data/
│   │                   │   └── UserPreferences.kt
│   │                   │
│   │                   ├── database/
│   │                   │   ├── AppDatabase.kt
│   │                   │   ├── DatabaseModule.kt
│   │                   │   ├── MessageDao.kt
│   │                   │   └── MessageEntity.kt
│   │                   │
│   │                   ├── di/
│   │                   │   ├── NetworkModule.kt
│   │                   │   ├── RefreshClient.kt
│   │                   │   ├── RefreshRetrofit.kt
│   │                   │   └── TokenAuthenticator.kt
│   │                   │
│   │                   ├── model/
│   │                   │   ├── ChatRequest.kt
│   │                   │   ├── ChatResponse.kt
│   │                   │   ├── ChatUiState.kt
│   │                   │   ├── HistoryMessage.kt
│   │                   │   ├── LoginRequest.kt
│   │                   │   ├── LoginResponse.kt
│   │                   │   ├── Message.kt
│   │                   │   ├── RefreshRequest.kt
│   │                   │   ├── RefreshResponse.kt
│   │                   │   ├── RegisterRequest.kt
│   │                   │   └── RegisterResponse.kt
│   │                   │
│   │                   ├── navigation/
│   │                   │   ├── AppNavigation.kt
│   │                   │   ├── AppRoute.kt
│   │                   │   └── BottomNavigationBar.kt
│   │                   │
│   │                   ├── network/
│   │                   │   ├── AuthInterceptor.kt
│   │                   │   ├── ChatApi.kt
│   │                   │   ├── NetworkResult.kt
│   │                   │   └── RefreshApi.kt
│   │                   │
│   │                   ├── repository/
│   │                   │   └── ChatRepository.kt
│   │                   │
│   │                   ├── view/
│   │                   │   ├── chat/
│   │                   │   ├── components/
│   │                   │   ├── history/
│   │                   │   ├── login/
│   │                   │   ├── register/
│   │                   │   ├── setting/
│   │                   │   └── start/
│   │                   │
│   │                   └── viewmodel/
│   │                       ├── ChatViewModel.kt
│   │                       ├── HistoryViewModel.kt
│   │                       ├── LoginViewModel.kt
│   │                       └── RegisterViewModel.kt
│   │
│   ├── screenshots/
│   │
│   └── README.md
│
│
├── AIchatBackend/
│   │
│   ├── database/
│   │   ├── database.py
│   │   └── models.py
│   │
│   ├── model/
│   │   └── schema.py
│   │
│   ├── routers/
│   │   ├── chat.py
│   │   ├── history.py
│   │   └── user.py
│   │
│   ├── services/
│   │   └── chat.py
│   │
│   ├── utils/
│   │   ├── auth.py
│   │   └── security.py
│   │
│   ├── config.py
│   ├── main.py
│   ├── requirements.txt
│   ├── Dockerfile
│   └── README.md
│
│
└── README.md
🛠️ 技术栈
Android
技术	用途
Kotlin	Android 应用主要开发语言
Jetpack Compose	Android UI 开发
Material 3	UI 组件与设计
MVVM	Android 应用架构
Coroutines	异步任务与并发处理
ViewModel	管理 UI 状态
Retrofit	网络请求
OkHttp	HTTP 客户端、Interceptor、Authenticator
Room	本地数据库
Navigation	页面导航
DataStore / Preferences	Token 与用户状态本地保存
Hilt / Dependency Injection	依赖注入
Backend
技术	用途
Python	后端开发语言
FastAPI	Web API 框架
Pydantic	请求与响应数据校验
SQLAlchemy	ORM 数据库操作
SQLite	项目开发阶段数据库
Bcrypt	用户密码 Hash
PyJWT	JWT Token 生成与验证
Uvicorn	ASGI 服务器
Docker	后端容器化运行
🤖 AI

项目使用：

DeepSeek API

作为 AI 对话服务。

整体调用流程：

Android
   ↓
POST /chat
   ↓
FastAPI
   ↓
DeepSeek API
   ↓
AI Response
   ↓
FastAPI
   ↓
Android
🔐 JWT 身份认证

项目使用 JWT 实现用户身份认证。

登录成功后，Backend 返回：

Access Token
Refresh Token

Android 客户端通过 DataStore 保存 Token。

Access Token

Access Token 用于访问需要登录权限的 API。

例如：

POST /chat
GET /history
DELETE /history

请求时通过 HTTP Header 携带：

Authorization: Bearer <access_token>

Access Token 使用较短的有效期。

这样可以降低长期 Token 泄露后产生的风险。

Refresh Token

Refresh Token 用于获取新的 Access Token。

Refresh Token 不直接用于访问普通业务 API。

当 Access Token 过期后：

Android
   ↓
请求 /chat
   ↓
Authorization: Bearer <expired_access_token>
   ↓
FastAPI
   ↓
JWT 验证失败
   ↓
401 Unauthorized

Android 的 TokenAuthenticator 会自动处理 Token 刷新。

🔄 Access Token 自动刷新

Android 使用 OkHttp Interceptor 和 Authenticator 实现 JWT 自动认证和 Token 刷新。

整体流程：

┌─────────────────────────────┐
│       Android App           │
└──────────────┬──────────────┘
               │
               │ POST /chat
               ↓
      AuthInterceptor
               │
               │ Authorization:
               │ Bearer Access Token
               ↓
┌─────────────────────────────┐
│       FastAPI Backend       │
└──────────────┬──────────────┘
               │
               │ Access Token expired
               ↓
        401 Unauthorized
               │
               ↓
      TokenAuthenticator
               │
               │ Refresh Token
               ↓
         POST /refresh
               │
               ↓
┌─────────────────────────────┐
│       FastAPI Backend       │
│                             │
│ 验证 Refresh Token           │
│ 生成新的 Access Token        │
└──────────────┬──────────────┘
               │
               │ New Access Token
               ↓
      DataStore 保存
               │
               ↓
        自动重试原请求
               │
               ↓
          POST /chat
               │
               ↓
           200 OK
AuthInterceptor

AuthInterceptor 负责在普通 API 请求中自动添加 Access Token。

HTTP Request
      ↓
AuthInterceptor
      ↓
读取 DataStore 中的 Access Token
      ↓
Authorization: Bearer <token>
      ↓
发送请求
TokenAuthenticator

TokenAuthenticator 负责处理服务器返回的 401 Unauthorized。

主要流程：

收到 401
   ↓
检查当前请求是否已经重试
   ↓
读取当前 Access Token
   ↓
判断是否已经被其他请求刷新
   ↓
如果已经刷新
   ↓
直接使用新的 Access Token 重试

如果确实需要刷新：

读取 Refresh Token
      ↓
POST /refresh
      ↓
获取新的 Access Token
      ↓
保存新的 Access Token
      ↓
重试原请求

如果 Refresh Token 无效或者刷新失败：

Refresh Token
      ↓
刷新失败
      ↓
清除本地 Token
      ↓
返回认证失败
      ↓
用户重新登录
🔒 Token 存储

Android 使用 DataStore 保存 Token。

主要保存：

access_token
refresh_token

登录成功：

LoginResponse
     ↓
saveTokens()
     ↓
DataStore

Refresh Token 刷新成功：

RefreshResponse
     ↓
saveAccessToken()
     ↓
DataStore

这里只更新 Access Token，不需要重新保存 Refresh Token。

🔑 登录流程

用户输入用户名和密码：

用户输入用户名和密码
        ↓
LoginScreen
        ↓
LoginViewModel
        ↓
Repository
        ↓
Retrofit
        ↓
POST /login
        ↓
FastAPI
        ↓
查询数据库
        ↓
验证密码
        ↓
生成 Access Token
        ↓
生成 Refresh Token
        ↓
返回 Token
        ↓
Android DataStore
        ↓
保存 Access Token
保存 Refresh Token

登录成功后的响应：

{
  "access_token": "...",
  "refresh_token": "...",
  "token_type": "bearer"
}
📝 注册流程
RegisterScreen
      ↓
RegisterViewModel
      ↓
Repository
      ↓
Retrofit
      ↓
POST /register
      ↓
FastAPI
      ↓
密码 Hash
      ↓
SQLAlchemy
      ↓
数据库
      ↓
返回注册结果
💬 AI 聊天流程
用户输入问题
      ↓
ChatScreen
      ↓
ChatViewModel
      ↓
ChatRepository
      ↓
ChatApi
      ↓
AuthInterceptor
      ↓
添加 Access Token
      ↓
POST /chat
      ↓
FastAPI
      ↓
验证 JWT
      ↓
获取当前用户
      ↓
保存用户消息
      ↓
DeepSeek API
      ↓
AI 返回回答
      ↓
FastAPI
      ↓
保存 AI 消息
      ↓
Android
      ↓
更新 UI
📚 历史记录流程

历史记录 API 通过 JWT 获取当前登录用户。

Android
   ↓
GET /history
   ↓
Authorization: Bearer <access_token>
   ↓
FastAPI
   ↓
验证 JWT
   ↓
获取 current_user
   ↓
查询 current_user.id 对应的消息
   ↓
返回聊天历史

这样客户端不需要再主动传递 user_id。

用户只能访问自己的聊天记录。

💾 数据库

后端目前使用关系型数据库保存用户和聊天相关数据。

主要数据模型包括：

User
users
├── id
├── username
├── password_hash
└── created_time
Message
messages
├── id
├── user_id
├── role
├── content
└── created_time

用户与聊天消息之间通过：

User
  │
  └── Message

建立关系。

其中：

messages.user_id

用于表示消息属于哪个用户。

🐳 Docker

后端项目支持 Docker 容器化运行。

Docker 的作用主要是将后端运行环境进行封装，使项目不需要依赖宿主机上的 Python 环境。

基本流程：

Dockerfile
    ↓
docker build
    ↓
Docker Image
    ↓
docker run
    ↓
Container
    ↓
FastAPI + Uvicorn

例如：

cd AIchatBackend

docker build -t aichat-backend .

运行：

docker run -p 8000:8000 aichat-backend

启动后可以访问：

http://localhost:8000

FastAPI 接口文档：

http://localhost:8000/docs

即可打开 Swagger API 文档。

🗄️ 数据库与 Docker

项目中的数据库文件不应该被直接打包进 Docker 镜像。

Docker 镜像主要负责提供：

Python
FastAPI
SQLAlchemy
项目代码
依赖环境

容器启动后，应用根据数据库配置初始化数据库。

因此开发环境中的数据库文件与 Docker 镜像应该进行分离。

同时项目通过 .gitignore 和 .dockerignore 排除：

.env
.venv/
venv/
*.db
*.sqlite
*.sqlite3
__pycache__/

避免把敏感配置、本地环境以及数据库文件提交到 GitHub 或打入 Docker 镜像。

🖼️ Android 页面展示
Chat

History

Settings

Clear History

🚀 项目运行
1. 克隆项目
git clone https://github.com/lizzy184/AIChat.git

进入项目：

cd AIChat
2. 启动 Backend

进入后端：

cd AIchatBackend

创建并激活 Python 虚拟环境：

python -m venv .venv

Windows：

.venv\Scripts\activate

安装依赖：

pip install -r requirements.txt
配置环境变量

项目需要配置环境变量。

例如：

DEEPSEEK_API_KEY=你的DeepSeek_API_Key

JWT_SECRET_KEY=你的JWT_SECRET_KEY
JWT_ALGORITHM=HS256

JWT_EXPIRE_MINUTES=15
REFRESH_TOKEN_EXPIRE_DAYS=7
注意

不要将真实的 API Key、JWT Secret Key 或其他敏感信息直接提交到 GitHub。

推荐通过：

.env

保存本地环境变量，并确保 .env 已加入 .gitignore。

启动 FastAPI
uvicorn main:app --reload

或者使用 Docker：

docker build -t aichat-backend .

然后：

docker run -p 8000:8000 aichat-backend

访问：

http://localhost:8000/docs

即可打开 FastAPI Swagger API 文档。

3. 启动 Android

使用 Android Studio 打开：

AIchatAndroid

等待 Gradle 同步完成。

然后：

Run → Run 'app'

选择：

Android Emulator

或者：

Android 真机

即可运行项目。

⚙️ Android 与 Backend 通信

Android 客户端通过 Retrofit 调用 FastAPI。

开发环境中需要注意：

如果 Android Emulator 访问宿主机上的 FastAPI：

localhost

通常不能直接指向电脑。

Android Emulator 访问宿主机可以使用：

10.0.2.2

例如：

http://10.0.2.2:8000/

如果使用真机，则需要让手机与电脑处于同一局域网，并使用电脑的局域网 IP 地址。

例如：

http://192.168.x.x:8000/
📡 API

目前后端主要包含以下 API。

用户认证
注册
POST /register

用于创建用户。

登录
POST /login

用于用户登录并获取：

Access Token
Refresh Token
刷新 Token
POST /refresh

使用 Refresh Token 获取新的 Access Token。

聊天
POST /chat

用于发送聊天消息并获取 AI 回复。

该接口需要 Access Token。

请求：

Authorization: Bearer <access_token>
历史记录
获取历史记录
GET /history

用于获取当前登录用户的聊天历史。

需要 Access Token。

清空历史记录
DELETE /history

用于清空当前登录用户的聊天历史。

需要 Access Token。

API 认证关系
/register
    ↓
不需要登录

/login
    ↓
不需要登录
    ↓
返回 Access Token + Refresh Token

/refresh
    ↓
需要 Refresh Token

/chat
    ↓
需要 Access Token

/history
    ↓
需要 Access Token

DELETE /history
    ↓
需要 Access Token

具体接口参数和返回结果可以通过 FastAPI 自动生成的 Swagger 文档查看：

/docs
🧩 Android 架构

Android 项目主要按照以下思路组织：

UI
 ↓
ViewModel
 ↓
Repository
 ↓
Network / Database

例如聊天功能：

ChatScreen
     ↓
ChatViewModel
     ↓
ChatRepository
     ↓
ChatApi
     ↓
AuthInterceptor
     ↓
FastAPI

Token 自动刷新流程：

ChatApi
   ↓
OkHttp
   ↓
AuthInterceptor
   ↓
FastAPI
   ↓
401
   ↓
TokenAuthenticator
   ↓
RefreshApi
   ↓
POST /refresh
   ↓
新的 Access Token
   ↓
重新请求原 API

本地聊天记录：

ChatViewModel
     ↓
Repository
     ↓
Room
     ↓
MessageDao
     ↓
MessageEntity

这种结构可以降低 UI、业务逻辑、网络请求以及数据库操作之间的耦合。

🧵 协程

项目在 Android 网络请求以及数据处理过程中使用 Kotlin Coroutines。

主要用于：

网络请求
数据库操作
异步任务
UI 状态更新
DataStore 数据读取
Token 刷新过程中的异步调用

基本数据流：

Main Thread
    ↓
ViewModel
    ↓
Coroutine
    ↓
Repository
    ↓
Network / Database
    ↓
返回结果
    ↓
更新 UI State

通过协程，可以让耗时操作在后台执行，同时保持 Android UI 的流畅性。

🛡️ HTTP 状态码与认证

项目中的认证流程主要使用以下 HTTP 状态码。

200 OK

请求成功。

例如：

POST /login → 200
POST /refresh → 200
POST /chat → 200
401 Unauthorized

表示当前请求没有通过身份认证。

常见情况：

Access Token 过期
Access Token 无效
Token 缺失
Refresh Token 无效

对于 Access Token 过期：

401
 ↓
TokenAuthenticator
 ↓
/refresh
 ↓
新的 Access Token
 ↓
重试原请求
403 Forbidden

表示用户已经完成身份认证，但没有访问某个资源的权限。

项目当前主要使用 JWT 进行身份认证，后续可以进一步完善用户权限管理。

📚 项目学习内容

通过这个项目主要学习和实践了以下内容。

Android
Kotlin
Jetpack Compose
Material 3
MVVM
ViewModel
Kotlin Coroutines
Retrofit
OkHttp
Interceptor
Authenticator
Room
Navigation
DataStore / Preferences
Repository Pattern
Dependency Injection
Hilt
UI State 管理
JWT Token 管理
Access Token / Refresh Token
Token 自动刷新
Backend
Python
FastAPI
REST API
Pydantic
SQLAlchemy
ORM
SQLite
用户注册与登录
密码 Hash
JWT
Access Token
Refresh Token
OAuth2 Bearer Token
API 路由设计
Service 层设计
用户与消息数据关联
AI
DeepSeek API
AI 对话 API 调用
Android → FastAPI → DeepSeek 数据流
DevOps
Docker
Dockerfile
Docker Image
Docker Container
.dockerignore
.gitignore
Git
GitHub
前后端分离项目部署
🧪 JWT 自动刷新测试

项目已经实际测试 Access Token 过期后的自动刷新流程。

测试流程：

Access Token 过期
        ↓
POST /chat
        ↓
401 Unauthorized
        ↓
Android TokenAuthenticator
        ↓
POST /refresh
        ↓
200 OK
        ↓
获取新的 Access Token
        ↓
保存新的 Access Token
        ↓
自动重试 POST /chat
        ↓
200 OK

实际测试日志：

POST /chat      → 401 Unauthorized
POST /refresh   → 200 OK
POST /chat      → 200 OK

这说明 Android 客户端已经能够在 Access Token 过期后自动使用 Refresh Token 获取新的 Access Token，并重新执行原请求。

📈 当前项目状态

目前项目已经完成：

 Android 基础项目
 Jetpack Compose UI
 MVVM 架构
 Kotlin Coroutines
 Retrofit 网络请求
 OkHttp
 Room 本地数据库
 DataStore 本地数据存储
 用户注册
 用户登录
 JWT Token 认证
 Access Token
 Refresh Token
 Access Token 自动刷新
 401 自动重试
 登录状态保存
 用户退出登录
 AI 聊天
 聊天记录
 历史记录页面
 清空聊天历史
 设置页面
 FastAPI 后端
 Pydantic 数据校验
 SQLAlchemy 数据库操作
 用户密码 Hash
 DeepSeek API 接入
 Docker 后端容器化
 Git 版本控制
 GitHub 项目发布
🔮 后续计划

以下功能属于后续可以继续完善的方向，并不是当前项目已经实现的功能。

认证与安全
 更完善的用户权限管理
 Refresh Token Rotation
 Refresh Token 撤销机制
 多设备登录管理
 Token / Session 管理优化
 更完善的异常处理
AI 功能
 多轮上下文优化
 Markdown AI 回复
 代码高亮
 流式输出
 多模型支持
 AI 对话参数配置
Backend
 Docker Compose
 PostgreSQL
 Redis
 后端部署到云服务器
 HTTPS
 API 限流
 日志系统优化
DevOps / Testing
 CI/CD
 自动化测试
 后端单元测试
 Android UI 测试
 API 测试
 JWT 认证测试
 Token 自动刷新测试自动化
📌 项目定位

AIChat 不只是一个简单的 AI 聊天 Demo，而是一个用于学习和实践完整前后端项目开发流程的个人项目。

通过这个项目，将：

Android 客户端
      +
FastAPI Backend
      +
SQLAlchemy
      +
Database
      +
JWT Authentication
      +
DeepSeek API
      +
Docker
      +
Git / GitHub

结合起来，形成一个完整的前后端分离应用。

整体技术路线：

Kotlin
  +
Jetpack Compose
  +
MVVM
  +
Coroutines
  +
Retrofit / OkHttp
  +
Room
  +
DataStore
  +
JWT Authentication
       │
       ↓
    FastAPI
       │
       ↓
   SQLAlchemy
       │
       ↓
    Database
       │
       ↓
  DeepSeek API
       │
       ↓
      AI

同时通过 Git 和 GitHub 对项目进行版本管理和代码托管。

📁 子项目说明
Android 客户端

Android 客户端的详细说明请查看：

AIchatAndroid/README.md

主要包含：

Android 项目结构
Compose UI
MVVM
ViewModel
Coroutines
Retrofit
OkHttp
Room
Navigation
DataStore
JWT Authentication
Access Token / Refresh Token
Token 自动刷新
页面功能
本地数据存储
Backend

后端项目的详细说明请查看：

AIchatBackend/README.md

主要包含：

FastAPI 项目结构
API 接口
SQLAlchemy
数据库
用户认证
JWT
Access Token
Refresh Token
DeepSeek API
Docker
环境变量
后端运行方式
📝 项目说明

本项目主要用于：

个人学习
Android 开发实践
FastAPI 后端开发
AI 应用开发
前后端分离项目实践
JWT 身份认证学习
Docker 容器化学习
Git / GitHub 版本管理

项目会随着学习进度持续完善。

👨‍💻 Author

lizzy184

GitHub：

https://github.com/lizzy184/AIChat
⭐ Star

如果这个项目对你有帮助，欢迎 Star ⭐