
package com.example.aichatapp.repository

import android.os.SystemClock
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.aichatapp.BuildConfig
import com.example.aichatapp.data.UserPreferences
import com.example.aichatapp.model.*
import com.example.aichatapp.network.ChatApi
import com.example.aichatapp.network.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val api: ChatApi,
    private val userPreferences: UserPreferences,
    private val okHttpClient: OkHttpClient,
    @ApplicationContext
    private val context: Context
) {

    /**
     * 普通 AI 回复
     */
    suspend fun getAiReply(
        message: String,
        conversation_id: Int
    ): NetworkResult<Message> {
        return try {
            val response = api.chat(
                ChatRequest(
                    conversation_id,
                    message
                )
            )

            NetworkResult.Success(
                Message(
                    text = response.reply,
                    isUser = false,
                    time = ""
                )
            )
        } catch (e: IOException) {
            NetworkResult.Error("网络连接失败")
        } catch (e: HttpException) {
            NetworkResult.Error("服务器错误:${e.code()}")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "未知错误")
        }
    }

    /**
     * SSE 流式 AI 回复
     */
    suspend fun streamAiReply(
        message: String,
        conversationId: Int?,
        onConversation: (Int) -> Unit,
        onChunk: (String) -> Unit
    ): NetworkResult<Unit> {

        val requestStartMs = SystemClock.elapsedRealtime()
        var eventCount = 0
        var chunkCount = 0
        var totalCharacters = 0

        return try {
            withContext(Dispatchers.IO) {

                val json = JSONObject()
                    .put("message", message)
                    .put("conversation_id", conversationId)
                    .toString()

                // 诊断修改：请求服务端返回未压缩的 SSE 数据，
                // 排除 HTTP 内容压缩可能造成的缓冲影响。
                val request = Request.Builder()
                    .url(BuildConfig.BASE_URL + "chat")
                    .header("Accept", "text/event-stream")
                    .header("Accept-Encoding", "identity")
                    .header("Cache-Control", "no-cache")
                    .post(
                        json.toRequestBody(
                            "application/json".toMediaType()
                        )
                    )
                    .build()

                Log.d(
                    "SSE_TIMING",
                    "Request started at=$requestStartMs"
                )

                okHttpClient.newCall(request).execute().use { response ->

                    Log.d(
                        "SSE_TIMING",
                        "HTTP response received: " +
                                "code=${response.code}, " +
                                "elapsed=${SystemClock.elapsedRealtime() - requestStartMs}ms, " +
                                "contentType=${response.header("Content-Type")}, " +
                                "contentEncoding=${response.header("Content-Encoding")}"
                    )

                    if (!response.isSuccessful) {
                        return@use NetworkResult.Error(
                            "请求失败:${response.code}"
                        )
                    }

                    val source = response.body?.source()
                        ?: return@use NetworkResult.Error(
                            "服务器响应为空"
                        )

                    var event = ""
                    val dataBuilder = StringBuilder()

                    while (true) {
                        val line = source.readUtf8Line() ?: break
                        Log.d(
                            "SSE_RAW_TIMING",
                            "t=+${SystemClock.elapsedRealtime() - requestStartMs}ms, line=$line"
                        )

                        val lineElapsed =
                            SystemClock.elapsedRealtime() - requestStartMs

                        Log.d(
                            "SSE_LINE",
                            "t=+${lineElapsed}ms, line=$line"
                        )

                        when {
                            // SSE 注释行，忽略
                            line.startsWith(":") -> {
                                continue
                            }

                            // 事件名称
                            line.startsWith("event:") -> {
                                event = line
                                    .removePrefix("event:")
                                    .trim()
                            }

                            // 数据行
                            line.startsWith("data:") -> {
                                if (dataBuilder.isNotEmpty()) {
                                    dataBuilder.append("\n")
                                }

                                dataBuilder.append(
                                    line.removePrefix("data:").trimStart()
                                )
                            }

                            // 空行表示当前 SSE 事件结束
                            line.isEmpty() -> {
                                val data = dataBuilder.toString()

                                if (event.isNotEmpty() || data.isNotEmpty()) {
                                    eventCount++

                                    val eventElapsed =
                                        SystemClock.elapsedRealtime() -
                                                requestStartMs

                                    Log.d(
                                        "SSE_TIMING",
                                        "Event #$eventCount: " +
                                                "type=$event, " +
                                                "t=+${eventElapsed}ms, " +
                                                "dataLength=${data.length}"
                                    )

                                    when (event) {
                                        "conversation" -> {
                                            try {
                                                val jsonData = JSONObject(data)
                                                val id = jsonData.getInt(
                                                    "conversation_id"
                                                )

                                                Log.d(
                                                    "SSE_TIMING",
                                                    "Conversation received: id=$id"
                                                )

                                                onConversation(id)
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "SSE",
                                                    "Failed to parse conversation event: $data",
                                                    e
                                                )

                                                return@use NetworkResult.Error(
                                                    "会话事件解析失败"
                                                )
                                            }
                                        }

                                        "message" -> {
                                            if (data.isNotEmpty()) {
                                                chunkCount++
                                                totalCharacters += data.length

                                                val callbackElapsed =
                                                    SystemClock.elapsedRealtime() -
                                                            requestStartMs

                                                Log.d(
                                                    "SSE_TIMING",
                                                    "Calling onChunk: " +
                                                            "chunk=$chunkCount, " +
                                                            "t=+${callbackElapsed}ms, " +
                                                            "chunkLength=${data.length}, " +
                                                            "totalLength=$totalCharacters"
                                                )

                                                onChunk(data)
                                            }
                                        }

                                        "error" -> {
                                            return@use NetworkResult.Error(
                                                data.ifEmpty {
                                                    "AI服务异常"
                                                }
                                            )
                                        }

                                        "done" -> {
                                            Log.d(
                                                "SSE_TIMING",
                                                "Done event received: " +
                                                        "chunks=$chunkCount, " +
                                                        "totalCharacters=$totalCharacters, " +
                                                        "elapsed=${SystemClock.elapsedRealtime() - requestStartMs}ms"
                                            )

                                            return@use NetworkResult.Success(Unit)
                                        }

                                        else -> {
                                            Log.d(
                                                "SSE_TIMING",
                                                "Unknown or empty event: $event"
                                            )
                                        }
                                    }
                                }

                                // 准备读取下一个 SSE 事件
                                event = ""
                                dataBuilder.clear()
                            }
                        }
                    }

                    Log.w(
                        "SSE_TIMING",
                        "Stream ended without done event: " +
                                "chunks=$chunkCount, " +
                                "totalCharacters=$totalCharacters, " +
                                "elapsed=${SystemClock.elapsedRealtime() - requestStartMs}ms"
                    )

                    NetworkResult.Success(Unit)
                }
            }
        } catch (e: Exception) {
            Log.e(
                "SSE",
                "Stream error after " +
                        "${SystemClock.elapsedRealtime() - requestStartMs}ms",
                e
            )

            NetworkResult.Error(
                e.message ?: "网络异常"
            )
        }
    }

    /**
     * 获取历史记录
     */
    suspend fun getHistory(): NetworkResult<List<Message>> {
        return try {
            val response = api.getHistory()

            val list = response.map {
                Message(
                    text = it.content,
                    isUser = it.role == "user",
                    time = it.created_time
                )
            }

            NetworkResult.Success(list)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "获取历史失败")
        }
    }

    /**
     * 清空历史记录
     */
    suspend fun clearHistory(): NetworkResult<Unit> {
        return try {
            val response = api.clearHistory()

            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("删除失败:${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "删除失败")
        }
    }

    /**
     * 注册
     */
    suspend fun register(
        username: String,
        password: String
    ): NetworkResult<RegisterResponse> {
        return try {
            val response = api.register(
                RegisterRequest(username, password)
            )

            NetworkResult.Success(response)
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> NetworkResult.Error("用户名已存在", 409)
                else -> NetworkResult.Error("注册失败", e.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "注册失败")
        }
    }

    /**
     * 登录
     */
    suspend fun login(
        username: String,
        password: String
    ): NetworkResult<LoginResponse> {
        return try {
            val response = api.login(
                LoginRequest(username, password)
            )

            NetworkResult.Success(response)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> NetworkResult.Error("用户名或密码错误", 401)
                else -> NetworkResult.Error("登录失败", e.code())
            }
        } catch (e: UnknownHostException) {
            NetworkResult.Error("无法连接服务器")
        } catch (e: IOException) {
            NetworkResult.Error("网络连接失败")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "登录失败")
        }
    }

    /**
     * 获取会话列表
     */
    suspend fun getConversations(): NetworkResult<List<Conversation>> {
        return try {
            val response = api.getConversations()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "获取会话失败")
        }
    }

    /**
     * 获取指定会话消息
     */
    suspend fun getConversationMessages(
        conversationId: Int
    ): NetworkResult<List<HistoryMessage>> {
        return try {
            val response = api.getConversationMessages(conversationId)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "获取消息失败")
        }
    }

    /**
     * 删除会话
     */
    suspend fun deleteConversation(
        conversationId: Int
    ): NetworkResult<Unit> {
        return try {
            val response = api.deleteConversation(conversationId)

            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("删除失败:${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "删除失败")
        }
    }

    /**
     * 退出登录
     */
    suspend fun logout(): NetworkResult<Unit> {
        return try {
            val refreshToken = userPreferences.refreshToken.first()

            if (refreshToken.isNullOrEmpty()) {
                return NetworkResult.Error("Refresh Token 不存在")
            }

            val response = api.logout(
                LogoutRequest(refresh_token = refreshToken)
            )

            if (response.isSuccessful) {
                userPreferences.clearTokens()
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    "退出登录失败:${response.code()}",
                    response.code()
                )
            }
        } catch (e: IOException) {
            NetworkResult.Error("网络连接失败")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "退出登录失败")
        }
    }

    /**
     * 上传 Multipart 文件
     */
    suspend fun uploadDocument(
        requestBody: MultipartBody.Part
    ): NetworkResult<DocumentUploadResponse> {
        return try {
            val response = api.uploadDocument(requestBody)
            NetworkResult.Success(response)
        } catch (e: IOException) {
            NetworkResult.Error("文件上传失败，请检查网络连接")
        } catch (e: HttpException) {
            NetworkResult.Error("服务器错误：${e.code()}")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "文件上传失败")
        }
    }

    /**
     * 上传 PDF
     */
    suspend fun uploadPdf(
        uri: Uri
    ): NetworkResult<DocumentUploadResponse> {
        return try {
            val resolver = context.contentResolver

            val fileName = resolver.query(
                uri,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

                if (cursor.moveToFirst() && nameIndex >= 0) {
                    cursor.getString(nameIndex)
                } else {
                    "document.pdf"
                }
            } ?: "document.pdf"

            val inputStream = resolver.openInputStream(uri)
                ?: return NetworkResult.Error("无法读取PDF文件")

            val bytes = inputStream.use {
                it.readBytes()
            }

            val requestBody = bytes.toRequestBody(
                "application/pdf".toMediaType()
            )

            val multipart = MultipartBody.Part.createFormData(
                "file",
                fileName,
                requestBody
            )

            uploadDocument(multipart)
        } catch (e: IOException) {
            NetworkResult.Error("PDF文件读取失败")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "PDF上传失败")
        }
    }

    /**
     * 清空指定会话的消息
     */
    suspend fun clearConversationMessages(
        conversationId: Int
    ): NetworkResult<Unit> {
        return try {
            val response = api.deleteConversationMessages(conversationId)

            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    message = "删除聊天记录失败",
                    code = response.code()
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.message ?: "网络异常"
            )
        }
    }
}
