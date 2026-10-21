\# AIChat Backend



AIChat Backend 是 AIChat 全栈 AI 聊天应用的后端服务，基于 Python 和 FastAPI 构建，为 Android 客户端提供 API、用户认证、聊天数据管理以及 AI 对话能力。



项目结合 LangGraph Agent、RAG 知识检索、LLM 调用和流式响应等能力，实践 AI 应用后端的模块化设计与服务部署。



\## ✨ Features



\### REST API



\- 基于 FastAPI 提供 HTTP API

\- 使用 Pydantic 进行请求和响应数据校验

\- 使用 SQLAlchemy 访问关系型数据库

\- 为 Android 客户端提供用户与聊天相关的业务接口



\### Authentication



\- JWT 用户认证

\- 密码哈希处理

\- OAuth2 Bearer Token 认证模式

\- Access Token 与 Refresh Token 相关流程



Token 刷新、过期处理及权限校验的具体行为以当前代码为准。



\### AI Chat \& Streaming



\- 调用 LLM 服务生成 AI 回复

\- 使用 SSE 相关机制传输流式输出

\- 将 AI 服务与聊天业务流程连接

\- 支持聊天消息和会话的持久化管理



\### LangGraph Agent



项目使用 LangGraph 组织 Agent 工作流。根据实际实现，Agent 可以结合用户问题、工作流状态和工具调用结果生成最终回答。



当前规划中的工作流涉及：



\- LLM 节点

\- Agent State

\- 知识库检索工具

\- Calculator 工具

\- 工具调用与结果处理



具体节点、条件边和执行顺序以 Agent 源码为准。



\### RAG Knowledge Base



项目支持 PDF 文档知识库处理，相关流程包括：



1\. 加载 PDF 文档。

2\. 提取文本。

3\. 将文本拆分为适当的片段。

4\. 生成文本 Embedding。

5\. 建立或更新向量索引。

6\. 检索与用户问题相关的内容。

7\. 将检索结果作为上下文辅助生成回答。



\### Database \& Cache



\- PostgreSQL：服务端关系型数据存储方案。

\- SQLite：开发阶段可使用的轻量级数据库方案。

\- Redis：缓存或临时状态存储方案。



实际启用的数据库和 Redis 功能取决于当前配置及服务接入情况。



\### Containerization



\- Docker 镜像构建

\- FastAPI 服务容器化

\- Docker Compose 多服务编排

\- 环境变量配置

\- 数据卷与容器网络管理



\## 🛠 Tech Stack



| Technology | Purpose |

|---|---|

| Python | 后端开发语言 |

| FastAPI | HTTP API 框架 |

| Uvicorn | ASGI 服务运行 |

| Pydantic | 数据校验 |

| SQLAlchemy | ORM 与数据库访问 |

| PostgreSQL | 关系型数据库 |

| SQLite | 开发阶段的轻量级数据库 |

| JWT | 用户认证 |

| Bcrypt | 密码哈希 |

| OAuth2 Bearer Token | API 认证模式 |

| LangGraph | Agent 工作流编排 |

| LLM API | AI 文本生成 |

| Embedding Model | 文本向量化 |

| RAG | 检索增强生成 |

| Redis | 缓存或临时状态存储 |

| Docker | 应用容器化 |

| Docker Compose | 多服务编排 |



\## 🏗 Architecture



后端整体职责可以概括为：



```text

Android Client

&#x20;     |

&#x20;     v

FastAPI Routes

&#x20;     |

&#x20;     +-------------------------+

&#x20;     |                         |

&#x20;     v                         v

Authentication             Chat Service

&#x20;     |                         |

&#x20;     v                         v

JWT / User Service        Agent Workflow

&#x20;                               |

&#x20;                               v

&#x20;                          LangGraph

&#x20;                               |

&#x20;                 +-------------+-------------+

&#x20;                 |                           |

&#x20;                 v                           v

&#x20;            RAG Tool                   Calculator Tool

&#x20;                 |

&#x20;                 v

&#x20;            Retriever

&#x20;                 |

&#x20;                 v

&#x20;            Vector Index



Chat Service / Agent

&#x20;     |

&#x20;     v

LLM API

&#x20;     |

&#x20;     v

Streaming Response

&#x20;     |

&#x20;     v

Android Client



Database Layer

&#x20;     |

&#x20;     +---- PostgreSQL / SQLite

&#x20;     |

&#x20;     +---- Redis (按实际接入情况)

```



