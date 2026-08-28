package com.example.aichatapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatapp.data.UserPreferences

import com.example.aichatapp.model.Message
import com.example.aichatapp.network.NetworkResult
import com.example.aichatapp.repository.ChatRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject



@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: ChatRepository

):ViewModel(){



    private val _messages =
        MutableStateFlow<List<Message>>(emptyList())


    val messages =
        _messages.asStateFlow()



    init {

        loadHistory()

    }



    private fun loadHistory(){


        viewModelScope.launch {
            userPreferences.userId.collect {


                    userId ->


                if (userId != null) {


                    val result =
                        repository.getHistory(userId = userId)



                    when (result) {


                        is NetworkResult.Success -> {


                            _messages.value =
                                result.data


                        }


                        is NetworkResult.Error -> {


                            //这里可以增加错误状态


                        }


                        is NetworkResult.Loading -> {


                        }


                    }


                }


            }
        }
        }
}