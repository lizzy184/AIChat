package com.example.aichatapp.model

data class ChatUiState(
    val messages: List<Message> = emptyList(),

    val inputText: String = "",

    val isLoading: Boolean = false,

    val isUploading: Boolean = false,

    val errorMessage: String? = null,

    val uploadMessage: String? = null
)