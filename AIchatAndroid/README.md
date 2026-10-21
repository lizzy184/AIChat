# AIChat Android

AIChat Android 是 AIChat 全栈 AI 聊天应用的 Android 客户端，使用 Kotlin 和 Jetpack Compose 构建，通过后端 API 与 AI 服务交互，支持聊天会话、AI 流式回复以及用户认证等功能。

本项目重点实践 Android 原生应用开发、声明式 UI、状态管理、网络通信和客户端分层架构。

## ✨ Features

### AI Chat

- AI 对话界面
- 用户消息与 AI 回复展示
- AI 回复流式显示
- 聊天消息与时间展示
- Markdown 格式的 AI 回复
- 聊天会话与历史消息管理

### Authentication

- 用户登录
- JWT Token 管理
- 通过认证信息访问后端 API
- Token 持久化及相关认证状态管理

具体的 Token 刷新和失败重试行为以当前网络层实现为准。

### UI & Navigation

- Jetpack Compose 声明式 UI
- Material 3 组件与主题
- 页面导航
- 深色与浅色主题切换
- 设置页面与关于页面

### Local Data & State

- ViewModel 管理页面状态
- StateFlow 驱动 UI 更新
- Kotlin Coroutines 处理异步任务
- DataStore 保存应用设置及实际接入的持久化信息
- Room 用于项目中实际接入的本地数据库功能

## 🛠 Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Android 开发语言 |
| Jetpack Compose | 声明式 UI |
| Material 3 | UI 组件与主题系统 |
| MVVM | 客户端架构 |
| ViewModel | 页面状态管理 |
| Kotlin Coroutines | 异步任务处理 |
| StateFlow | 响应式状态传递 |
| Hilt | 依赖注入 |
| Navigation Compose | 页面导航 |
| Retrofit | HTTP API 通信 |
| OkHttp | 网络通信与流式响应相关处理 |
| Room | 本地关系型数据库 |
| DataStore | 应用设置及实际接入的持久化数据 |

## 🏗 Architecture

客户端采用以 MVVM 为核心的分层设计。

```text
Compose UI
    |
    v
ViewModel
    |
    v
Repository
    |
    +-------------------+
    |                   |
    v                   v
Network API         Local Storage
    |                Room / DataStore
    v
FastAPI Backend
    |
    v
AI Service
```

各层的职责如下：

- **UI Layer**：使用 Compose 构建页面，负责展示数据和接收用户操作。
- **ViewModel Layer**：管理页面状态，调用业务方法，并通过 StateFlow 等状态流向 UI 提供数据。
- **Repository Layer**：封装数据访问逻辑，协调网络请求和本地数据来源。
- **Network Layer**：通过 HTTP API 与后端通信，并处理项目中实际实现的流式响应。
- **Local Storage**：使用 Room、DataStore 保存各自负责的本地数据。

具体模块和类名以项目源码为准。

## 💬 Chat Request Flow

一次典型的聊天交互过程如下：

```text
User Input
    |
    v
Chat Screen
    |
    v
ChatViewModel
    |
    v
ChatRepository
    |
    v
Network / SSE Client
    |
    v
FastAPI Backend
    |
    v
AI Agent / LLM
    |
    v
Streaming Response
    |
    v
Update Message State
    |
    v
Compose UI Recomposition
```

在流式响应过程中，客户端接收后端逐步返回的数据，并更新正在生成的消息状态。Compose 根据状态变化刷新界面，从而呈现连续输出的聊天体验。

实际的事件格式、消息结束标记、异常处理和取消机制以客户端与后端的协议实现为准。

## 🔐 Authentication

客户端使用 JWT 相关认证机制访问需要身份验证的后端接口。

典型流程：

1. 用户提交登录信息。
2. 客户端向后端发起登录请求。
3. 后端返回认证信息。
4. 客户端按实际实现保存 Token。
5. 后续受保护请求携带认证信息。

常见的 Bearer Token 请求头形式如下：

```http
Authorization: Bearer <access_token>
```

