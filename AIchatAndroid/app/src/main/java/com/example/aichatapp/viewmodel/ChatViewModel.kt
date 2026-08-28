package com.example.aichatapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.aichatapp.model.ChatUiState
import com.example.aichatapp.model.Message
import com.example.aichatapp.network.NetworkResult
import com.example.aichatapp.repository.ChatRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import javax.inject.Inject

import android.util.Log
import com.example.aichatapp.data.UserPreferences


@HiltViewModel
class ChatViewModel @Inject constructor(

    private val repository: ChatRepository,
    private val userPreferences: UserPreferences
): ViewModel(){



    /**
     *
     * UI状态
     *
     */

    private val _uiState =
        MutableStateFlow(
            ChatUiState()
        )


    val uiState =
        _uiState.asStateFlow()



    /**
     *
     * 保存最后一次发送消息
     *
     * 用于重新发送
     *
     */

    private var lastMessage:String? = null





    /**
     *
     * 页面创建
     *
     * 自动加载服务器历史聊天
     *
     */

    init {

        loadHistory()

    }





    /**
     *
     * 获取历史记录
     *
     * Android
     *
     * GET /history
     *
     * ↓
     *
     * FastAPI
     *
     * ↓
     *
     * SQLite
     *
     */

    private fun loadHistory(){


        viewModelScope.launch {
            userPreferences.userId.collect{


                    userId->


                if(userId!=null) {

                    val result =
                        repository.getHistory(userId = userId)



                    when (result) {


                        is NetworkResult.Success<List<Message>> -> {


                            _uiState.value =
                                _uiState.value.copy(

                                    messages =
                                        result.data

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


                        }


                    }
                }
                }
        }


    }









    /**
     *
     * 点击发送按钮
     *
     */

    fun sendMessage(){



        Log.d(
            "CHAT_TEST",
            "sendMessage执行了"
        )




        if(
            _uiState.value.isLoading
        ){

            return

        }




        val userInput =
            _uiState.value.inputText




        if(
            userInput.isBlank()
        ){

            return

        }




        lastMessage =
            userInput






        /**
         *
         * 立即显示用户消息
         *
         */

        val userMessage =
            Message(

                text = userInput,

                isUser = true,

                time = getCurrentTime()

            )







        _uiState.value =
            _uiState.value.copy(

                messages =
                    _uiState.value.messages
                            +
                            userMessage,


                inputText = ""

            )






        sendMessageInternet(

            userInput

        )


    }









    /**
     *
     * 调用后端AI接口
     *
     */

    private fun sendMessageInternet(

        message:String

    ){


        viewModelScope.launch {



            _uiState.value =
                _uiState.value.copy(

                    isLoading = true

                )






            val result =
                repository.getAiReply(

                    message

                )







            when(result){



                /**
                 *
                 * AI返回成功
                 *
                 */

                is NetworkResult.Success<Message> -> {



                    val aiMessage =
                        result.data





                    _uiState.value =
                        _uiState.value.copy(

                            messages =
                                _uiState.value.messages
                                        +
                                        aiMessage

                        )


                }





                /**
                 *
                 * 网络或者服务器错误
                 *
                 */

                is NetworkResult.Error -> {



                    _uiState.value =
                        _uiState.value.copy(

                            errorMessage =
                                result.message

                        )


                }





                /**
                 *
                 * 加载状态
                 *
                 */

                is NetworkResult.Loading -> {



                }



            }






            _uiState.value =
                _uiState.value.copy(

                    isLoading = false

                )



        }



    }









    /**
     *
     * 重新发送
     *
     */

    fun retryLastMessage(){



        if(
            _uiState.value.isLoading
        ){

            return

        }




        val message =
            lastMessage
                ?: return





        sendMessageInternet(

            message

        )



    }









    /**
     *
     * 清空聊天记录
     *
     * 调用后端 DELETE /history
     *
     */

    fun clearMessages(){

        Log.d(
            "CLEAR_TEST",
            "点击清空聊天记录"
        )
        viewModelScope.launch {



            when(
                repository.clearHistory()
            ){



                is NetworkResult.Success<Unit> -> {



                    _uiState.value =
                        _uiState.value.copy(

                            messages =
                                emptyList()

                        )


                }





                is NetworkResult.Error -> {



                    _uiState.value =
                        _uiState.value.copy(

                            errorMessage =
                                "清空失败"

                        )


                }





                is NetworkResult.Loading -> {



                }



            }



        }



    }









    /**
     *
     * 输入框改变
     *
     */

    fun onTextChange(

        newText:String

    ){


        _uiState.value =
            _uiState.value.copy(

                inputText =
                    newText

            )


    }









    /**
     *
     * 清除错误
     *
     */

    fun clearError(){


        _uiState.value =
            _uiState.value.copy(

                errorMessage = null

            )


    }









    /**
     *
     * 获取当前时间
     *
     */

    private fun getCurrentTime():String{


        return SimpleDateFormat(

            "HH:mm",

            Locale.getDefault()

        ).format(

            Date()

        )


    }



}