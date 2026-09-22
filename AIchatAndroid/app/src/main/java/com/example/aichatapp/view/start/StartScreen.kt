package com.example.aichatapp.view.start


import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

import com.example.aichatapp.data.UserPreferences
import com.example.aichatapp.view.login.LoginScreen
import com.example.aichatapp.view.register.RegisterScreen


@Composable
fun StartScreen(

    navController: NavController

){


    val context =
        LocalContext.current



    val userPreferences =
        remember {

            UserPreferences(context)

        }



    val token by
    userPreferences.accessToken
        .collectAsState(
            initial = null
        )


    val hasRegistered by
    userPreferences.hasRegistered
        .collectAsState(
            initial=false
        )




    LaunchedEffect(token){


        if(token!=null){


            navController.navigate(
                "conversations"
            ){

                popUpTo("start"){

                    inclusive=true

                }

            }


        }


    }





    if(token==null){


        if(hasRegistered){


            LoginScreen(
                navController
            )


        }else{


            RegisterScreen(
                navController
            )


        }


    }



}