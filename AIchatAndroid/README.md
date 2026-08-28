AI Chat App

一个基于 Kotlin + Jetpack Compose + MVVM + Retrofit + Room + Hilt + AI API 开发的 Android AI 聊天应用。

本项目主要用于学习和实践现代 Android 应用开发，包括 Compose UI、MVVM 架构、网络请求、AI API 调用、本地数据库存储以及依赖注入。

📱 App 截图

说明：
下面的图片目前是占位路径。完成截图后，将图片放到项目根目录的 screenshots 文件夹中，并按照下面的文件名保存即可。

推荐准备 4 张截图：

chat.png：AI 聊天页面

history.png：历史记录页面

setting.png：设置页面

clear-history.png：清空聊天记录确认弹窗（可选）

AI 聊天页面

<!-- 【截图位置 1：在这里放 chat.png】 -->
![chat.png](screenshots/chat.png)


历史记录页面

<!-- 【截图位置 2：在这里放 history.png】 -->
![history.png](screenshots/history.png)


设置页面

<!-- 【截图位置 3：在这里放 setting.png】 -->
![setting.png](screenshots/setting.png)


清空聊天记录

<!-- 【截图位置 4：可选，在这里放 clear-history.png】 -->
![clearhistory.png](screenshots/clearhistory.png)


✨ 功能介绍

1. AI 对话

用户可以在聊天页面输入问题，并通过 AI API 获取 AI 回复。

聊天页面主要包括：

用户消息输入

AI 回复显示

加载状态显示

网络错误提示

失败后重新发送上一条消息

2. AI API

项目通过 Retrofit 调用 AI API。

网络层主要负责：

创建 Retrofit

配置 OkHttpClient

添加认证拦截器

发送聊天请求

处理网络异常

当前项目通过 BuildConfig.SILICON_API_KEY 提供 API Key，而不是直接将 Key 写死在 UI 代码中。

3. 聊天记录

项目使用 Room 保存聊天记录。

每条聊天记录包含：

消息文本

是否为用户消息

消息时间

数据库自增 ID

聊天记录在应用重新打开后仍然可以从本地数据库读取。

4. 历史记录

历史页面用于查看已经保存的聊天记录。

当前数据库查询使用：

ORDER BY id ASC

通过 Room 数据库记录的自增 ID 保证消息按照保存顺序显示：

较早消息
↓
较新消息

当前项目的时间字段保存为 HH:mm 格式，因此历史记录的实际排序依据是数据库自增 ID，而不是 time 字符串。

这种方式对于当前项目的消息先后顺序是可靠的，也避免了仅使用 HH:mm 时跨日期排序可能产生的问题。

5. 空状态

当数据库中没有聊天记录时，历史页面显示空状态，引导用户开始聊天。

6. 清空聊天记录

在设置页面可以清空所有聊天记录。

操作流程：

设置
↓
清空聊天记录
↓
确认弹窗
↓
点击「确认」
↓
ViewModel
↓
Repository
↓
Room DAO
↓
删除所有聊天记录

删除完成后，历史页面会自动更新。

7. 设置页面

设置页面目前包含：

深色模式设置入口

消息通知设置入口

关于 APP

清空聊天记录

如果某些设置目前只是 UI 开关，还没有真正改变整个 App 的行为，可以在后续版本继续完善。

8. 关于页面

项目提供 About 页面，用于展示 App 的相关信息。

🛠 技术栈

技术

用途

Kotlin

Android 应用主要开发语言

Jetpack Compose

构建现代 Android UI

MVVM

应用架构

ViewModel

管理 UI 状态和业务逻辑

Retrofit

网络请求

OkHttp

HTTP 客户端

Gson

JSON 数据转换

Room

本地数据库

Hilt

依赖注入

Coroutines

异步任务

Flow

响应式数据流

AI API

获取 AI 回复

🏗 项目架构

项目整体采用 MVVM + Repository + Room + Retrofit 的结构。

┌──────────────────────────────┐
│          Compose UI          │
│ Chat / History / Setting     │
└──────────────┬───────────────┘
↓
┌──────────────────────────────┐
│          ViewModel           │
│ ChatViewModel                │
│ HistoryViewModel             │
└──────────────┬───────────────┘
↓
┌──────────────────────────────┐
│         Repository           │
│      ChatRepository          │
└──────────────┬───────────────┘
┌────┴─────┐
↓          ↓
┌──────────────┐  ┌──────────────┐
│    Room      │  │   Retrofit   │
│   Database   │  │    AI API    │
└──────────────┘  └──────────────┘

数据流

用户发送消息：

