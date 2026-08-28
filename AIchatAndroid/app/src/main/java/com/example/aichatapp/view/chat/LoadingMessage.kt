package com.example.aichatapp.view.chat

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.delay


@Composable
fun LoadingMessage(){

    var dots by remember{mutableStateOf("")}

    LaunchedEffect(Unit) {
while (true){


    dots =
        when(dots){

            "" -> "."

            "." -> ".."

            ".." -> "..."

            else -> ""

        }


delay(500)



}

    }


    Text(

        text =
            "AI正在思考$dots"

    )

}


