package com.example.aichatapp.view.history

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aichatapp.view.components.MessageItem
import com.example.aichatapp.viewmodel.HistoryViewModel


@Composable
fun HistoryScreen(
    viewmodel: HistoryViewModel=hiltViewModel()


){
val messages by viewmodel.messages.collectAsState()
LazyColumn {
    items(messages){message->

        MessageItem(
            message = message
        )

    }
}
}