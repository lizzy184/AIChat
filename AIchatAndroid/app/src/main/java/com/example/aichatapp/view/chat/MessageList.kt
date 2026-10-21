package com.example.aichatapp.view.chat


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.Modifier

import com.example.aichatapp.model.Message
import com.example.aichatapp.view.components.MessageItem



@Composable
fun MessageList(

    messages: List<Message>,

    isLoading: Boolean,

    modifier: Modifier

) {


    val listState =
        rememberLazyListState()



    /*
    =========================================

    自动滚动优化

    监听：

    最后一条消息id
    +
    文本长度


    原：

    text全文

    会导致大量重组


    现在：

    Java
    length=4

    只关心变化


    =========================================
    */


    val lastMessageKey =
        messages.lastOrNull()?.let {

            "${it.id}_${it.text.length}"

        }



    LaunchedEffect(
        lastMessageKey,
        isLoading
    ) {


        if(messages.isNotEmpty()){


            listState.scrollToItem(

                messages.lastIndex

            )


        }


    }




    LazyColumn(

        state = listState,

        modifier = modifier

    ){



        /*
        =================================
        空状态
        =================================
        */


        if(messages.isEmpty()){


            item {


                EmptyState()


            }


        }



        /*
        =================================
        消息列表

        使用id作为key

        配合Message.id

        防止Compose错位

        =================================
        */


        items(

            items = messages,

            key = {

                    message ->

                message.id

            }

        ){ message ->


            MessageItem(

                message = message

            )


        }




        /*
        =================================
        AI加载提示

        注意：

        这里不是AI正文

        只是请求状态

        =================================
        */


        item {


            AnimatedVisibility(

                visible = isLoading

            ){


                LoadingMessage()


            }


        }


    }


}