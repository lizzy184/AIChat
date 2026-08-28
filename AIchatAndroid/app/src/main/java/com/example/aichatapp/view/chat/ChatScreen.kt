package com.example.aichatapp.view.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.aichatapp.viewmodel.ChatViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment

@Composable
fun ChatScreen(){



    val viewModel: ChatViewModel = hiltViewModel()


    //收集ViewModel里面的UiState
    val uiState by viewModel.uiState.collectAsState()

val snackbarHostState= remember{
    SnackbarHostState()
}
    LaunchedEffect(
        uiState.errorMessage
    ){

        uiState.errorMessage?.let { message ->

        val result=snackbarHostState.showSnackbar(
                message=message,
            actionLabel = "重新发送"
            )

            if (result== SnackbarResult.ActionPerformed){
    viewModel.retryLastMessage()

            }
            viewModel.clearError()
        }

    }

    Box(modifier = Modifier.fillMaxSize()) {


        Column(
            modifier = Modifier.fillMaxSize()
        ) {


            Text(
                modifier = Modifier.fillMaxWidth(),

                text = "AI chat APP",

                color = Color.Black,

                fontSize = MaterialTheme.typography.headlineLarge.fontSize,

                fontWeight = FontWeight.Normal,

                fontStyle = FontStyle.Italic,

                textAlign = TextAlign.Center
            )


            MessageList(
                uiState.messages,
                uiState.isLoading,
                modifier = Modifier.weight(1f)
                    .fillMaxWidth()
            )







            ChatInput(
                inputText = uiState.inputText,
                onTextChange = { newText -> viewModel.onTextChange(newText) },
                onSendClick = { viewModel.sendMessage() },
                isLoading = uiState.isLoading
            )


        }


        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )

    }



    }








@Preview
@Composable
fun ChatScreenPreview(){





}