该图用于说明主要职责和逻辑关系，不代表所有组件都通过同一条同步调用链执行。实际依赖关系以项目源码为准。



\## 📁 Project Structure



以下为项目模块的逻辑示意。请根据仓库中的真实文件目录进行调整。



```text

AIchatBackend/

├── main.py

├── app/

│   ├── api/

│   ├── models/

│   ├── database/

│   ├── services/

│   ├── agent/

│   └── rag/

│       ├── loader.py

│       ├── splitter.py

│       ├── embedding.py

│       ├── vector\_store.py

│       ├── retriever.py

│       ├── rag\_service.py

│       └── build\_index.py

├── config.py

├── requirements.txt

├── Dockerfile

└── README.md

```



如果实际项目将 `main.py`、`agent/` 或 `rag/` 放在不同位置，应以真实目录为准。



\## 🔐 Authentication



项目使用 JWT 相关机制对受保护的 API 进行认证。



典型的 Access Token 认证流程如下：



```text

Client Login

&#x20;    |

&#x20;    v

Validate Credentials

&#x20;    |

&#x20;    v

Generate Token

&#x20;    |

&#x20;    v

Return Authentication Data

&#x20;    |

&#x20;    v

Client Sends Bearer Token

&#x20;    |

&#x20;    v

Backend Validates Token

&#x20;    |

&#x20;    v

Process Protected Request

```



客户端请求通常会携带：



```http

Authorization: Bearer <access\_token>

```



认证实现需要重点关注：



\- 密码是否经过安全哈希处理。

\- JWT 的签名算法和密钥配置。

\- Token 的有效期与过期处理。

\- 受保护接口是否进行认证与授权检查。

\- Refresh Token 的签发、校验、更新和失效逻辑。



\*\*安全提示：\*\* JWT Secret、模型 API Key、数据库密码等敏感值应通过环境变量或适当的密钥管理方式提供，不应提交到 Git 仓库。



\## ⚡ AI Chat \& SSE Streaming



流式聊天将 AI 生成过程中的内容逐步传递给客户端，避免必须等待整段回答完成后才显示。



典型流程：



```text

Android Sends Chat Request

&#x20;         |

&#x20;         v

FastAPI Receives Request

&#x20;         |

&#x20;         v

Chat / Agent Processing

&#x20;         |

&#x20;         v

LLM Generates Output

&#x20;         |

&#x20;         v

Backend Streams Events

&#x20;         |

&#x20;         v

Android Parses Stream

&#x20;         |

&#x20;         v

UI Updates Incrementally

```



SSE 是一种服务端向客户端持续发送事件的传输机制，并不负责决定 AI 如何推理或检索知识。



实际实现应明确：



\- 请求使用的 HTTP 方法和路径。

\- 响应是否采用 `text/event-stream`。

\- 事件数据格式及消息边界。

\- 流结束时使用的协议标记。

\- 错误事件、超时与客户端断开时的处理方式。

\- 客户端是否能够取消正在执行的请求。



具体协议应以实际接口实现为准。



\## 🤖 LangGraph Agent



Agent 工作流负责组织模型调用、状态传递和工具执行。



项目的目标工作流包括：



```text

User Question

&#x20;     |

&#x20;     v

Agent Workflow

&#x20;     |

&#x20;     v

LLM / Decision Node

&#x20;     |

&#x20;     v

Tool Selection

&#x20;     |

&#x20;     +--------------------+

&#x20;     |                    |

&#x20;     v                    v

Knowledge Search       Calculator

&#x20;     |                    |

&#x20;     v                    v

Retrieved Context      Calculation Result

&#x20;     |                    |

&#x20;     +----------+---------+

&#x20;                |

&#x20;                v

&#x20;          Final Response

```



其中：



\- \*\*Agent State\*\*：保存工作流执行所需的状态。

\- \*\*LLM Node\*\*：调用模型进行问题处理或下一步决策。

\- \*\*Knowledge Tool\*\*：在需要时检索知识库。

