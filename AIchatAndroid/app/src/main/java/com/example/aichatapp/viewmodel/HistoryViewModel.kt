package com.example.aichatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages =
        MutableStateFlow<List<Message>>(emptyList())

    val messages = _messages.asStateFlow()

    fun loadMessages(conversationId: Int?) {

        // 没有会话ID，历史页面直接为空
        if (conversationId == null) {
            _messages.value = emptyList()
            return
        }

        viewModelScope.launch {

            when (
                val result =
                    repository.getConversationMessages(conversationId)
            ) {

                is NetworkResult.Success -> {

                    _messages.value =
                        result.data.map {
                            Message(
                                text = it.content,
                                isUser = it.role == "user",
                                time = it.created_time
                            )
                        }
                }

                is NetworkResult.Error -> {
                    _messages.value = emptyList()
                }

                is NetworkResult.Loading -> {
                }
            }
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}