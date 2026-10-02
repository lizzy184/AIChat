README.md
Part 2/4
🛠️ 技术栈
Android
技术	用途
Kotlin	Android 应用主要开发语言
Jetpack Compose	Android UI 开发
Material 3	UI 组件与设计
MVVM	Android 应用架构
ViewModel	管理 UI 状态
Kotlin Coroutines	异步任务处理
StateFlow	实时状态更新
Retrofit	HTTP 网络请求
OkHttp	HTTP 客户端、Interceptor、Authenticator、SSE 流读取
Room	本地数据库
Navigation	页面导航
DataStore	Token 与用户状态保存
Hilt / Dependency Injection	依赖注入
Repository Pattern	数据层抽象
Backend
技术	用途
Python	后端开发语言
FastAPI	Web API 框架
StreamingResponse	SSE 流式输出
Pydantic	请求与响应数据校验
SQLAlchemy	ORM 数据库操作
SQLite	项目开发阶段数据库
Bcrypt	用户密码 Hash
PyJWT	JWT Token 生成与验证
OAuth2 Bearer Token	API 身份认证
logger.py	统一日志管理
Uvicorn	ASGI 服务运行
Docker	后端容器化
🤖 AI / RAG / Agent

项目使用 DeepSeek Streaming API 作为 AI 对话服务。

同时加入：

RAG（Retrieval-Augmented Generation）
LangGraph Agent Workflow
Tool Calling

形成：

AI Chat

   |

   ├── Direct LLM Chat

   |

   ├── RAG Knowledge Retrieval

   |

   └── Agent Tool Calling


使用的技术：

技术	用途
DeepSeek Streaming API	AI 流式对话
LangGraph	Agent 工作流编排
Agent State	管理 Agent 当前状态
Tool Calling	Agent 调用外部工具
RAG Pipeline	知识库增强生成
Embedding	文档向量化
Vector Retrieval	相似度检索
PDF Parser	文档解析
📚 RAG 知识库

项目已经完成：

PDF

 ↓

Text Extract

 ↓

Chunk Split

 ↓

Embedding

 ↓

Vector Index

 ↓

Retriever

 ↓

LLM

 ↓

Answer

用户可以上传 PDF 文件：

Android

 ↓

POST /documents/upload

 ↓

FastAPI

 ↓

PDF Loader

 ↓

Chunk Splitter

 ↓

Embedding

 ↓

Vector Store

 ↓

Knowledge Base
RAG Pipeline

完整流程：

Android

    ↓

选择 PDF

    ↓

POST /documents/upload

    ↓

Backend

    ↓

PDF Loader

    ↓

Text Chunking

    ↓

Embedding Model

    ↓

vectors.npy

    ↓

rag_index.json

    ↓

Retriever Reload

    ↓

用户提问

    ↓

Knowledge Base Tool

    ↓

Vector Retrieval

    ↓

Context

    ↓

Prompt

    ↓

DeepSeek

    ↓

SSE Streaming

    ↓

Android UI
RAG 模块结构
rag/

├── loader.py

├── splitter.py

├── embedding.py

├── vector_store.py

├── retriever.py

├── rag_service.py

├── build_index.py

├── test_loader.py

└── test_retriever.py


模块说明：

loader.py

负责：

PDF 文件读取
文本提取
splitter.py

负责：

文档切分
Chunk 生成
embedding.py

负责：

文本向量化
vector_store.py

负责：

保存向量
加载索引
retriever.py

负责：

根据用户问题搜索相关 Chunk
rag_service.py

负责：

组织 RAG 查询流程
🤖 LangGraph Agent

项目后端新增 Agent 能力。

Agent 使用 LangGraph 构建。

目前实现的是：

基于 LangGraph State Graph 的单 Agent 工作流，通过 Tool Calling 调用知识库查询和计算工具。

并不是复杂的 Multi-Agent 系统，而是一个轻量级 Agent Workflow。

Agent 架构

整体结构：

                User Message

                     |

                     ↓

              Agent.run()

                     |

                     ↓

             LangGraph Graph

                     |

                     ↓

              Agent State

                     |

                     ↓

              LLM Node

                     |

          判断是否需要调用 Tool

                     |

        ┌────────────┴────────────┐

        ↓                         ↓

