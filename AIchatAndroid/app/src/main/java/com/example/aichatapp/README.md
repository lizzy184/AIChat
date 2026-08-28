# AIChatAndroid

基于 **Kotlin + Jetpack Compose + MVVM + Retrofit + Room + DataStore + Coroutines/Flow + Hilt** 构建的 Android AI 聊天客户端。

本项目是暑假 **Android + AI 应用工程**学习路线中的客户端部分，目标不是单纯完成一个聊天页面，而是通过一个完整项目逐步掌握 Android UI、网络请求、状态管理、本地数据、用户系统以及前后端联调等工程能力。

> 当前项目重点：AI 聊天、聊天历史、用户注册/登录、登录状态持久化、前后端通信以及基础项目架构。
>
> RAG、Agent、OCR、PDF 问答等属于后续扩展方向，不作为当前版本已经完成的功能。

---

## 一、项目介绍

AIChatAndroid 是一个 Android AI Chat App 客户端。

核心流程：

```text
Android App
    ↓
Jetpack Compose UI
    ↓
ViewModel
    ↓
Repository
    ↓
Retrofit
    ↓
FastAPI Backend
    ↓
SQLite / AI API
```

用户可以：

- 注册账号
- 登录账号
- 保存当前用户 ID
- 退出登录
- 进入 AI 聊天页面
- 发送消息
- 获取 AI 回复
- 获取当前用户的历史聊天记录
- 查看历史聊天
- 清除聊天记录
- 在设置页面进行基础操作

---

# 二、当前主要功能

## 1. AI 聊天

聊天页面采用 Compose 构建。

主要组成：

```text
ChatScreen
├── MessageList
├── MessageItem
├── EmptyState
├── LoadingMessage
└── ChatInput
```

支持：

- 用户消息显示
- AI 消息显示
- 输入框
- 发送按钮
- 加载状态
- 空状态
- 重新发送相关逻辑
- 聊天历史加载

---

## 2. 用户注册

注册流程：

```text
RegisterScreen
      ↓
RegisterViewModel
      ↓
ChatRepository
      ↓
Retrofit
      ↓
POST /register
      ↓
FastAPI
      ↓
数据库创建用户
      ↓
返回 user_id
      ↓
Android DataStore 保存 user_id
      ↓
进入 ChatScreen
```

注册成功后，Android 会保存后端返回的 `user_id`。

---

## 3. 用户登录

登录流程：

```text
LoginScreen
      ↓
LoginViewModel
      ↓
ChatRepository
      ↓
Retrofit
      ↓
POST /login
      ↓
FastAPI 查询用户
      ↓
验证用户
      ↓
返回 user_id
      ↓
DataStore 保存 user_id
      ↓
进入 ChatScreen
```

当前 Android 项目中已经建立了：

- `LoginRequest`
- `LoginResponse`
- `LoginViewModel`
- `ChatRepository.login()`
- `ChatApi.login()`

后端已经加入密码哈希验证后，Android 端还需要同步增加密码字段并完成最终联调。

---

# 三、登录状态持久化

项目使用 **Jetpack DataStore Preferences** 保存用户状态。

核心类：

```text
data/UserPreferences.kt
```

当前保存的核心数据包括：

```text
user_id
has_registered
```

其中：

- `user_id`：表示当前设备上当前登录用户
- `has_registered`：表示该 App 是否已经完成过注册

启动时：

```text
StartScreen
    ↓
读取 DataStore
    ↓
user_id != null？
    ├── 是 → ChatScreen
    │
    └── 否
         ↓
    has_registered？
         ├── 是 → LoginScreen
         │
         └── 否 → RegisterScreen
```

因此可以实现：

```text
第一次打开
    ↓
注册页面

注册成功
    ↓
聊天页面

关闭 App 再打开
    ↓
如果 user_id 仍存在
    ↓
聊天页面

退出登录
    ↓
清除 user_id
    ↓
再次打开
    ↓
登录页面
```

---

# 四、聊天记录与用户隔离

项目存在两层数据存储：

## Android 本地

使用：

```text
Room
```

项目中包含：

```text
database/
├── AppDatabase.kt
├── DatabaseModule.kt
├── MessageDao.kt
└── MessageEntity.kt
```

Room 是 Android 本地数据库方案。