如果项目已实现 Refresh Token、自动刷新及 401 重试，应以当前认证拦截器、Repository 和后端接口的实际逻辑为准。

## 🎨 Theme & UI State

应用使用 Material 3 的 `MaterialTheme` 管理主题配色。

深色与浅色模式通过主题状态控制，并由设置页面修改。主题偏好可以通过 DataStore 持久化，使用户重新打开应用后继续使用已选择的模式。

UI 状态由 ViewModel 提供，Compose 订阅状态并在数据变化时更新页面。

## 📁 Project Structure

以下为客户端的逻辑结构示意，具体目录以实际仓库为准。

```text
AIchatAndroid/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/aichatapp/
│           │       ├── navigation/
│           │       ├── ui/
│           │       ├── view/
│           │       ├── viewmodel/
│           │       ├── repository/
│           │       ├── network/
│           │       └── model/
│           └── AndroidManifest.xml
├── build.gradle.kts
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Android Studio
- 与项目 Gradle 配置兼容的 JDK
- Android SDK
- Android 模拟器或 Android 真机
- 可访问的 AIChat Backend 服务

项目的 SDK、JDK 和 Gradle 版本请以仓库配置文件为准。

### 1. Clone the repository

```bash
git clone https://github.com/lizzy184/AIChat.git
cd AIChat/AIchatAndroid
```

### 2. Open the project

使用 Android Studio 打开 `AIchatAndroid` 目录，等待 Gradle Sync 完成。

### 3. Configure the backend address

确认客户端网络配置中的 API Base URL 指向当前运行的后端服务。

常见开发环境：

- **Android 模拟器访问宿主机服务**：通常使用 `http://10.0.2.2:8000/` 访问宿主机的本地 8000 端口。
- **Android 真机访问电脑上的服务**：通常需要使用电脑在局域网中的 IP 地址，并确保手机与电脑网络互通。
- **远程部署环境**：使用实际部署的服务器地址及正确配置的 HTTPS。

以上地址只是常见示例，应根据项目实际网络配置和运行环境调整。不要直接假定 Android 中的 `localhost` 指向开发电脑。

### 4. Run the application

1. 在 Android Studio 中完成 Gradle Sync。
2. 选择模拟器或连接真机。
3. 运行 Android 应用模块。
4. 确认后端服务可访问后，测试登录与聊天功能。

也可以根据项目实际的 Gradle 配置使用命令行构建：

```bash
./gradlew assembleDebug
```

Windows 环境可使用：

```powershell
gradlew.bat assembleDebug
```

## 🧪 Verification Checklist

运行后建议检查：

- [ ] 应用可以正常启动。
- [ ] 登录请求能够连接后端。
- [ ] 认证信息可以按预期保存。
- [ ] 可以创建或选择聊天会话。
- [ ] 用户消息能够发送。
- [ ] AI 回复可以正常显示。
- [ ] 流式输出过程中 UI 能够持续更新。
- [ ] Markdown 消息显示正常。
- [ ] 深色与浅色主题切换正常。
- [ ] 重启应用后，已持久化的设置仍然生效。

## 🔧 Troubleshooting

### Backend connection failed

检查后端是否运行、Base URL 是否正确，以及模拟器或真机是否能够访问服务。

### Login or authorization failed

检查登录参数、Token 保存逻辑、请求头和后端认证配置。

### Streaming response is not displayed correctly

检查客户端流式响应解析逻辑、后端事件格式、结束事件和异常处理。

### UI colors are difficult to read

检查 `Theme.kt` 中的 Material 3 颜色定义，以及聊天气泡和 Markdown 组件是否使用了硬编码颜色。

## 🔮 Future Improvements

- 完善网络异常和流式请求取消处理
- 增加单元测试与 UI 测试
- 优化聊天列表性能
- 完善 Token 生命周期管理
- 改进无障碍支持与主题适配
- 优化加载、错误和空状态体验

## 👨‍💻 Author

**lizzy184**

GitHub: https://github.com/lizzy184/AIChat