Knowledge Base Tool        Calculator Tool


        ↓                         ↓

 RAG Retrieval             Math Calculation


        └────────────┬────────────┘

                     ↓

              Final Response

Agent 核心代码流程

Agent 初始化：

class Agent:


    def __init__(
        self,
        llm_client,
        model
    ):


        self.llm_client = llm_client

        self.model = model


        self.tools = [

            KNOWLEDGE_BASE_TOOL,

            CALCULATOR_TOOL

        ]


        self.graph = create_graph(

            llm_client=self.llm_client,

            model=self.model,

            tools=self.tools

        )

初始化过程中：

创建 Agent Tools
创建 LangGraph Workflow
注入 LLM Client
构建 Agent 执行图
Agent 执行流程

用户消息进入：

async def run(
    self,
    messages:list,
):


    state = {

        "messages":messages

    }


    result = await self.graph.ainvoke(

        state

    )


    return result["messages"][-1].content

执行流程：

messages

   ↓

AgentState

   ↓

LangGraph Graph

   ↓

Node Execution

   ↓

Tool Calling

   ↓

Final Message

Agent Tools

当前 Agent 包含两个工具。

📚 Knowledge Base Tool

用于调用 RAG 知识库。

流程：

User Question

       ↓

Agent

       ↓

Knowledge Base Tool

       ↓

Retriever

       ↓

Vector Search

       ↓

Relevant Documents

       ↓

LLM

       ↓

Answer

应用场景：

PDF 文档问答
项目文档查询
知识库搜索
🧮 Calculator Tool

用于处理数学计算。

流程：

User

 ↓

Agent

 ↓

Calculator Tool

 ↓

Calculation

 ↓

Result

 ↓

LLM Response

例如：

用户：

计算 123 * 456


Agent:

调用 Calculator Tool


返回：

56088
Agent 与 RAG 集成

Agent 和已有 RAG 系统结合：

                 User

                  |

                  ↓

              LangGraph

                  |

          判断是否需要知识查询

                  |

          ┌───────────────┐

          ↓               ↓

       普通回答       Knowledge Tool


                          |

                          ↓

                     Retriever


                          |

                          ↓

                    Vector Search


                          |

                          ↓

                    Context


                          |

                          ↓

                       LLM


                          |

                          ↓

                      Answer

Agent 与 SSE Streaming

当前聊天架构：

Android

 ↓

POST /chat

 ↓

FastAPI

 ↓

Agent

 ↓

LangGraph

 ↓

DeepSeek

 ↓

StreamingResponse

 ↓

Android OkHttp

 ↓

Compose UI

Agent 生成结果后，可以继续通过现有 SSE 机制返回 Android。

Agent 当前能力总结

目前已经实现：

✅ LangGraph Agent Workflow

✅ Agent State 管理

✅ LLM Node 调用

✅ Tool Calling

✅ Knowledge Base Tool

✅ Calculator Tool

✅ Agent + RAG 集成

✅ Agent 与 FastAPI 集成

✅ Agent 与聊天系统结合

（Part 2/4 完）

下一部分继续：

Part 3/4：JWT认证、SSE Streaming、数据库、Docker、API说明、Android架构部分

继续
README.md
Part 3/4
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

POST /documents/upload

请求时通过 HTTP Header 携带：

Authorization: Bearer <access_token>

Access Token 使用较短有效期。

这样可以降低 Token 泄露后的风险。

Refresh Token

Refresh Token 用于获取新的 Access Token。

Refresh Token 不直接用于访问普通业务 API。

流程：

Android

 ↓

请求业务 API

 ↓

Access Token 过期

 ↓

401 Unauthorized

 ↓

TokenAuthenticator

 ↓

POST /refresh

 ↓

获取新的 Access Token

 ↓

保存 Token

 ↓

重新执行原请求
🔄 Access Token 自动刷新

Android 使用：

AuthInterceptor
TokenAuthenticator

实现自动 Token 管理。

