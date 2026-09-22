package com.example.aichatapp.repository

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
     * 普通AI回复
     */
    suspend fun getAiReply(
        message: String,
        conversation_id: Int
    ): NetworkResult<Message> {

        return try {

            val response =
                api.chat(
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

            NetworkResult.Error(
                "网络连接失败"
            )

        } catch (e: HttpException) {

            NetworkResult.Error(
                "服务器错误:${e.code()}"
            )

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "未知错误"
            )
        }
    }


    /**
     * SSE 流式AI回复
     */
    suspend fun streamAiReply(

        message: String,

        conversationId: Int?,

        onConversation: (Int) -> Unit,

        onChunk: (String) -> Unit

    ): NetworkResult<Unit> {

        return try {

            withContext(
                Dispatchers.IO
            ) {

                val json =
                    JSONObject()
                        .put(
                            "message",
                            message
                        )
                        .put(
                            "conversation_id",
                            conversationId
                        )
                        .toString()


                val request =
                    Request.Builder()
                        .url(
                            BuildConfig.BASE_URL + "chat"
                        )
                        .post(
                            json.toRequestBody(
                                "application/json"
                                    .toMediaType()
                            )
                        )
                        .build()


                okHttpClient
                    .newCall(request)
                    .execute()
                    .use { response ->

                        if (!response.isSuccessful) {

                            return@use NetworkResult.Error(
                                "请求失败:${response.code}"
                            )
                        }


                        val source =
                            response.body
                                ?.source()
                                ?: return@use NetworkResult.Error(
                                    "服务器响应为空"
                                )


                        var event = ""

                        val dataBuilder =
                            StringBuilder()


                        while (true) {

                            val line =
                                source.readUtf8Line()
                                    ?: break


                            Log.d(
                                "SSE",
                                line
                            )


                            when {

                                line.startsWith("event:") -> {

                                    event =
                                        line
                                            .removePrefix(
                                                "event:"
                                            )
                                            .trim()
                                }


                                line.startsWith("data:") -> {

                                    dataBuilder.append(
                                        line
                                            .removePrefix(
                                                "data:"
                                            )
                                            .trim()
                                    )

                                    dataBuilder.append("\n")
                                }


                                line.isEmpty() -> {

                                    val data =
                                        dataBuilder
                                            .toString()
                                            .trim()


                                    when (event) {

                                        "message" -> {

                                            if (data.isNotEmpty()) {

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

                                            return@use NetworkResult.Success(
                                                Unit
                                            )
                                        }


                                        "conversation" -> {

                                            val json =
                                                JSONObject(data)

                                            val id =
                                                json.getInt(
                                                    "conversation_id"
                                                )

                                            onConversation(id)

                                        }
                                    }


                                    event = ""

                                    dataBuilder.clear()
                                }
                            }
                        }


                        NetworkResult.Success(Unit)
                    }
            }

        } catch (e: Exception) {

            Log.e(
                "SSE",
                "stream error",
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
    suspend fun getHistory():
            NetworkResult<List<Message>> {

        return try {

            val response =
                api.getHistory()

            val list =
                response.map {

                    Message(
                        text = it.content,
                        isUser =
                            it.role == "user",
                        time =
                            it.created_time
                    )
                }

            NetworkResult.Success(list)

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "获取历史失败"
            )
        }
    }


    /**
     * 清空历史
     */
    suspend fun clearHistory():
            NetworkResult<Unit> {

        return try {

            val response =
                api.clearHistory()

            if (response.isSuccessful) {

                NetworkResult.Success(Unit)

            } else {

                NetworkResult.Error(
                    "删除失败:${response.code()}"
                )
            }

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "删除失败"
            )
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

            val response =
                api.register(
                    RegisterRequest(
                        username,
                        password
                    )
                )

            NetworkResult.Success(response)

        } catch (e: HttpException) {

            when (e.code()) {

                409 ->
                    NetworkResult.Error(
                        "用户名已存在",
                        409
                    )

                else ->
                    NetworkResult.Error(
                        "注册失败",
                        e.code()
                    )
            }

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "注册失败"
            )
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

            val response =
                api.login(
                    LoginRequest(
                        username,
                        password
                    )
                )

            NetworkResult.Success(response)

        } catch (e: HttpException) {

            when (e.code()) {

                401 ->
                    NetworkResult.Error(
                        "用户名或密码错误",
                        401
                    )

                else ->
                    NetworkResult.Error(
                        "登录失败",
                        e.code()
                    )
            }

        } catch (e: UnknownHostException) {

            NetworkResult.Error(
                "无法连接服务器"
            )

        } catch (e: IOException) {

            NetworkResult.Error(
                "网络连接失败"
            )

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "登录失败"
            )
        }
    }


    /**
     * 获取会话列表
     */
    suspend fun getConversations():
            NetworkResult<List<Conversation>> {

        return try {

            val response =
                api.getConversations()

            NetworkResult.Success(response)

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "获取会话失败"
            )
        }
    }


    /**
     * 获取指定会话消息
     */
    suspend fun getConversationMessages(
        conversationId: Int
    ):
            NetworkResult<List<HistoryMessage>> {

        return try {

            val response =
                api.getConversationMessages(
                    conversationId
                )

            NetworkResult.Success(response)

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "获取消息失败"
            )
        }
    }


    /**
     * 删除会话
     */
    suspend fun deleteConversation(
        conversationId: Int
    ):
            NetworkResult<Unit> {

        return try {

            val response =
                api.deleteConversation(
                    conversationId
                )

            if (response.isSuccessful) {

                NetworkResult.Success(Unit)

            } else {

                NetworkResult.Error(
                    "删除失败:${response.code()}"
                )
            }

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "删除失败"
            )
        }
    }


    /**
     * 直接上传 Multipart 文件
     *
     * 这个方法保留。
     */
    suspend fun uploadDocument(
        requestBody: MultipartBody.Part
    ): NetworkResult<DocumentUploadResponse> {

        return try {

            val response =
                api.uploadDocument(
                    requestBody
                )

            NetworkResult.Success(
                response
            )

        } catch (e: IOException) {

            NetworkResult.Error(
                "文件上传失败，请检查网络连接"
            )

        } catch (e: HttpException) {

            NetworkResult.Error(
                "服务器错误：${e.code()}"
            )

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "文件上传失败"
            )
        }
    }


    /**
     * 上传 PDF
     *
     * Uri
     * ↓
     * ContentResolver
     * ↓
     * ByteArray
     * ↓
     * MultipartBody.Part
     * ↓
     * /documents/upload
     */
    suspend fun uploadPdf(
        uri: Uri
    ): NetworkResult<DocumentUploadResponse> {

        return try {

            val resolver =
                context.contentResolver


            // ------------------------------------------------
            // 1. 获取文件名
            // ------------------------------------------------

            val fileName =
                resolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )?.use { cursor ->

                    val nameIndex =
                        cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                        )

                    if (
                        cursor.moveToFirst() &&
                        nameIndex >= 0
                    ) {

                        cursor.getString(
                            nameIndex
                        )

                    } else {

                        "document.pdf"
                    }

                } ?: "document.pdf"


            // ------------------------------------------------
            // 2. 打开 PDF
            // ------------------------------------------------

            val inputStream =
                resolver.openInputStream(uri)
                    ?: return NetworkResult.Error(
                        "无法读取PDF文件"
                    )


            // ------------------------------------------------
            // 3. 读取文件
            // ------------------------------------------------

            val bytes =
                inputStream.use {
                    it.readBytes()
                }


            // ------------------------------------------------
            // 4. 创建 RequestBody
            // ------------------------------------------------

            val requestBody =
                bytes.toRequestBody(
                    "application/pdf".toMediaType()
                )


            // ------------------------------------------------
            // 5. 创建 Multipart
            // ------------------------------------------------

            val multipart =
                MultipartBody.Part.createFormData(
                    "file",
                    fileName,
                    requestBody
                )


            // ------------------------------------------------
            // 6. 上传
            // ------------------------------------------------

            uploadDocument(
                multipart
            )

        } catch (e: IOException) {

            NetworkResult.Error(
                "PDF文件读取失败"
            )

        } catch (e: Exception) {

            NetworkResult.Error(
                e.message ?: "PDF上传失败"
            )
        }
    }
}