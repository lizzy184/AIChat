package com.example.aichatapp.view.chat


import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun ChatInput(

    inputText: String,

    onTextChange: (String) -> Unit,

    onSendClick: () -> Unit,
    isLoading: Boolean

){

    Row {


        TextField(

            value = inputText,


            onValueChange = {

                    newText ->

                onTextChange(newText)

            },


            modifier = Modifier.weight(1f),


            label = {

                Text("input your things")

            },


            maxLines = 3,


            keyboardOptions = KeyboardOptions(

                keyboardType = KeyboardType.Text,

                imeAction = ImeAction.Go

            )

        )



        Button(
           enabled = inputText.isNotBlank()&&!isLoading,
            onClick = {

                onSendClick()

            }

        ){if (isLoading){
            CircularProgressIndicator()
        }

            Text(
                text = "send"
            )

        }


    }

}