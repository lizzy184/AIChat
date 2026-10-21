package com.example.aichatapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.aichatapp.model.Conversation
import com.example.aichatapp.network.NetworkResult
import com.example.aichatapp.repository.ChatRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch

import javax.inject.Inject



@HiltViewModel
class ConversationViewModel @Inject constructor(

    private val chatRepository: ChatRepository

) : ViewModel() {



    /*
    ==========================
        会话列表
    ==========================
     */


    private val _conversations =
        MutableStateFlow<List<Conversation>>(
            emptyList()
        )


    val conversations =
        _conversations.asStateFlow()



    /*
    ==========================
        当前选择会话
    ==========================
     */




    /*
    ==========================
        Loading
    ==========================
     */


    private val _isLoading =
        MutableStateFlow(false)


    val isLoading =
        _isLoading.asStateFlow()



    /*
    ==========================
        错误
    ==========================
     */


    private val _errorMessage =
        MutableStateFlow<String?>(null)


    val errorMessage =
        _errorMessage.asStateFlow()



    init {

        loadConversations()

    }




    /*
    ==========================
        获取会话列表

        GET /conversations

    ==========================
     */


    fun loadConversations(){


        viewModelScope.launch {


            _isLoading.value=true

            _errorMessage.value=null



            when(
                val result =
                    chatRepository.getConversations()
            ){


                is NetworkResult.Success -> {


                    _conversations.value =
                        result.data


                }



                is NetworkResult.Error -> {


                    _errorMessage.value =
                        result.message


                }



                is NetworkResult.Loading -> {

                }

            }



            _isLoading.value=false


        }

    }




    /*
    ==========================
        选择会话
    ==========================
     */





    /*
    ==========================
        删除整个会话

        DELETE /conversations/{id}

    ==========================
     */


    fun deleteConversation(
        conversationId:Int
    ){


        viewModelScope.launch {


            when(
                val result =
                    chatRepository.deleteConversation(
                        conversationId
                    )
            ){


                is NetworkResult.Success -> {


                    _conversations.update { list ->


                        list.filter {

                            it.id != conversationId

                        }


                    }


                }


                is NetworkResult.Error -> {


                    _errorMessage.value =
                        result.message


                }



                is NetworkResult.Loading -> {


                }


            }

        }


    }




    /*
    ==========================
        清除当前会话消息

        DELETE /conversations/{id}/messages

        保留会话

    ==========================
     */


    fun clearConversationMessages(
        conversationId:Int
    ){


        viewModelScope.launch {


            when(
                val result =
                    chatRepository.clearConversationMessages(
                        conversationId
                    )
            ){


                is NetworkResult.Success -> {


                    // 删除成功
                    // 不处理messages


                }



                is NetworkResult.Error -> {


                    _errorMessage.value =
                        result.message


                }



                is NetworkResult.Loading -> {


                }


            }


        }


    }





    /*
    ==========================
        清除错误
    ==========================
     */


    fun clearError(){

        _errorMessage.value=null

    }



}