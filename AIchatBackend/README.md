# AIChatBackend

AIChatAndroid 的后端服务，使用 **Python + FastAPI + Pydantic + SQLAlchemy + SQLite + 密码哈希 + Docker** 构建，为 Android 客户端提供用户系统、AI 聊天以及聊天历史接口。

本项目是暑假 **Android + AI 应用工程**学习路线中的 Backend 部分。

> 当前版本重点：用户注册/登录、密码哈希、用户 ID、聊天数据持久化、历史聊天查询、前后端 API 联调以及 Docker 容器化。
>
> RAG、Agent、OCR、PDF 问答等属于后续扩展方向。

---

# 一、项目介绍

Backend 的主要职责：

```text
Android App
    ↓
Retrofit
    ↓
FastAPI
    ↓
Router
    ↓
Service / Database
    ↓
SQLite
```

聊天相关：

```text
Android
    ↓
POST /chat
    ↓
FastAPI
    ↓
保存用户消息
    ↓
调用 AI
    ↓
保存 AI 回复
    ↓
返回 Android
```

用户系统：

```text
Register
    ↓
创建 User
    ↓
生成 user_id
    ↓
保存密码哈希
    ↓
返回 user_id
```

登录：

```text
Login
    ↓
查询 username
    ↓
获取 password_hash
    ↓
verify_password()
    ↓
验证成功
    ↓
返回 user_id
```

---

# 二、主要功能

## 1. 用户注册

接口：

```http
POST /register
```

主要逻辑：

```text
接收注册信息
    ↓
查询 username 是否已经存在
    ↓
不存在
    ↓
密码进行哈希
    ↓
创建 User
    ↓
保存数据库
    ↓
返回 user_id
```

如果用户名已经存在，则数据库的唯一约束可以阻止重复用户。

---

# 三、用户登录

接口：

```http
POST /login
```

登录核心逻辑：

```text
username
password
    ↓
查询 User
    ↓
user.username == username
    ↓
获取 password_hash
    ↓
verify_password(
    password,
    password_hash
)
    ↓
验证成功
    ↓
返回 user_id
```

密码不会以明文形式保存到数据库。

数据库保存的是：

```text
password_hash
```

而不是：

```text
password
```

---

# 四、密码安全

项目使用密码哈希方案。

核心思想：

```text
用户输入密码
    ↓
hash
    ↓
password_hash
    ↓
数据库
```

登录时：

```text
用户输入密码
    ↓
verify
    ↓
数据库中的 password_hash
    ↓
True / False
```

因此数据库中不应该直接保存用户明文密码。

---

# 五、数据库

当前后端数据库使用：

```text
SQLite
```

主要数据表：

## users

```text
users
├── id
├── username
├── password_hash
└── created_time
```

其中：

- `id`：用户唯一 ID
- `username`：用户名
- `password_hash`：密码哈希
- `created_time`：创建时间

---

## messages

```text
messages
├── id
├── user_id
├── role
├── content
└── created_time
```

其中：

- `id`：消息 ID
- `user_id`：所属用户
- `role`：消息角色
- `content`：聊天内容
- `created_time`：创建时间

---

# 六、用户隔离

这是当前用户系统最重要的设计之一。

聊天记录不是简单地：

```text
所有用户
    ↓
同一个聊天记录列表
```

而是：

```text
user_id = 1
    ↓
用户 A 的聊天记录

user_id = 2
    ↓
用户 B 的聊天记录
```

Android 请求历史：

```http
GET /history?user_id=1
```

后端根据：

```text
user_id
```

查询对应用户的消息。

因此：

```text
用户 A 登录
    ↓
user_id = 1
    ↓
查询 user_id = 1 的聊天记录

用户 B 登录
    ↓
user_id = 2
    ↓
查询 user_id = 2 的聊天记录
```

这就是当前项目实现用户聊天数据隔离的核心机制。

---

# 七、API

当前项目涉及的主要接口：

```http
POST /register
POST /login
POST /chat
GET /history
DELETE /history
```

---

## `POST /register`

作用：

```text
注册用户
```

返回：

```json
{
  "user_id": 1
}
```

---