\- \*\*Calculator Tool\*\*：处理适合交由计算工具执行的任务。

\- \*\*Final Response\*\*：结合模型输出及工具结果生成回答。



Agent 并不意味着所有问题都会调用工具。工具调用的触发条件、循环终止规则和错误处理应根据实际图结构实现。



\## 📚 RAG Knowledge Base



RAG（Retrieval-Augmented Generation，检索增强生成）通过检索外部知识，为模型回答提供相关上下文。



\### Document Ingestion



```text

PDF Document

&#x20;     |

&#x20;     v

PDF Loader / Parser

&#x20;     |

&#x20;     v

Text Extraction

&#x20;     |

&#x20;     v

Text Chunking

&#x20;     |

&#x20;     v

Embedding Generation

&#x20;     |

&#x20;     v

Vector Index

```



\### Query Process



```text

User Question

&#x20;     |

&#x20;     v

Query Embedding

&#x20;     |

&#x20;     v

Retriever

&#x20;     |

&#x20;     v

Relevant Documents

&#x20;     |

&#x20;     v

Knowledge Context

&#x20;     |

&#x20;     v

LLM / Agent

&#x20;     |

&#x20;     v

Answer

```



关键模块的职责通常包括：



| Module | Responsibility |

|---|---|

| `loader.py` | 加载和解析文档 |

| `splitter.py` | 将文本拆分为片段 |

| `embedding.py` | 生成文本向量 |

| `vector\_store.py` | 管理向量索引 |

| `retriever.py` | 检索相关内容 |

| `rag\_service.py` | 组织 RAG 查询流程 |

| `build\_index.py` | 构建或更新索引 |



以上文件名依据项目现有描述整理，实际功能及调用关系需要以源码为准。



RAG 与 Agent 的职责不同：RAG 负责提供相关知识，Agent 负责组织决策与工具调用。Agent 可以将 RAG 作为其中一个工具。



\## 💾 Database



项目描述中包含 SQLite 和 PostgreSQL 两种关系型数据库方案。



\### SQLite



SQLite 适合本地开发、轻量级测试和快速验证。它不需要独立数据库服务，部署和维护相对简单。



\### PostgreSQL



PostgreSQL 更适合需要独立数据库服务、多客户端并发访问和更丰富数据库管理能力的后端应用。



\### Data Model



项目中的核心业务实体包括：



\- \*\*User\*\*：用户与认证相关信息。

\- \*\*Conversation\*\*：聊天会话及其所属用户。

\- \*\*Message\*\*：会话中的消息内容与角色等信息。



概念关系：



```text

User

&#x20; |

&#x20; +---- Conversation

&#x20;               |

&#x20;               +---- Message

```



具体字段、外键关系、级联删除规则和数据库迁移方式请以 ORM 模型与迁移文件为准。



\## ⚡ Redis



Redis 可用于缓存、带过期时间的临时状态以及其他适合内存存储的数据。



项目文档中规划的用途包括：



\- 缓存

\- 临时状态

\- Token 相关数据（如果当前实现已接入）



Redis 不应被默认视为长期业务数据的唯一存储。用户、会话和消息等需要持久保存的数据，应依据实际设计存放在关系型数据库或其他持久化存储中。



\## 🐳 Docker \& Docker Compose



Docker 用于将应用及其运行依赖封装到镜像中；Docker Compose 用于定义和管理多个关联服务。



目标部署结构：



```text

Docker Compose

&#x20;     |

&#x20;     +-------- Backend

&#x20;     |

&#x20;     +-------- PostgreSQL

&#x20;     |

&#x20;     +-------- Redis

```



\### Docker Network



同一个 Compose 网络中的服务通常可以使用服务名称相互访问。



例如，若 Compose 中的 PostgreSQL 服务名称为 `postgres`，后端容器中的数据库连接地址可以采用类似形式：



```text

postgresql://<user>:<password>@postgres:5432/<database>

```



其中用户名、密码和数据库名称必须替换为实际配置值。



\*\*不要在容器内部使用 `localhost` 访问另一个容器中的数据库。\*\* 容器内的 `localhost` 通常指当前容器本身。



\### Persistent Data



