package com.example.aichatapp.view.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(){

    Column(modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
    {

        Text(
            text = "👋你好"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "我是AI助手"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "有什么可以帮助你？"
        )




    }





}