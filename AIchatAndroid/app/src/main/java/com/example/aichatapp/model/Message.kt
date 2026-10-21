package com.example.aichatapp.model

import java.util.UUID


data class Message(

    val id:String =
        UUID.randomUUID().toString(),

    val text:String,

    val isUser:Boolean,

    val time:String,

    val isStreaming:Boolean = false

)