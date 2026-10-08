AIChat

A full-stack AI chat application built with Android, FastAPI, LangGraph Agent, RAG and Docker.

AIChat 是一个完整的全栈 AI 应用项目。

项目包含：

Android 客户端
FastAPI 后端服务
LLM Streaming Chat
LangGraph Agent Workflow
RAG 知识库
JWT 用户认证
PostgreSQL 数据持久化
Redis 缓存
Docker Compose 部署

项目目标：

通过实际工程实践，学习并实现：

Android AI 应用开发
Backend API 设计
Agent Workflow
RAG Retrieval
SSE Streaming
Authentication
Database Engineering
Container Deployment
✨ Features
AI Chat

支持类似 ChatGPT 的实时对话体验。

功能：

Streaming Response
SSE 数据流
实时 UI 更新
Chat History 保存

流程：

Android

↓

FastAPI

↓

Agent Workflow

↓

LLM

↓

SSE Streaming

↓

Android Compose UI
🤖 AI Agent & RAG

项目集成 LangGraph Agent。

Agent 负责根据用户问题判断：

是否直接回答
是否调用知识库
是否调用工具

架构：

User Message

        ↓

   LangGraph Agent

        ↓

   Agent State

        ↓

    LLM Node

        ↓

 Tool Decision

        ↓

 ┌───────────────┐
 │               │
 ↓               ↓

Knowledge      Calculator
 Tool             Tool

 ↓               ↓

RAG Search    Calculation

        ↓

   Final Answer
📚 RAG Knowledge Base

项目支持 PDF 文档知识库。

完整流程：

PDF Upload

↓

PDF Parser

↓

Text Extraction

↓

Chunk Split

↓

Embedding

↓

Vector Index

↓

Retriever

↓

Knowledge Context

↓

LLM Answer

用户可以：

上传 PDF
构建知识库
基于文档进行问答

RAG 模块：

rag/

├── loader.py

├── splitter.py

├── embedding.py

├── vector_store.py

├── retriever.py

├── rag_service.py

└── build_index.py
🛠 Tech Stack
Android
Technology	Purpose
Kotlin	Android 开发语言
Jetpack Compose	UI 开发
Material 3	UI Component
MVVM	Application Architecture
ViewModel	State Management
Kotlin Coroutines	Async Task
StateFlow	Reactive UI Update
Retrofit	HTTP Communication
OkHttp	Network Layer / SSE
Room	Local Database
DataStore	Token Storage
Navigation Compose	Page Navigation
Hilt	Dependency Injection
Backend
Technology	Purpose
Python	Backend Language
FastAPI	Web API Framework
SQLAlchemy	ORM
PostgreSQL	Production Database
SQLite	Development Database
Pydantic	Data Validation
JWT	Authentication
Bcrypt	Password Hash
OAuth2 Bearer Token	API Security
Uvicorn	ASGI Server
AI Stack
Technology	Purpose
LLM API	AI Conversation
LangGraph	Agent Workflow
Agent State	Workflow State Management
Tool Calling	External Capability
Embedding Model	Vector Representation
Vector Search	Knowledge Retrieval
RAG Pipeline	Retrieval Augmented Generation
DevOps
Technology	Purpose
Docker	Containerization
Docker Compose	Multi Container Management
Linux	Deployment Environment
Redis	Cache / Temporary Data
PostgreSQL	Database Service
🏗 System Architecture

整体架构：

                 Android App

                     |

                     ↓

              FastAPI Backend

                     |

          ┌──────────┴──────────┐

          ↓                     ↓

 Authentication             AI Service

    JWT                     Agent

                                  |

                                  ↓

                            LangGraph

                                  |

                ┌─────────────────┴───────────────┐

                ↓                                 ↓

          RAG Knowledge Tool              Calculator Tool

                |

                ↓

           Vector Retrieval

                |

                ↓

                LLM


                     |

                     ↓

             PostgreSQL / Redis
📱 Android Architecture

Android 使用 MVVM 架构。

结构：

UI Layer

    ↓

ViewModel

    ↓

Repository

    ↓

Network / Database

例如 Chat：

ChatScreen

↓

ChatViewModel

↓

ChatRepository

↓

OkHttp SSE

↓

FastAPI

↓

Agent

↓

LLM
🔐 Authentication

项目使用 JWT 实现用户认证。

认证流程：

Login

↓

FastAPI

↓

Generate JWT

↓

Access Token

+

Refresh Token

↓

Android DataStore

支持：

Access Token
Refresh Token
Token 自动刷新
401 自动重试

请求：

Authorization:

Bearer <access_token>



💾 Database Design

项目使用关系型数据库保存用户、聊天记录以及业务数据。

Development

开发阶段：

SQLite

SQLite 适合：

本地开发
单用户测试
快速验证功能
Production

生产环境：

PostgreSQL

原因：

相比 SQLite，PostgreSQL 更适合服务端应用：

多用户访问
并发连接
完整事务支持
更强的数据管理能力

数据库架构：

User

 |

 |

 └──── Message


Conversation

 |

 |

 └──── Message

主要数据：

User

保存：

username
password_hash
created_time
Conversation

保存：

conversation_id
user_id
title
created_time
Message

保存：

conversation_id
role
content
created_time
⚡ Redis

项目引入 Redis 作为缓存和临时状态存储。

Redis 主要用于：

Cache
Token 相关数据
临时状态

使用的数据结构：

类型	使用场景
String	简单缓存
Hash	用户相关数据
List	队列场景
Set	集合数据
TTL	自动过期数据

Redis 优势：

内存存储
高性能读写
支持过期策略

项目不会将所有数据存入 Redis。

原则：

PostgreSQL

负责：

长期业务数据


Redis

负责：

高速访问数据
临时状态
缓存
🐳 Docker

