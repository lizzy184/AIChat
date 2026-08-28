package com.example.aichatapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aichatapp.model.Message

@Composable
fun MessageItem(message: Message){



    if(message.isUser){


        //用户消息靠右

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.End
        ){


            Column(
                horizontalAlignment = Alignment.End
            ){


                Text(

                    text = message.text,


                    modifier = Modifier

                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .padding(20.dp),


                    color = Color.White
                )


                Text(
                    text = message.time
                )

            }


        }


    }else{


        //AI消息靠左

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.Start
        ){


            Column{


                Text(

                    text = message.text,


                    modifier = Modifier

                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .padding(20.dp),


                    color = Color.White
                )


                Text(
                    text = message.time
                )


            }


        }


    }



}