---

## Backend 服务端

当前聊天历史的核心数据由后端数据库保存。

Android 获取历史：

```http
GET /history?user_id=xxx
```

发送聊天：

```http
POST /chat
```

请求中携带：

```text
user_id
message
```

因此服务器可以根据：

```text
user_id
```

区分不同用户的聊天记录。

例如：

```text
用户 A
user_id = 1
    ↓
聊天记录 A

用户 B
user_id = 2
    ↓
聊天记录 B
```

当前项目的核心用户隔离依赖服务端 `user_id`。

> 注意：当前 Android `MessageEntity` 中还没有 `userId` 字段，Room 本地消息表的 DAO 查询也不是按照用户过滤。因此，如果未来把 Room 作为多用户本地聊天缓存，需要继续完善本地数据库的用户隔离设计。

---

# 五、项目架构

当前项目采用接近 **MVVM + Repository** 的结构。

```text
aichatapp/
│
├── data/
│   └── UserPreferences.kt
│
├── database/
│   ├── AppDatabase.kt
│   ├── DatabaseModule.kt
│   ├── MessageDao.kt
│   └── MessageEntity.kt
│
├── di/
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── model/
│   ├── ChatRequest.kt
│   ├── ChatResponse.kt
│   ├── ChatUiState.kt
│   ├── HistoryMessage.kt
│   ├── LoginRequest.kt
│   ├── LoginResponse.kt
│   ├── Message.kt
│   ├── RegisterRequest.kt
│   └── RegisterResponse.kt
│
├── navigation/
│   ├── AppNavigation.kt
│   ├── AppRoute.kt
│   └── BottomNavigationBar.kt
│
├── network/
│   ├── ChatApi.kt
│   └── NetworkResult.kt
│
├── repository/
│   └── ChatRepository.kt
│
├── view/
│   ├── chat/
│   ├── components/
│   ├── history/
│   ├── login/
│   ├── register/
│   ├── setting/
│   └── start/
│
├── viewmodel/
│   ├── ChatViewModel.kt
│   ├── HistoryViewModel.kt
│   ├── LoginViewModel.kt
│   └── RegisterViewModel.kt
│
├── MainActivity.kt
└── MyApplication.kt
```

---

# 六、技术栈

## Android

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- MVVM
- Repository Pattern
- ViewModel
- Hilt / Dependency Injection
- Retrofit
- OkHttp
- Gson
- Room
- DataStore Preferences
- Kotlin Coroutines
- Flow
- StateFlow
- `viewModelScope`
- `suspend` 函数

---

## Kotlin / 协程

项目已经实际使用协程相关技术：

```kotlin
viewModelScope.launch {
    ...
}
```

以及：

```kotlin
Flow
StateFlow
collect
first
```

主要用于：

- 网络请求
- 读取 DataStore
- 监听用户状态
- 加载聊天历史
- 更新 UI 状态
- 执行异步数据库操作

---

## 网络层

使用：

```text
Retrofit
    ↓
OkHttp
    ↓
FastAPI
```

主要接口：

```http
POST /register
POST /login
POST /chat
GET /history
DELETE /history
```

---

# 七、核心类说明

## `UserPreferences`

负责本地用户状态。

主要职责：

```text
保存 user_id
读取 user_id
清除 user_id
读取 has_registered
```

---

## `StartScreen`

负责启动时判断当前应该进入哪个页面。

判断逻辑：

```text
user_id
    ↓
判断是否已经登录

has_registered
    ↓
判断是否注册过
```

---

## `LoginViewModel`

负责：

```text
登录
↓
调用 Repository
↓
获取 user_id
↓
保存 DataStore
↓
通知 UI 登录成功
```

---

## `RegisterViewModel`

负责：

```text
注册
↓
调用 Repository
↓
获取 user_id
↓
保存 DataStore
↓
通知 UI 注册成功
```

---

## `ChatViewModel`

负责聊天业务状态：

```text
输入消息
发送消息
加载历史
处理 AI 回复
管理 Loading 状态
管理 UI State
```

---

## `ChatRepository`

负责连接：

```text
ViewModel
    ↓
Repository
    ↓
Retrofit API
```

Repository 将网络层返回的数据转换成 Android 使用的 Model，并统一处理网络错误。

---