项目使用 Docker 实现 Backend 容器化。

Docker 核心组件：

Image

镜像：

应用运行模板。

包含：

Python 环境
项目代码
Dependencies
Container

容器：

镜像运行后的实例。

Backend：

Docker Image

↓

Container

↓

FastAPI Service
Volume

用于数据持久化。

例如：

数据库数据不能依赖 Container 生命周期。

如果：

docker rm container

容器删除：

数据可能丢失。

因此：

使用 Volume 保存：

PostgreSQL 数据
Redis 数据
Network

Docker Network 用于容器之间通信。

例如：

Backend Container

        |

        ↓

Docker Network

        |

 ┌──────┴──────┐

 ↓             ↓

PostgreSQL    Redis

容器内部不能使用：

localhost

应该使用：

service name

例如：

DATABASE_URL=
postgresql://user:password@postgres:5432/database
🐳 Docker Compose

项目使用 Docker Compose 管理多个服务。

当前架构：

             Docker Compose


                  |

        ┌─────────┼─────────┐

        ↓         ↓         ↓


    Backend   PostgreSQL   Redis


        |

        ↓

    FastAPI API

Compose 负责：

Service 管理
Container 创建
Network 配置
Environment 注入
Volume 持久化

示例：

services:

  backend:

  postgres:

  redis:
🚀 Deployment Architecture

完整部署链路：

Linux Server


      ↓


Docker


      ↓


Docker Compose


      ↓


FastAPI Container


      ↓


PostgreSQL + Redis


      ↓


AI API Service
🐧 Linux Deployment

项目按照服务器环境进行部署模拟。

常用 Linux 操作：

cd

ls

pwd

grep

cat

vim

ps

top

curl

ssh

排查流程：

查看服务
ps
查看端口
ss

例如：

检查 FastAPI：

curl localhost:8000/docs
查看日志
cat

tail
🔧 Environment Configuration

敏感配置不直接写入代码。

使用：

.env

例如：

SILICON_API_KEY=

DATABASE_URL=

JWT_SECRET_KEY=

JWT_ALGORITHM=HS256

JWT_EXPIRE_MINUTES=15

REFRESH_TOKEN_EXPIRE_DAYS=7

REDIS_HOST=redis

REDIS_PORT=6379

不会提交：

.env

通过：

.gitignore

.dockerignore

避免敏感信息进入：

Git Repository
Docker Build Context
📂 Project Structure

整体结构：

AIChat

├── AIchatAndroid

│   ├── app

│   ├── ui

│   ├── viewmodel

│   ├── repository

│   └── network


├── AIchatBackend

│   ├── app

│   ├── api

│   ├── models

│   ├── database

│   ├── agent

│   ├── rag

│   ├── config.py
│   └── Dockerfile


├── docker-compose.yml

├── .env.example

├── .gitignore

├── .dockerignore

└── README.md
🚀 Quick Start
1. Clone Project
git clone https://github.com/lizzy184/AIChat.git

cd AIChat
Backend

进入：

cd AIchatBackend

创建环境：

python -m venv .venv

安装依赖：

pip install -r requirements.txt

配置：

创建：

.env

填写：

SILICON_API_KEY=

DATABASE_URL=

JWT_SECRET_KEY=

启动：

uvicorn main:app --reload

访问：

http://localhost:8000/docs
Docker Compose Start

启动全部服务：

docker compose up --build

启动后：

Backend

localhost:8000


PostgreSQL

5432


Redis

6379
📈 Current Project Status

目前已经完成：

Android

✅ Kotlin Android Application

✅ Jetpack Compose

✅ Material 3

✅ MVVM Architecture

✅ ViewModel

✅ Coroutines

✅ StateFlow

✅ Retrofit

✅ OkHttp SSE

✅ Room

✅ DataStore

✅ Navigation

✅ Hilt Dependency Injection

✅ JWT Token Management

AI System

✅ Streaming AI Chat

✅ LLM API Integration

✅ LangGraph Agent Workflow

✅ Agent State

✅ Tool Calling

✅ Knowledge Base Tool

✅ Calculator Tool

✅ Agent + RAG Integration

RAG

✅ PDF Upload

✅ PDF Parsing

✅ Text Chunking

✅ Embedding

✅ Vector Index

✅ Retriever

✅ Knowledge Query

Backend

✅ FastAPI

✅ REST API

✅ SSE Streaming

✅ JWT Authentication

✅ Password Hash

✅ SQLAlchemy

✅ PostgreSQL Support

✅ Conversation Management

✅ Logging System

DevOps

✅ Docker

✅ Dockerfile

✅ Docker Image

✅ Docker Container

✅ Docker Compose

✅ PostgreSQL Container

✅ Redis Container

✅ .gitignore

✅ .dockerignore

✅ Linux Deployment Practice

🔮 Future Plan
AI Agent
Agent Streaming Optimization
More Tools
Multi-Agent Workflow
Agent Evaluation
RAG
Better Chunk Strategy
Embedding Optimization
Vector Database Integration
Retrieval Evaluation
Backend
API Rate Limiting
Monitoring
HTTPS
Better Logging
Automated Testing
DevOps
CI/CD
Production Reverse Proxy
Cloud Deployment
📌 Project Positioning

AIChat 不只是一个简单 AI Demo。

项目完整结合：

Android

+

FastAPI

+

JWT Authentication

+

SSE Streaming

+

LangGraph Agent

+

RAG Knowledge Base

+

PostgreSQL

+

Redis

+

Docker Compose

+

Linux Deployment

形成：

一个完整的 Full-stack AI Application。

👨‍💻 Author

lizzy184

GitHub:

https://github.com/lizzy184/AIChat

如果这个项目对你有帮助，欢迎 Star ⭐

