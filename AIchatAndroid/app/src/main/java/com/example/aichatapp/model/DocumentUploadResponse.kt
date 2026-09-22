package com.example.aichatapp.model

data class DocumentUploadResponse(

    val filename: String,

    val content_type: String,

    val size: Long,

    val pages: Int,

    val chunks: Int,

    val status: String

)