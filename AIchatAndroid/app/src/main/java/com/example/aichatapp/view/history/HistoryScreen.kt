package com.example.aichatapp.view.history

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aichatapp.view.components.MessageItem
import com.example.aichatapp.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    conversationId: Int?,
    viewModel: HistoryViewModel = hiltViewModel()
) {

    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
    }

    LazyColumn {

        items(messages) { message ->

            MessageItem(
                message = message
            )
        }
    }
}