用户输入
↓
ChatScreen
↓
ChatViewModel
↓
ChatRepository
↓
Retrofit
↓
AI API
↓
AI 回复
↓
Room
↓
Flow
↓
Compose UI

📂 项目结构

com.example.aichatapp
│
├── database
│   ├── AppDatabase.kt
│   ├── DatabaseModule.kt
│   ├── MessageDao.kt
│   └── MessageEntity.kt
│
├── di
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── model
│   ├── ChatRequest.kt
│   ├── ChatResponse.kt
│   ├── ChatUiState.kt
│   └── Message.kt
│
├── navigation
│   ├── AppNavigation.kt
│   ├── AppRoute.kt
│   └── BottomNavigationBar.kt
│
├── network
│   ├── AuthInterceptor.kt
│   ├── ChatApi.kt
│   └── NetworkResult.kt
│
├── repository
│   └── ChatRepository.kt
│
├── ui
│   └── theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── view
│   ├── chat
│   │   ├── ChatInput.kt
│   │   ├── ChatScreen.kt
│   │   ├── LoadingMessage.kt
│   │   └── MessageList.kt
│   │
│   ├── components
│   │   └── MessageItem.kt
│   │
│   ├── history
│   │   └── HistoryScreen.kt
│   │
│   └── setting
│       ├── AboutScreen.kt
│       ├── SettingItem.kt
│       └── SettingScreen.kt
│
├── viewmodel
│   ├── ChatViewModel.kt
│   └── HistoryViewModel.kt
│
├── MainActivity.kt
└── MyApplication.kt

🗄 数据库设计

聊天记录使用 Room 保存。

MessageEntity 主要包含：

id       → 数据库自增 ID
text     → 消息内容
isUser   → 是否为用户发送
time     → 消息时间

数据库表：

messages

查询历史记录时：

SELECT * FROM messages ORDER BY id ASC

清空聊天记录时：

DELETE FROM messages

🌐 网络请求

项目使用 Retrofit 与 AI API 通信。

主要流程：

ChatViewModel
↓
ChatRepository
↓
ChatApi
↓
Retrofit
↓
AI API

网络请求结果通过 NetworkResult 进行封装，主要包括：

Success
Error
Loading

同时针对网络连接异常、HTTP 异常以及其他异常进行处理。

💉 依赖注入

项目使用 Hilt 管理依赖。

主要依赖关系：

ChatViewModel
↓
ChatRepository
↓
ChatApi
↓
Retrofit / OkHttp

ChatRepository
↓
MessageDao
↓
Room Database

这样可以减少手动创建对象的代码，并让 ViewModel、Repository、DAO 等组件更容易管理和测试。

🔐 API Key 配置

项目使用：

BuildConfig.SILICON_API_KEY

读取 API Key。

不要把真实 API Key 直接提交到 GitHub。

建议使用本地 local.properties 或其他安全的本地配置方式，并确保敏感信息不会被提交到公开仓库。

例如：

SILICON_API_KEY=你的API_KEY

具体配置方式需要根据项目当前的 Gradle 配置进行调整。

🚀 运行项目

1. 克隆项目

git clone <你的 GitHub 仓库地址>

2. 使用 Android Studio 打开项目

使用较新的 Android Studio 打开项目，并等待 Gradle 同步完成。

3. 配置 API Key

配置项目需要的 AI API Key。

4. 运行

连接 Android 手机或启动 Android Emulator，然后运行项目。

📌 当前项目完成内容

Kotlin Android 项目

Jetpack Compose UI

MVVM 架构

AI API 接入

Retrofit 网络请求

Room 聊天记录存储

聊天历史页面

历史记录空状态

聊天记录先后顺序显示

设置页面

关于页面

清空聊天记录

Hilt 依赖注入

网络错误处理

📚 学习内容

通过这个项目主要学习了：

Jetpack Compose UI 开发

Compose 状态管理

MVVM 架构

ViewModel 与 StateFlow

Kotlin Coroutines

Flow 数据流

Retrofit 网络请求

AI API 调用

Room 数据库

DAO 数据访问

Repository 数据层

Hilt 依赖注入

Compose Navigation

聊天记录管理

Android 项目结构整理

🔮 后续可以继续完善

后续可以继续增加：

真正实现深色模式

持久化保存设置

消息通知功能

更完善的历史记录分组

日期显示

Markdown / 代码高亮

AI 多模型切换

聊天记录搜索

删除单条聊天记录

导出聊天记录

更完善的错误处理

UI 动画优化

📄 License

本项目主要用于 Android 学习和个人项目实践。

如果用于其他用途，请根据实际情况补充 License 信息。