整体流程：

                Android App

                     |

                     ↓

              HTTP Request

                     |

                     ↓

            AuthInterceptor

                     |

                     ↓

      Authorization: Bearer Token

                     |

                     ↓

             FastAPI Backend

                     |

                     ↓

              Token Expired

                     |

                     ↓

                  401

                     |

                     ↓

          TokenAuthenticator

                     |

                     ↓

              POST /refresh

                     |

                     ↓

          New Access Token

                     |

                     ↓

              DataStore保存

                     |

                     ↓

             Retry Original Request

AuthInterceptor

负责：

自动读取 Access Token
添加 Authorization Header

流程：

HTTP Request

      ↓

AuthInterceptor

      ↓

读取 DataStore

      ↓

添加 Token

      ↓

发送请求
TokenAuthenticator

负责：

捕获 401
判断 Token 是否失效
调用 Refresh API
保存新 Token
重试请求

流程：

收到401

 ↓

检查是否已经刷新

 ↓

读取 Refresh Token

 ↓

POST /refresh

 ↓

获取新 Access Token

 ↓

保存

 ↓

重新发送请求


如果刷新失败：

 ↓

清除本地 Token

 ↓

用户重新登录
🔑 登录流程

用户输入用户名和密码：

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

查询用户

      ↓

验证密码

      ↓

生成 JWT

      ↓

返回 Token

      ↓

DataStore 保存


登录返回：

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

Password Hash

      ↓

SQLAlchemy

      ↓

Database

      ↓

返回结果
💬 AI 聊天流程（SSE Streaming）

项目已经移除传统一次性响应模式。

当前全部使用 SSE Streaming。

完整流程：

用户输入问题


        ↓


ChatScreen


        ↓


ChatViewModel


        ↓


ChatRepository


        ↓


OkHttp SSE Request


        ↓


AuthInterceptor


        ↓


POST /chat


        ↓


FastAPI


        ↓


JWT验证


        ↓


获取用户


        ↓


保存用户消息


        ↓


Agent


        ↓


LangGraph Workflow


        ↓


Tool Calling / DeepSeek


        ↓


StreamingResponse


        ↓


Android读取chunk


        ↓


Repository callback


        ↓


StateFlow更新


        ↓


Compose实时渲染


        ↓


保存完整 Assistant Message

🧵 Android SSE Streaming 实现

Android 使用：

OkHttp
Kotlin Coroutine
Repository Callback

数据流：

FastAPI

 ↓

SSE Chunk

 ↓

OkHttp Response Body

 ↓

Repository

 ↓

onChunk()

 ↓

ViewModel

 ↓

StateFlow

 ↓

Compose

例如：

AI:

你

你好

你好，

你好，很高兴

你好，很高兴帮助你

实现类似 ChatGPT 的实时输出效果。

💾 数据库设计

项目使用关系型数据库保存用户和聊天数据。

主要模型：

User

users

字段：

id

username

password_hash

created_time
Message

messages

字段：

id

user_id

role

content

created_time

关系：

User


 |

 |

 └──── Message


其中：

messages.user_id

用于关联用户聊天记录。

Conversation 会话管理

项目支持多会话管理。

包括：

创建会话
获取会话列表
获取会话消息
删除会话

接口：

GET /conversations


GET /conversations/{conversation_id}/messages


DELETE /conversations/{conversation_id}
🪵 logger.py 日志系统

Backend 增加统一日志模块。

记录：

API 请求
用户认证
Agent 调用
DeepSeek 请求
SSE Streaming
数据库异常
网络异常

示例：

INFO  Chat request received

INFO  Start Agent workflow

INFO  Tool calling knowledge_base

INFO  DeepSeek streaming started

INFO  Save assistant message

ERROR Request failed
🐳 Docker

Backend 支持 Docker 容器运行。

作用：

封装 Python 环境
固定依赖版本
简化部署

流程：

Dockerfile

 ↓

docker build

 ↓

Docker Image

 ↓

docker run

 ↓

FastAPI Container

构建：

cd AIchatBackend

docker build -t aichat-backend .

运行：

docker run -p 8000:8000 aichat-backend

启动后：

http://localhost:8000

Swagger:

http://localhost:8000/docs
🗄️ Docker 与数据库

数据库文件不直接打包进入 Docker。

Docker 镜像包含：

Python
FastAPI
项目代码
requirements

不包含：

.env
*.db
*.sqlite
本地缓存

通过：

.gitignore

.dockerignore

进行过滤。

