
package com.example.aichatapp.view.chat


import android.net.Uri
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

import androidx.hilt.navigation.compose.hiltViewModel

import com.example.aichatapp.viewmodel.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(

    conversationId: Int?,

    onBack: (Boolean) -> Unit,

    onConversationIdChanged: (Int?) -> Unit,

    viewModel: ChatViewModel = hiltViewModel(),

    /*
    =========================
        PDF选择结果
    =========================

    默认不做任何事情。

    所以现有调用 ChatScreen(...)
    的地方不用修改。
    */

    onPdfSelected: (Uri) -> Unit = {}

) {


    /*
    =========================
        PDF文件选择器
    =========================
    */

    val pdfLauncher =
        rememberLauncherForActivityResult(

            contract =
                ActivityResultContracts.OpenDocument()

        ) { uri ->

            uri?.let {

                Log.d(
                    "PDF_UPLOAD",
                    "选择PDF：$it"
                )

                /*
                当前阶段：

                只拿到PDF Uri，
                不进行真正上传。

                后面再把这里连接到
                ViewModel / Repository。
                */

                viewModel.uploadPdf(it)

            }

        }


    /*
    =========================
        UI State
    =========================
    */

    val uiState by
    viewModel.uiState.collectAsState()


    val currentConversationId by
    viewModel.currentConversationId.collectAsState()


    val snackbarHostState =
        remember {

            SnackbarHostState()

        }


    /*
    =========================
        初始化当前会话
    =========================
    */

    LaunchedEffect(conversationId) {

        Log.d(
            "CHAT",
            "进入聊天 conversationId=$conversationId"
        )


        if (conversationId != null) {

            viewModel.setConversationId(
                conversationId
            )

            viewModel.loadMessages(
                conversationId
            )

        } else {

            viewModel.newConversation()

        }

    }


    /*
    =========================
        conversationId变化
    =========================
    */

    LaunchedEffect(currentConversationId) {

        if (currentConversationId != null) {

            onConversationIdChanged(
                currentConversationId
            )

        }

    }


    /*
    =========================
        错误提示
    =========================
    */

    LaunchedEffect(
        uiState.errorMessage
    ) {

        uiState.errorMessage?.let { message ->

            val result =
                snackbarHostState.showSnackbar(

                    message = message,

                    actionLabel = "重新发送"

                )


            if (
                result ==
                androidx.compose.material3.SnackbarResult.ActionPerformed
            ) {

                viewModel.retryLastMessage()

            }


            viewModel.clearError()

        }

    }
    LaunchedEffect(uiState.uploadMessage) {

        uiState.uploadMessage?.let { message ->

            snackbarHostState.showSnackbar(
                message
            )

            viewModel.clearUploadMessage()
        }
    }

    /*
    =========================
        页面
    =========================
    */

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(

                        text = "AI Chat",

                        modifier =
                            Modifier.fillMaxWidth(),

                        textAlign =
                            TextAlign.Center

                    )

                },


                navigationIcon = {

                    IconButton(

                        onClick = {

                            onBack(true)

                        }

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "返回"

                        )

                    }

                }

            )

        },


        snackbarHost = {

            SnackbarHost(

                hostState =
                    snackbarHostState

            )

        }

    ) { padding ->


        Column(

            modifier =
                Modifier

                    .fillMaxSize()

                    .padding(padding)

        ) {


            /*
            =========================
                消息列表
            =========================
            */

            MessageList(

                messages =
                    uiState.messages,

                isLoading =
                    uiState.isLoading,

                modifier =
                    Modifier

                        .weight(1f)

                        .fillMaxWidth()

            )


            /*
            =========================
                输入区域
            =========================

                PDF按钮已经移动到
                ChatInput内部。

                最终布局：

                [输入框] [PDF] [send]
            */

            ChatInput(

                inputText =
                    uiState.inputText,

                onTextChange = { text ->

                    viewModel.onTextChange(
                        text
                    )

                },

                onSendClick = {

                    viewModel.sendMessage()

                },

                isLoading =
                    uiState.isLoading,

                onPdfClick = {

                    /*
                    打开Android文件选择器

                    只允许选择PDF
                    */

                    pdfLauncher.launch(

                        arrayOf(
                            "application/pdf"
                        )

                    )

                }

            )

        }

    }

}
