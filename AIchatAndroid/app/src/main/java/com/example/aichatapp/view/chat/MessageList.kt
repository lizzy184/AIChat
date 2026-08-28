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
fun MessageList(messages: List<Message>,isLoading: Boolean,modifier: Modifier){
    val listState = rememberLazyListState()




    LaunchedEffect(messages.size,
        isLoading

    ){

        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)


        }

    }








    LazyColumn(
        state =listState ,
        modifier = modifier


    ){
if(messages.isEmpty()){


     item {
         EmptyState()
     }
}


       else items(messages){ message ->

            MessageItem(message)






        }



        //loading状态




            item {
                AnimatedVisibility(visible = isLoading){
                    LoadingMessage()

                }




            }











    }




}
