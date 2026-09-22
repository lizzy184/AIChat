package com.example.aichatapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatapp.model.ChatUiState
import com.example.aichatapp.model.Message
import com.example.aichatapp.network.NetworkResult
import com.example.aichatapp.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {


    // ============================================================
    // UI State
    // ============================================================

    private val _uiState =
        MutableStateFlow(
            ChatUiState()
        )

    val uiState =
        _uiState.asStateFlow()


    // ============================================================
    // 当前会话 ID
    // ============================================================

    private val _currentConversationId =
        MutableStateFlow<Int?>(null)

    val currentConversationId =
        _currentConversationId.asStateFlow()


    // ============================================================
    // 上一次发送的消息
    // ============================================================

    private var lastMessage: String? = null


    // ============================================================
    // 设置当前会话 ID
    // ============================================================

    fun setConversationId(id: Int?) {

        _currentConversationId.value = id

        Log.d(
            "CHAT",
            "设置 conversationId=$id"
        )
    }


    // ============================================================
    // 加载历史消息
    // ============================================================

    fun loadMessages(conversationId: Int) {

        _currentConversationId.value =
            conversationId

        viewModelScope.launch {

            Log.d(
                "CHAT",
                "开始加载历史消息: $conversationId"
            )

            when (
                val result =
                    repository.getConversationMessages(
                        conversationId
                    )
            ) {

                is NetworkResult.Success -> {

                    val messages =
                        result.data.map {

                            Message(
                                text = it.content,
                                isUser =
                                    it.role == "user",
                                time =
                                    it.created_time
                            )
                        }

                    _uiState.value =
                        _uiState.value.copy(
                            messages = messages,
                            errorMessage = null
                        )

                    Log.d(
                        "CHAT",
                        "历史消息加载完成: ${messages.size} 条"
                    )
                }


                is NetworkResult.Error -> {

                    Log.e(
                        "CHAT",
                        "加载历史消息失败: ${result.message}"
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage =
                                result.message
                        )
                }


                is NetworkResult.Loading -> {

                    Log.d(
                        "CHAT",
                        "历史消息加载中"
                    )
                }
            }
        }
    }


    // ============================================================
    // 上传 PDF
    // ============================================================

    fun uploadPdf(uri: Uri) {

        // 防止重复上传
        if (_uiState.value.isUploading) {

            Log.d(
                "PDF_UPLOAD",
                "当前正在上传，忽略本次操作"
            )

            return
        }


        viewModelScope.launch {

            Log.d(
                "PDF_UPLOAD",
                "开始上传 PDF: $uri"
            )


            // ----------------------------------------------------
            // 开始上传
            // ----------------------------------------------------

            _uiState.value =
                _uiState.value.copy(
                    isUploading = true,
                    uploadMessage = null,
                    errorMessage = null
                )


            // ----------------------------------------------------
            // 调用 Repository
            // ----------------------------------------------------

            when (
                val result =
                    repository.uploadPdf(uri)
            ) {

                // ------------------------------------------------
                // 上传成功
                // ------------------------------------------------

                is NetworkResult.Success -> {

                    val data =
                        result.data


                    Log.d(
                        "PDF_UPLOAD",
                        "PDF上传成功"
                    )

                    Log.d(
                        "PDF_UPLOAD",
                        "filename=${data.filename}"
                    )

                    Log.d(
                        "PDF_UPLOAD",
                        "pages=${data.pages}"
                    )

                    Log.d(
                        "PDF_UPLOAD",
                        "chunks=${data.chunks}"
                    )

                    Log.d(
                        "PDF_UPLOAD",
                        "status=${data.status}"
                    )


                    _uiState.value =
                        _uiState.value.copy(
                            isUploading = false,

                            uploadMessage =
                                "上传成功\n" +
                                        "文件：${data.filename}\n" +
                                        "页数：${data.pages}\n" +
                                        "知识片段：${data.chunks}\n" +
                                        "状态：${data.status}"
                        )
                }


                // ------------------------------------------------
                // 上传失败
                // ------------------------------------------------

                is NetworkResult.Error -> {

                    Log.e(
                        "PDF_UPLOAD",
                        "PDF上传失败: ${result.message}"
                    )


                    _uiState.value =
                        _uiState.value.copy(
                            isUploading = false,
                            errorMessage =
                                result.message
                        )
                }


                // ------------------------------------------------
                // Loading
                // ------------------------------------------------

                is NetworkResult.Loading -> {

                    Log.d(
                        "PDF_UPLOAD",
                        "PDF上传中"
                    )
                }
            }
        }
    }


    // ============================================================
    // 清除上传成功提示
    // ============================================================

    fun clearUploadMessage() {

        _uiState.value =
            _uiState.value.copy(
                uploadMessage = null
            )
    }


    // ============================================================
    // 发送消息
    // ============================================================

    fun sendMessage() {

        if (_uiState.value.isLoading) {

            Log.d(
                "CHAT",
                "当前正在请求，忽略本次发送"
            )

            return
        }


        val text =
            _uiState.value.inputText.trim()


        if (text.isBlank()) {
            return
        }


        lastMessage = text


        val userMessage =
            Message(
                text = text,
                isUser = true,
                time = getCurrentTime()
            )


        _uiState.value =
            _uiState.value.copy(

                messages =
                    _uiState.value.messages +
                            userMessage,

                inputText = "",

                errorMessage = null
            )


        Log.d(
            "CHAT",
            "发送消息: $text"
        )


        sendMessageInternet(
            message = text,
            conversationId =
                _currentConversationId.value
        )
    }


    // ============================================================
    // AI 流式请求
    // ============================================================

    private fun sendMessageInternet(
        message: String,
        conversationId: Int?
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )


            val aiMessage =
                Message(
                    text = "",
                    isUser = false,
                    time = getCurrentTime()
                )


            _uiState.value =
                _uiState.value.copy(
                    messages =
                        _uiState.value.messages +
                                aiMessage
                )


            val chunkChannel =
                Channel<String>(
                    Channel.UNLIMITED
                )


            var networkResult:
                    NetworkResult<Unit>? = null


            val networkJob =
                launch(Dispatchers.IO) {

                    Log.d(
                        "CHAT",
                        "开始 SSE 请求"
                    )


                    networkResult =
                        repository.streamAiReply(

                            message = message,

                            conversationId =
                                conversationId,

                            onConversation = { newId ->

                                _currentConversationId
                                    .value = newId

                                Log.d(
                                    "CHAT",
                                    "conversationId=$newId"
                                )
                            },

                            onChunk = { chunk ->

                                chunkChannel.trySend(
                                    chunk
                                )

                                Log.d(
                                    "STREAM_NETWORK",
                                    "收到 chunk: ${chunk.length} 字符"
                                )
                            }
                        )


                    Log.d(
                        "CHAT",
                        "SSE 网络读取结束"
                    )


                    chunkChannel.close()
                }


            val pendingText =
                StringBuilder()


            var displayedLength = 0


            val charsPerTick = 4

            val tickDelay = 30L


            while (true) {

                if (
                    displayedLength >=
                    pendingText.length
                ) {

                    val receiveResult =
                        chunkChannel.receiveCatching()


                    if (receiveResult.isClosed) {
                        break
                    }


                    val chunk =
                        receiveResult.getOrNull()


                    if (chunk == null) {
                        break
                    }


                    pendingText.append(
                        chunk
                    )


                    Log.d(
                        "STREAM_BUFFER",
                        "当前缓存: ${pendingText.length} 字符"
                    )
                }


                val targetLength =
                    minOf(
                        displayedLength +
                                charsPerTick,

                        pendingText.length
                    )


                displayedLength =
                    targetLength


                val currentText =
                    pendingText.substring(
                        0,
                        displayedLength
                    )


                val messages =
                    _uiState.value.messages
                        .toMutableList()


                if (messages.isNotEmpty()) {

                    messages[messages.lastIndex] =
                        aiMessage.copy(
                            text = currentText
                        )


                    _uiState.value =
                        _uiState.value.copy(
                            messages = messages
                        )


                    Log.d(
                        "STREAM_UI",
                        "UI显示: " +
                                "$displayedLength 字符 | " +
                                currentText.takeLast(30)
                    )
                }


                delay(tickDelay)
            }


            networkJob.join()


            if (
                displayedLength <
                pendingText.length
            ) {

                val finalText =
                    pendingText.toString()


                val messages =
                    _uiState.value.messages
                        .toMutableList()


                if (messages.isNotEmpty()) {

                    messages[messages.lastIndex] =
                        aiMessage.copy(
                            text = finalText
                        )


                    _uiState.value =
                        _uiState.value.copy(
                            messages = messages
                        )
                }


                Log.d(
                    "STREAM_UI",
                    "最终刷新: ${finalText.length} 字符"
                )
            }


            when (val result = networkResult) {

                is NetworkResult.Success -> {

                    Log.d(
                        "CHAT",
                        "AI 回复完成"
                    )

                    Log.d(
                        "CHAT",
                        "最终文本长度=${pendingText.length}"
                    )
                }


                is NetworkResult.Error -> {

                    Log.e(
                        "CHAT",
                        "AI 请求失败: ${result.message}"
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage =
                                result.message
                        )
                }


                is NetworkResult.Loading -> {

                    Log.d(
                        "CHAT",
                        "AI 请求仍在加载"
                    )
                }


                null -> {

                    Log.e(
                        "CHAT",
                        "networkResult=null"
                    )
                }
            }


            _uiState.value =
                _uiState.value.copy(
                    isLoading = false
                )


            Log.d(
                "CHAT",
                "整个 AI 请求流程完成"
            )
        }
    }


    // ============================================================
    // 重试上一条消息
    // ============================================================

    fun retryLastMessage() {

        if (_uiState.value.isLoading) {
            return
        }


        val message =
            lastMessage
                ?: return


        Log.d(
            "CHAT",
            "重新发送消息: $message"
        )


        sendMessageInternet(
            message = message,
            conversationId =
                _currentConversationId.value
        )
    }


    // ============================================================
    // 输入框文字变化
    // ============================================================

    fun onTextChange(
        text: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                inputText = text
            )
    }


    // ============================================================
    // 清除错误
    // ============================================================

    fun clearError() {

        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }


    // ============================================================
    // 新建会话
    // ============================================================

    fun newConversation() {

        _currentConversationId.value =
            null

        lastMessage = null

        _uiState.value =
            _uiState.value.copy(
                messages = emptyList(),
                inputText = "",
                errorMessage = null,
                isLoading = false,
                isUploading = false,
                uploadMessage = null
            )


        Log.d(
            "CHAT",
            "新建会话"
        )
    }


    // ============================================================
    // 清空当前消息
    // ============================================================

    fun clearMessages() {

        _uiState.value =
            _uiState.value.copy(
                messages = emptyList(),
                errorMessage = null
            )


        Log.d(
            "CHAT",
            "消息已清空"
        )
    }


    // ============================================================
    // 删除当前会话
    // ============================================================

    fun deleteCurrentConversation() {

        val conversationId =
            _currentConversationId.value


        if (conversationId == null) {

            Log.d(
                "CHAT",
                "当前没有会话"
            )

            return
        }


        viewModelScope.launch {

            Log.d(
                "CHAT",
                "开始删除会话: $conversationId"
            )


            when (
                val result =
                    repository.deleteConversation(
                        conversationId
                    )
            ) {

                is NetworkResult.Success -> {

                    _currentConversationId.value =
                        null

                    lastMessage = null


                    _uiState.value =
                        _uiState.value.copy(
                            messages = emptyList(),
                            inputText = "",
                            errorMessage = null,
                            isLoading = false
                        )


                    Log.d(
                        "CHAT",
                        "会话删除成功"
                    )
                }


                is NetworkResult.Error -> {

                    Log.e(
                        "CHAT",
                        "删除会话失败: ${result.message}"
                    )


                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage =
                                result.message
                        )
                }


                is NetworkResult.Loading -> {

                    Log.d(
                        "CHAT",
                        "删除会话中"
                    )
                }
            }
        }
    }


    // ============================================================
    // 获取当前时间
    // ============================================================

    private fun getCurrentTime(): String {

        return SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(
            Date()
        )
    }
}