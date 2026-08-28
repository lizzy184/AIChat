package com.example.aichatapp.view.setting


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(

    navController: NavController

){


    Scaffold(


        topBar = {


            TopAppBar(


                title = {


                    Text(
                        "关于APP"
                    )


                },


                navigationIcon = {


                    IconButton(


                        onClick = {


                            navController.popBackStack()


                        }


                    ){


                        Icon(

                            imageVector = Icons.Default.ArrowBack,

                            contentDescription = "返回"

                        )


                    }


                }


            )


        }



    ){ innerPadding ->



        Column(

            modifier =
                Modifier.padding(innerPadding)

        ){


            Text(

                text = "AI Chat APP"

            )


            Text(

                text = "版本：1.0"

            )


        }



    }


}