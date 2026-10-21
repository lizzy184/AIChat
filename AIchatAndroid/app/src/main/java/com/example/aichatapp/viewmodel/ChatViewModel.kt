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



    // =========================================================
    // UI状态
    // =========================================================


    private val _uiState =
        MutableStateFlow(
            ChatUiState()
        )


    val uiState =
        _uiState.asStateFlow()





    // =========================================================
    // 当前会话ID
    // =========================================================


    private val _currentConversationId =
        MutableStateFlow<Int?>(null)


    val currentConversationId =
        _currentConversationId.asStateFlow()





    // =========================================================
    // 上一次发送消息
    // =========================================================


    private var lastMessage:String? = null







    // =========================================================
    // 设置当前conversationId
    // =========================================================


    fun setConversationId(
        id:Int?
    ){

        _currentConversationId.value = id


        Log.d(
            "CHAT",
            "设置conversationId=$id"
        )

    }








    // =========================================================
    // 加载历史消息
    // =========================================================


    fun loadMessages(
        conversationId:Int
    ){


        _currentConversationId.value =
            conversationId



        viewModelScope.launch {



            Log.d(
                "CHAT",
                "加载历史消息:$conversationId"
            )



            when(
                val result =
                    repository.getConversationMessages(
                        conversationId
                    )
            ){



                is NetworkResult.Success -> {



                    val messages =
                        result.data.map {



                            Message(

                                text =
                                    it.content,


                                isUser =
                                    it.role == "user",


                                time =
                                    it.created_time,


                                // 历史消息已经完成
                                isStreaming =
                                    false

                            )


                        }





                    _uiState.value =
                        _uiState.value.copy(

                            messages =
                                messages,


                            errorMessage =
                                null

                        )




                    Log.d(
                        "CHAT",
                        "历史加载完成:${messages.size}"
                    )



                }





                is NetworkResult.Error -> {



                    _uiState.value =
                        _uiState.value.copy(

                            errorMessage =
                                result.message

                        )



                    Log.e(
                        "CHAT",
                        "历史加载失败:${result.message}"
                    )



                }





                is NetworkResult.Loading -> {


                    Log.d(
                        "CHAT",
                        "历史加载中"
                    )


                }


            }


        }


    }









    // =========================================================
    // PDF上传
    // =========================================================


    fun uploadPdf(
        uri:Uri
    ){


        if(
            _uiState.value.isUploading
        ){

            Log.d(
                "PDF",
                "正在上传，忽略"
            )

            return

        }





        viewModelScope.launch {



            _uiState.value =
                _uiState.value.copy(

                    isUploading = true,

                    uploadMessage = null,

                    errorMessage = null

                )





            when(
                val result =
                    repository.uploadPdf(uri)
            ){



                is NetworkResult.Success -> {



                    val data =
                        result.data




                    _uiState.value =
                        _uiState.value.copy(

                            isUploading = false,


                            uploadMessage =
                                "上传成功\n"+
                                        "文件:${data.filename}\n"+
                                        "页数:${data.pages}\n"+
                                        "知识片段:${data.chunks}\n"+
                                        "状态:${data.status}"

                        )



                    Log.d(
                        "PDF",
                        "上传成功"
                    )



                }






                is NetworkResult.Error -> {



                    _uiState.value =
                        _uiState.value.copy(

                            isUploading = false,


                            errorMessage =
                                result.message

                        )



                    Log.e(
                        "PDF",
                        "上传失败:${result.message}"
                    )



                }






                is NetworkResult.Loading -> {



                    Log.d(
                        "PDF",
                        "上传中"
                    )



                }


            }



        }



    }







    // =========================================================
    // 清除上传提示
    // =========================================================


    fun clearUploadMessage(){


        _uiState.value =
            _uiState.value.copy(

                uploadMessage = null

            )


    }
    // =========================================================
    // 发送消息
    // =========================================================


    fun sendMessage(){



        if(
            _uiState.value.isLoading
        ){

            Log.d(
                "CHAT",
                "正在请求，忽略发送"
            )

            return

        }




        val text =
            _uiState.value.inputText.trim()



        if(text.isBlank()){

            return

        }



        lastMessage = text





        val userMessage =
            Message(

                text = text,

                isUser = true,

                time = getCurrentTime(),

                isStreaming = false

            )






        _uiState.value =
            _uiState.value.copy(

                messages =
                    _uiState.value.messages +
                            userMessage,


                inputText = "",


                errorMessage = null

            )





        sendMessageInternet(

            message = text,


            conversationId =
                _currentConversationId.value

        )



    }










    // =========================================================
    // AI流式请求
    // =========================================================


    private fun sendMessageInternet(

        message:String,

        conversationId:Int?

    ){


        viewModelScope.launch {



            _uiState.value =
                _uiState.value.copy(

                    isLoading = true,

                    errorMessage = null

                )






            // 创建AI占位消息

            val aiMessage =
                Message(

                    text = "",

                    isUser = false,

                    time = getCurrentTime(),

                    isStreaming = true

                )




            val aiMessageId =
                aiMessage.id







            _uiState.value =
                _uiState.value.copy(

                    messages =
                        _uiState.value.messages +
                                aiMessage

                )







            /*
                网络chunk缓存

             */

            val chunkChannel =
                Channel<String>(
                    Channel.UNLIMITED
                )






            var networkResult:
                    NetworkResult<Unit>? = null








            /*
                SSE网络线程

             */


            val networkJob =
                launch(
                    Dispatchers.IO
                ){



                    networkResult =
                        repository.streamAiReply(


                            message = message,


                            conversationId =
                                conversationId,



                            onConversation = {

                                    newId ->



                                _currentConversationId.value =
                                    newId



                                Log.d(
                                    "CHAT",
                                    "conversationId=$newId"
                                )


                            },



                            onChunk = {

                                    chunk ->




                                chunkChannel.trySend(
                                    chunk
                                )



                                Log.d(
                                    "STREAM_NETWORK",
                                    "chunk=${chunk.length}"
                                )


                            }


                        )



                    chunkChannel.close()



                }








            /*
                AI完整文本缓存

             */


            val fullText =
                StringBuilder()






            var displayLength = 0





            /*
                控制UI刷新速度

             */


            val charsPerTick = 8

            val tickDelay = 40L







            while(true){





                /*
                    没有缓存时等待网络chunk

                 */


                if(
                    displayLength >=
                    fullText.length
                ){



                    val receive =
                        chunkChannel
                            .receiveCatching()



                    if(
                        receive.isClosed
                    ){

                        break

                    }



                    val chunk =
                        receive.getOrNull()
                            ?: break




                    fullText.append(
                        chunk
                    )


                }







                /*
                    模拟打字机

                 */


                displayLength =
                    minOf(

                        displayLength +
                                charsPerTick,


                        fullText.length

                    )





                val currentText =
                    fullText.substring(

                        0,

                        displayLength

                    )







                /*
                    根据id更新

                    不使用lastIndex

                 */


                updateAiMessage(

                    messageId = aiMessageId,


                    text = currentText,


                    streaming = true

                )






                delay(
                    tickDelay
                )



            }








            networkJob.join()







            /*
                最终完整文本

                开启Markdown解析

             */


            updateAiMessage(

                messageId = aiMessageId,


                text = fullText.toString(),


                streaming = false

            )








            when(
                val result =
                    networkResult
            ){



                is NetworkResult.Success -> {



                    Log.d(
                        "CHAT",
                        "AI回复完成"
                    )



                }





                is NetworkResult.Error -> {



                    _uiState.value =
                        _uiState.value.copy(

                            errorMessage =
                                result.message

                        )


                }





                is NetworkResult.Loading -> {



                    Log.d(
                        "CHAT",
                        "AI仍在加载"
                    )


                }





                null -> {



                    Log.e(
                        "CHAT",
                        "networkResult为空"
                    )


                }


            }








            _uiState.value =
                _uiState.value.copy(

                    isLoading = false

                )





            Log.d(
                "CHAT",
                "AI流程结束"
            )


        }


    }









    // =========================================================
    // 更新AI消息
    // =========================================================


    private fun updateAiMessage(

        messageId:String,


        text:String,


        streaming:Boolean


    ){



        val messages =
            _uiState.value.messages
                .toMutableList()





        val index =
            messages.indexOfFirst {


                it.id == messageId


            }






        if(
            index == -1
        ){

            Log.e(
                "CHAT",
                "找不到AI消息"
            )

            return

        }







        val old =
            messages[index]







        messages[index] =
            old.copy(

                text = text,


                isStreaming = streaming

            )







        _uiState.value =
            _uiState.value.copy(

                messages = messages

            )






        Log.d(
            "STREAM_UI",
            "UI更新:${text.length}"
        )



    }
    // =========================================================
    // 重试上一条消息
    // =========================================================


    fun retryLastMessage(){



        if(
            _uiState.value.isLoading
        ){

            return

        }





        val message =
            lastMessage
                ?: return






        Log.d(
            "CHAT",
            "重新发送:$message"
        )






        sendMessageInternet(

            message = message,


            conversationId =
                _currentConversationId.value

        )



    }









    // =========================================================
    // 输入框变化
    // =========================================================


    fun onTextChange(

        text:String

    ){


        _uiState.value =
            _uiState.value.copy(

                inputText = text

            )


    }









    // =========================================================
    // 清除错误
    // =========================================================


    fun clearError(){


        _uiState.value =
            _uiState.value.copy(

                errorMessage = null

            )


    }









    // =========================================================
    // 新建会话
    // =========================================================


    fun newConversation(){



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
            "创建新会话"
        )



    }









    // =========================================================
    // 当前时间
    // =========================================================


    private fun getCurrentTime():String{


        return SimpleDateFormat(

            "HH:mm",

            Locale.getDefault()

        ).format(

            Date()

        )


    }



}