## `POST /login`

作用：

```text
验证用户登录
```

成功返回：

```json
{
  "user_id": 1
}
```

---

## `POST /chat`

作用：

```text
发送聊天消息
```

核心请求数据包含：

```text
user_id
message
```

后端可以根据 `user_id` 将消息归属于对应用户。

---

## `GET /history`

作用：

```text
获取指定用户历史聊天
```

例如：

```http
GET /history?user_id=1
```

---

## `DELETE /history`

作用：

```text
删除聊天历史
```

当前接口用于聊天记录清理。

后续可以继续完善为明确的：

```text
DELETE /history?user_id=1
```

以进一步加强用户隔离。

---

# 八、技术栈

## Python

- Python
- 函数
- 类
- 字典
- 异常处理
- 模块
- 虚拟环境

---

## FastAPI

- FastAPI
- Router
- Dependency Injection
- HTTP API
- JSON
- HTTPException
- Depends
- 请求参数
- Response

---

## Pydantic

用于：

```text
Request Model
Response Model
数据验证
```

例如：

```text
LoginRequest
RegisterRequest
```

---

## SQLAlchemy

用于：

```text
Python
    ↓
SQLAlchemy ORM
    ↓
SQLite
```

核心概念：

- Engine
- Session
- Model
- Query
- ORM
- `db.query()`
- `filter()`
- `first()`
- `commit()`

---

## SQLite

当前项目使用 SQLite 保存：

```text
users
messages
```

优点：

- 配置简单
- 不需要单独安装数据库服务器
- 非常适合学习项目
- Docker 中容易运行

---

## 密码哈希

项目使用密码哈希机制保存密码。

核心概念：

```text
hash_password()
verify_password()
password_hash
```

---

## Docker

后端已经完成 Docker 基础容器化。

核心文件：

```text
Dockerfile
.dockerignore
requirements.txt
```

核心命令：

```bash
docker build
docker run
```

---

# 九、Docker 工作流程

项目 Docker 化流程：

```text
Backend Source Code
        ↓
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
FastAPI
```

例如：

```bash
docker build -t aichat-backend .
```

运行：

```bash
docker run -p 8000:8000 aichat-backend
```

然后访问：

```text
http://localhost:8000/docs
```

即可打开 FastAPI Swagger 文档。

---

# 十、Docker 与数据库

当前学习阶段采用的设计是：

```text
Docker Container
    ↓
运行 FastAPI
    ↓
启动时创建 SQLite 数据库
```

不要简单理解为：

```text
把电脑上的旧数据库
    ↓
COPY 到 Docker Image
```

更合理的开发学习方式是：

```text
Docker Image
    ↓
只包含后端运行环境和代码
    ↓
Container 启动
    ↓
应用初始化数据库
```

这样镜像不会绑定你本机测试时的旧数据库数据。

---

# 十一、项目运行

## 方式一：本地 Python

进入 Backend：

```bash
cd backend
```

创建虚拟环境：

```bash
python -m venv .venv
```

激活：

Windows PowerShell：

```powershell
.venv\Scripts\Activate.ps1
```

安装依赖：

```bash
pip install -r requirements.txt
```

启动：

```bash
uvicorn main:app --reload
```

---

# 十二、Docker 运行

构建：

```bash
docker build -t aichat-backend .
```

运行：

```bash
docker run -p 8000:8000 aichat-backend
```

查看容器：

```bash
docker ps
```

查看所有容器：

```bash
docker ps -a
```

停止：

```bash
docker stop <container_id>
```

删除：

```bash
docker rm <container_id>
```

---

# 十三、Swagger API 测试

启动 Backend 后访问：

```text
http://localhost:8000/docs
```

可以测试：

```text
/register
/login
/chat
/history
```

推荐测试顺序：

```text
1. register
      ↓
2. 获取 user_id
      ↓
3. login
      ↓
4. 获取 user_id
      ↓
5. chat
      ↓
6. history
```

然后再进行：

```text
Android
    ↓
Retrofit
    ↓
Docker FastAPI
```

前后端联调。

---

# 十四、项目结构

当前后端核心结构可以保持为：

