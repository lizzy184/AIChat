package com.example.aichatapp.model

data class HistoryMessage(

    val id:Int,

    val user_id:Int,

    val role:String,

    val content:String,

    val created_time:String

)