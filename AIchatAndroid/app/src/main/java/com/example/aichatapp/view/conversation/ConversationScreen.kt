package com.example.aichatapp.view.conversation


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel

import com.example.aichatapp.navigation.AppRoute
import com.example.aichatapp.viewmodel.ConversationViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(

    onNewConversation: () -> Unit,
    onConversationClick:(Int)->Unit,


    viewModel:ConversationViewModel = hiltViewModel()


){



    val conversations by
    viewModel.conversations.collectAsState()



    val isLoading by
    viewModel.isLoading.collectAsState()



    val errorMessage by
    viewModel.errorMessage.collectAsState()





    /*
    =========================
        页面进入刷新
    =========================
     */


    LaunchedEffect(Unit){

        viewModel.loadConversations()

    }







    Scaffold(



        topBar = {


            TopAppBar(

                title = {

                    Text(
                        "聊天"
                    )

                }

            )


        },




        floatingActionButton = {


            FloatingActionButton(


                onClick = {



                    /*
                    新聊天

                    不再使用 -1

                    直接进入 chat/new

                    */


                    onNewConversation()



                }


            ){



                Icon(

                    imageVector =
                        Icons.Default.Add,

                    contentDescription =
                        "新聊天"

                )



            }


        }



    ){ padding ->





        Column(


            modifier =
                Modifier

                    .fillMaxSize()

                    .padding(padding)



        ){





            if(isLoading){


                Box(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center

                ){


                    CircularProgressIndicator()


                }



            }else{





                LazyColumn(


                    modifier =
                        Modifier.fillMaxSize()



                ){



                    items(

                        conversations

                    ){ conversation ->





                        Card(


                            modifier =
                                Modifier

                                    .fillMaxWidth()

                                    .padding(
                                        8.dp
                                    )

                                    .clickable {



                                        /*
                                        已存在会话

                                        传真实id

                                        */


                                        onConversationClick(

                                            conversation.id

                                        )



                                    }



                        ){



                            Row(


                                modifier =
                                    Modifier

                                        .fillMaxWidth()

                                        .padding(
                                            16.dp
                                        ),



                                verticalAlignment =
                                    Alignment.CenterVertically



                            ){





                                Column(


                                    modifier =
                                        Modifier.weight(
                                            1f
                                        )


                                ){



                                    Text(

                                        text =
                                            conversation.title

                                    )



                                    Spacer(
                                        Modifier.height(
                                            4.dp
                                        )
                                    )



                                    Text(

                                        text =
                                            conversation.created_time,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall

                                    )



                                }





                                IconButton(



                                    onClick = {



                                        viewModel.deleteConversation(

                                            conversation.id

                                        )


                                    }



                                ){



                                    Icon(

                                        imageVector =
                                            Icons.Default.Delete,

                                        contentDescription =
                                            "删除"

                                    )



                                }





                            }





                        }





                    }





                }





            }






            errorMessage?.let {


                Text(

                    text = it,

                    color =
                        MaterialTheme.colorScheme.error,

                    modifier =
                        Modifier.padding(
                            16.dp
                        )

                )


            }





        }





    }





}