📡 API
用户认证
注册
POST /register

创建用户。

登录
POST /login

返回：

Access Token
Refresh Token
刷新 Token
POST /refresh

使用 Refresh Token 获取新的 Access Token。

聊天
AI Chat
POST /chat

功能：

AI 对话
Agent 调用
RAG 查询
SSE Streaming

请求：

Authorization: Bearer <access_token>
历史记录

获取：

GET /history

清空：

DELETE /history

需要：

Access Token
文档 / RAG

上传 PDF：

POST /documents/upload

请求：

multipart/form-data

file=<PDF>

功能：

PDF解析
Chunk生成
Embedding
Vector Index
Knowledge Base
API认证关系
/register

    ↓

无需登录



/login

    ↓

返回 Token



/refresh

    ↓

Refresh Token



/chat

    ↓

Access Token



/history

    ↓

Access Token



/documents/upload

    ↓

Access Token


（Part 3/4 完）

下一部分继续：

Part 4/4：Android 架构、项目运行方式、测试、当前状态、未来计划、Author（最终完整结束版）

继续
README.md
Part 4/4
🧩 Android 架构

Android 客户端采用 MVVM 架构。

整体结构：

UI Layer

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

OkHttp SSE

      ↓

FastAPI Backend

      ↓

LangGraph Agent

      ↓

DeepSeek
Repository Pattern

项目使用 Repository 作为数据访问层。

主要负责：

网络请求
SSE Streaming
Room 数据操作
Token 管理
数据转换

聊天流程：

ViewModel

    ↓

Repository

    ↓

SSE Stream

    ↓

chunk callback

    ↓

StateFlow

    ↓

Compose UI
🧵 Kotlin Coroutines

项目大量使用 Kotlin Coroutines。

主要场景：

网络请求
SSE 流读取
数据库操作
Token Refresh
DataStore 读取
UI 状态更新

流程：

Main Thread


      ↓


ViewModel Coroutine


      ↓


Repository


      ↓


Network / Database


      ↓


Result


      ↓


StateFlow


      ↓


Compose UI

🧭 Navigation

Android 使用 Navigation Compose。

页面包括：

Start 页面
Login 页面
Register 页面
Chat 页面
History 页面
Setting 页面

导航结构：

Start

 |

 ├── Login

 |

 ├── Register

 |

 ↓

Main

 |

 ├── Chat

 |

 ├── History

 |

 └── Setting

🧪 JWT 自动刷新测试

项目已经实际测试 Access Token 过期后的自动刷新流程。

测试：

Access Token 过期


        ↓


POST /chat


        ↓


401 Unauthorized


        ↓


TokenAuthenticator


        ↓


POST /refresh


        ↓


200 OK


        ↓


保存新的 Access Token


        ↓


重新请求 /chat


        ↓


200 OK


测试日志：

POST /chat       → 401 Unauthorized

POST /refresh    → 200 OK

POST /chat       → 200 OK

说明：

Android 客户端已经能够：

自动检测 Token 失效
使用 Refresh Token 刷新
保存新 Token
自动重试原请求
🚀 项目运行
1. Clone 项目
git clone https://github.com/lizzy184/AIChat.git

进入：

cd AIChat
2. 启动 Backend

进入：

cd AIchatBackend

创建虚拟环境：

python -m venv .venv

Windows:

.venv\Scripts\activate

安装依赖：

pip install -r requirements.txt
配置环境变量

创建：

.env

内容：

DEEPSEEK_API_KEY=your_api_key

JWT_SECRET_KEY=your_secret

JWT_ALGORITHM=HS256

JWT_EXPIRE_MINUTES=15

REFRESH_TOKEN_EXPIRE_DAYS=7

注意：

不要提交：

API Key
JWT Secret
数据库文件

到 GitHub。

启动 FastAPI
uvicorn main:app --reload

Swagger:

http://localhost:8000/docs
Docker 启动

构建：

docker build -t aichat-backend .

运行：

docker run -p 8000:8000 aichat-backend
3. 启动 Android

使用 Android Studio 打开：

AIchatAndroid

等待 Gradle Sync。

运行：

Run → Run app

支持：

Android Emulator
Android 真机
⚙️ Android Backend 通信
Emulator

