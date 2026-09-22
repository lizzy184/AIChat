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
        当前选中的会话
    ==========================
     */


    private val _selectedConversationId =
        MutableStateFlow<Int?>(null)


    val selectedConversationId =
        _selectedConversationId.asStateFlow()





    /*
    ==========================
        加载状态
    ==========================
     */


    private val _isLoading =
        MutableStateFlow(false)


    val isLoading =
        _isLoading.asStateFlow()





    /*
    ==========================
        错误信息
    ==========================
     */


    private val _errorMessage =
        MutableStateFlow<String?>(null)


    val errorMessage =
        _errorMessage.asStateFlow()







    /*
    ==========================
        初始化
    ==========================
     */


    init {

        loadConversations()

    }








    /*
    ==========================
        获取所有会话

        GET /conversations

    ==========================
     */


    fun loadConversations(){


        viewModelScope.launch {


            _isLoading.value = true

            _errorMessage.value = null



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



            _isLoading.value = false



        }


    }









    /*
    ==========================
        选择会话

        点击列表进入聊天

    ==========================
     */


    fun selectConversation(
        conversationId:Int
    ){


        _selectedConversationId.value =
            conversationId


    }









    /*
    ==========================
        新聊天

        conversationId=null

    ==========================
     */


    fun newConversation(){


        _selectedConversationId.value =
            null


    }









    /*
    ==========================
        删除会话

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



                    if(
                        _selectedConversationId.value
                        ==
                        conversationId
                    ){


                        _selectedConversationId.value =
                            null


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
        清除错误
    ==========================
     */


    fun clearError(){


        _errorMessage.value =
            null


    }





}