对于需要长期保存的数据，应确认 Compose 是否为数据库配置了持久化卷。容器生命周期不应与业务数据的保存周期混为一谈。



\## 🚀 Getting Started



\### Prerequisites



\- Python 版本应符合项目依赖要求。

\- pip

\- Git

\- 可用的模型 API 凭据

\- SQLite 或已配置的 PostgreSQL（按运行模式选择）

\- Redis（如果当前运行配置需要）

\- Docker 与 Docker Compose（如果使用容器化方式）



\### 1. Clone the repository



```bash

git clone https://github.com/lizzy184/AIChat.git

cd AIChat/AIchatBackend

```



\### 2. Create a virtual environment



```bash

python -m venv .venv

```



激活虚拟环境：



Linux / macOS：



```bash

source .venv/bin/activate

```



Windows PowerShell：



```powershell

.venv\\Scripts\\Activate.ps1

```



\### 3. Install dependencies



```bash

pip install -r requirements.txt

```



\### 4. Configure environment variables



根据项目实际配置创建 `.env` 文件。以下为配置项示例，具体名称应与代码中的配置读取逻辑一致：



```dotenv

SILICON\_API\_KEY=

DATABASE\_URL=

JWT\_SECRET\_KEY=

JWT\_ALGORITHM=HS256

JWT\_EXPIRE\_MINUTES=15

REFRESH\_TOKEN\_EXPIRE\_DAYS=7

REDIS\_HOST=localhost

REDIS\_PORT=6379

```



说明：



\- 填写当前环境实际使用的模型 API Key。

\- `DATABASE\_URL` 应与当前数据库类型及连接配置一致。

\- `JWT\_SECRET\_KEY` 必须使用安全的随机密钥。

\- Redis 地址取决于运行环境；如果后端运行在 Docker 容器内，通常需要使用 Compose 服务名称，而不是宿主机的 `localhost`。



不要将包含真实密钥的 `.env` 文件提交到 Git。



\### 5. Start the backend



如果项目入口确实是 `AIchatBackend/main.py`，且其中定义了 `app` 对象，可以使用：



```bash

uvicorn main:app --reload

```



如果入口文件位于 Python 包内，请将命令调整为对应模块路径。



\### 6. Verify the service



在本地开发环境中访问：



\- Swagger UI：`http://localhost:8000/docs`

\- OpenAPI Schema：`http://localhost:8000/openapi.json`



这些地址假定服务监听本机的 8000 端口。如果配置了其他端口，应使用实际地址。



\## 🐳 Run with Docker Compose



如果仓库根目录存在已经配置好的 `docker-compose.yml`，可以从仓库根目录尝试：



```bash

docker compose up --build

```



查看服务状态：



```bash

docker compose ps

```



查看日志：



```bash

docker compose logs -f

```



停止服务：



```bash

docker compose down

```



执行这些命令前，请确认 Compose 文件中的服务名称、环境变量、端口映射和数据卷配置。若 Compose 文件不存在或尚未完整配置，应先补齐实际配置，再将容器化启动作为可复现的运行方式写入项目文档。



\## 🔧 Troubleshooting



\### Database connection failed



检查数据库服务状态、连接字符串、用户名、密码和网络配置。



\### Model API request failed



检查 API Key、模型服务地址、网络连接、模型参数和服务端错误日志。



\### Authentication failed



检查 Token 的签名算法、密钥、有效期以及客户端请求头。



\### Streaming output terminates unexpectedly



检查模型生成流程、SSE 事件格式、异常传播和客户端断开处理。



\### Docker service cannot connect to another service



检查 Compose 网络、服务名称、容器内端口和环境变量。容器间通信通常使用服务名称。



\## 🔮 Future Improvements



\- 自动化测试与接口回归测试

\- 更完善的异常处理和日志追踪

\- API Rate Limiting

\- 生产环境 HTTPS 与反向代理

\- 数据库迁移与备份流程

\- RAG 检索质量评估

\- Embedding 与文本切分策略优化

\- Agent 工作流评估与工具扩展

\- CI/CD 与自动化部署

\- 生产环境监控与性能优化



\## 👨‍💻 Author



\*\*lizzy184\*\*



GitHub: https://github.com/lizzy184/AIChat