# 八、网络请求示例

发送聊天：

```kotlin
api.chat(
    ChatRequest(
        user_id = userId,
        message = message
    )
)
```

获取历史：

```kotlin
api.getHistory(userId)
```

登录：

```kotlin
api.login(
    LoginRequest(username)
)
```

---

# 九、错误处理

项目定义：

```text
NetworkResult
```

用于表示：

```text
Success
Error
Loading
```

Repository 会处理：

- IOException
- HttpException
- Exception

从而避免 ViewModel 直接处理大量 Retrofit 细节。

---

# 十、依赖注入

项目使用 Hilt。

主要用于提供：

```text
Retrofit
OkHttpClient
ChatApi
UserPreferences
AppDatabase
MessageDao
ChatRepository
ViewModel
```

核心结构：

```text
Hilt
 ↓
Dependency Injection
 ↓
ViewModel
 ↓
Repository
 ↓
API / DataStore / Database
```

---

# 十一、当前项目完成度

### 已完成 / 已建立

- [x] Compose 聊天 UI
- [x] Compose 登录页面
- [x] Compose 注册页面
- [x] 设置页面
- [x] 历史聊天页面
- [x] Navigation
- [x] MVVM 基础结构
- [x] Repository 层
- [x] Retrofit
- [x] OkHttp
- [x] Hilt
- [x] Room 基础结构
- [x] DataStore
- [x] 用户 ID 保存
- [x] 注册流程
- [x] 登录流程基础结构
- [x] 退出登录
- [x] 启动页面登录状态判断
- [x] 根据 user_id 获取服务端历史聊天
- [x] 用户聊天记录服务端隔离的基础机制
- [x] Kotlin Coroutines
- [x] Flow
- [x] StateFlow

### 正在完善

- [ ] Android 登录页面与后端密码登录完全同步
- [ ] Android 注册页面与后端密码注册完全同步
- [ ] 更完整的登录/注册错误提示
- [ ] Room 本地消息的 `userId` 隔离
- [ ] 更完善的 UI State
- [ ] 更完善的网络异常处理

### 后续扩展

- [ ] Markdown
- [ ] 图片上传
- [ ] PDF 问答
- [ ] RAG
- [ ] OCR
- [ ] Agent
- [ ] MCP
- [ ] 更完整的会话管理
- [ ] Token/JWT 登录认证
- [ ] 多设备登录状态管理

这些内容属于项目后续发展方向，不代表当前版本已经实现。

---

# 十二、运行前准备

需要先运行 FastAPI Backend。

例如：

```text
Android
  ↓
Retrofit
  ↓
FastAPI Backend
```

确保 Android 中的 `BASE_URL` 指向当前运行的后端地址。

Android 模拟器访问电脑本机服务时，通常需要根据你的网络配置使用正确的主机地址，例如：

```text
http://10.0.2.2:8000/
```

具体以当前项目 `NetworkModule` 配置为准。

---

# 十三、学习目标

本项目不仅用于实现 AI Chat 功能，也用于学习完整的 Android 工程开发流程：

```text
Kotlin
 ↓
Compose
 ↓
MVVM
 ↓
ViewModel
 ↓
Repository
 ↓
Retrofit
 ↓
FastAPI
 ↓
Database
 ↓
Docker
 ↓
GitHub
```

通过项目逐步理解：

- UI 与业务逻辑分离
- 状态管理
- 异步编程
- 网络请求
- 数据持久化
- 用户系统
- 前后端通信
- 数据库
- Docker 部署
- GitHub 项目管理

---

# 十四、项目定位

本项目是个人学习与作品项目，核心方向：

> **Android + AI 应用工程**

重点不是堆叠大量技术，而是通过一个完整项目理解：

```text
客户端
+
后端
+
数据库
+
AI
+
部署
```

最终形成一个可以运行、可以展示、可以继续扩展的完整 AI 应用。

---

# 十五、后续路线

后续可以继续按照：

```text
项目整理
    ↓
GitHub
    ↓
Linux
    ↓
AI API 深入
    ↓
流式输出
    ↓
Markdown
    ↓
RAG
    ↓
Agent
```

逐步完善。

当前阶段优先保证：

> **基础功能稳定 + 项目结构清晰 + 能够独立解释每一层代码。**