```text
backend/
│
├── main.py
│
├── routers/
│   ├── user.py
│   └── ...
│
├── models/
│   └── ...
│
├── schemas/
│   └── ...
│
├── utils/
│   └── security.py
│
├── database/
│   └── ...
│
├── requirements.txt
├── Dockerfile
├── .dockerignore
└── README.md
```

具体文件名以当前 Backend 实际项目结构为准。

---

# 十五、Android 与 Backend 联调

完整链路：

```text
                    AIChatAndroid
                         │
                         │ Retrofit
                         ↓
                    FastAPI Backend
                         │
              ┌──────────┴──────────┐
              ↓                     ↓
           SQLite                 AI API
              │                     │
              └──────────┬──────────┘
                         ↓
                    Chat Response
                         ↓
                    Android UI
```

---

# 十六、用户系统完整流程

第一次使用：

```text
打开 App
    ↓
DataStore
    ↓
没有 user_id
    ↓
has_registered = false
    ↓
RegisterScreen
```

注册：

```text
RegisterScreen
    ↓
POST /register
    ↓
Backend
    ↓
创建 User
    ↓
返回 user_id
    ↓
DataStore 保存 user_id
    ↓
ChatScreen
```

关闭 App：

```text
再次打开
    ↓
读取 user_id
    ↓
存在
    ↓
ChatScreen
```

退出登录：

```text
退出登录
    ↓
清除本地 user_id
    ↓
再次启动
    ↓
没有 user_id
    ↓
has_registered = true
    ↓
LoginScreen
```

登录：

```text
LoginScreen
    ↓
POST /login
    ↓
Backend 验证
    ↓
返回 user_id
    ↓
DataStore 保存
    ↓
ChatScreen
```

---

# 十七、当前项目完成度

## 已完成 / 已建立

- [x] FastAPI 后端
- [x] Router
- [x] Pydantic 请求模型
- [x] SQLAlchemy ORM
- [x] SQLite
- [x] users 表
- [x] messages 表
- [x] 注册接口
- [x] 登录接口
- [x] user_id
- [x] 密码哈希机制
- [x] 密码验证机制
- [x] 聊天接口
- [x] 历史聊天接口
- [x] 基于 user_id 的聊天数据隔离
- [x] Swagger API 测试
- [x] Dockerfile
- [x] Docker build
- [x] Docker run
- [x] Docker 容器运行 FastAPI
- [x] Android 与 Backend 基础联调

## 正在完善

- [ ] Android 端密码字段与后端密码登录完全同步
- [ ] 更完善的 HTTP 错误码设计
- [ ] 更完整的异常处理
- [ ] 删除历史记录时明确使用 user_id
- [ ] 环境变量管理
- [ ] Docker Compose
- [ ] 数据库持久化卷
- [ ] 更完善的生产环境配置

## 后续扩展

- [ ] JWT
- [ ] Token
- [ ] Refresh Token
- [ ] SSE 流式输出
- [ ] Markdown
- [ ] 图片上传
- [ ] PDF
- [ ] RAG
- [ ] OCR
- [ ] Agent
- [ ] MCP

这些内容属于后续学习方向，不代表当前版本已经完成。

---

# 十八、学习目标

通过 Backend 项目掌握：

```text
Python
 ↓
FastAPI
 ↓
REST API
 ↓
Pydantic
 ↓
SQLAlchemy
 ↓
SQLite
 ↓
用户系统
 ↓
密码安全
 ↓
Android 联调
 ↓
Docker
 ↓
部署
```

最终目标：

> 能够独立完成一个 Android + AI + Backend 的完整项目。

---

# 十九、项目定位

本项目是个人学习与作品项目。

核心方向：

> **Android + AI 应用工程**

通过一个完整 AI Chat App，将：

```text
Android
+
Kotlin
+
Compose
+
网络
+
FastAPI
+
数据库
+
AI
+
Docker
+
GitHub
```

串联起来。

最终目标不是只会写某一个功能，而是能够理解一个完整应用从：

```text
客户端
    ↓
API
    ↓
后端
    ↓
数据库
    ↓
AI
    ↓
部署
```

的完整工程流程。