Android Emulator 访问电脑：

不能使用：

localhost

使用：

10.0.2.2

例如：

http://10.0.2.2:8000/
真机

要求：

手机和电脑同一网络

使用电脑局域网 IP：

例如：

http://192.168.x.x:8000/
📈 当前项目状态

目前项目已经完成：

Android

✅ Kotlin Android 项目

✅ Jetpack Compose

✅ Material 3

✅ MVVM 架构

✅ ViewModel

✅ Coroutines

✅ StateFlow

✅ Retrofit

✅ OkHttp Streaming

✅ Room

✅ DataStore

✅ Navigation

✅ Hilt Dependency Injection

✅ JWT Token 管理

✅ Access Token

✅ Refresh Token

✅ Token 自动刷新

✅ 401 自动重试

✅ 登录状态保存

✅ 用户退出登录

AI Chat

✅ SSE Streaming AI Chat

✅ DeepSeek Streaming API

✅ Repository Chunk Callback

✅ Compose 实时渲染

✅ Streaming 完成保存消息

Agent

✅ LangGraph Workflow

✅ Agent State

✅ Agent Graph 创建

✅ Tool Calling

✅ Knowledge Base Tool

✅ Calculator Tool

✅ Agent 与 RAG 集成

✅ Agent 与 FastAPI 集成

RAG

✅ PDF 上传

✅ PDF Parsing

✅ Text Chunk

✅ Embedding

✅ Vector Index

✅ Retriever

✅ Knowledge Base Query

Backend

✅ FastAPI

✅ REST API

✅ StreamingResponse

✅ Pydantic

✅ SQLAlchemy

✅ SQLite

✅ 用户系统

✅ JWT Authentication

✅ Password Hash

✅ Conversation 管理

✅ logger.py 日志系统

DevOps

✅ Docker

✅ Dockerfile

✅ dockerignore

✅ Git

✅ GitHub

🔮 后续计划
Agent 能力增强

未来可以继续完善：

Agent Streaming 输出优化
Agent Tool 扩展
更多外部工具接入
Agent Prompt 优化
Agent 状态管理优化
Multi-Agent Workflow
Agent Evaluation
AI 功能
RAG 检索优化
Chunk 策略优化
Embedding 模型优化
Markdown 渲染
Code Highlight
多模型支持
AI 参数配置
Backend
PostgreSQL
Redis
Docker Compose
HTTPS
API 限流
服务监控
日志系统优化
Testing
Android UI Test
Backend Unit Test
API Test
Agent Workflow Test
RAG Retrieval Test
JWT 自动刷新自动化测试
📌 项目定位

AIChat 不只是一个简单 AI Demo。

项目结合：

Android

+

Jetpack Compose

+

MVVM

+

Coroutines

+

StateFlow

+

Retrofit / OkHttp SSE

+

Room

+

DataStore

+

JWT Authentication

        |

        ↓

FastAPI Backend

        |

        ↓

LangGraph Agent

        |

        ↓

RAG Knowledge Base

        |

        ↓

DeepSeek Streaming API


形成一个完整的：

Android + FastAPI + Agent + RAG + Streaming AI 应用。

通过该项目实践：

Android 客户端开发
后端 API 设计
AI Agent Workflow
RAG 知识库
SSE Streaming
JWT 安全认证
Docker 部署
Git 项目管理
📁 子项目说明
Android 客户端

详细说明：

AIchatAndroid/README.md

包含：

Compose UI
MVVM
ViewModel
Repository
StateFlow
SSE Streaming
Room
DataStore
JWT
Backend

详细说明：

AIchatBackend/README.md

包含：

FastAPI
JWT
SQLAlchemy
SSE
DeepSeek
LangGraph Agent
RAG
Docker
📝 项目说明

本项目主要用于：

个人学习
Android 开发实践
FastAPI 后端实践
AI Agent 开发实践
LangGraph 工作流学习
RAG 应用开发
SSE Streaming 实践
JWT 认证学习
Docker 部署学习
GitHub 项目管理

项目会随着学习持续完善。

👨‍💻 Author

lizzy184

GitHub：

https://github.com/lizzy184/AIChat

⭐ Star

如果这个项目对你有帮助，欢迎 Star ⭐