package com.example.aichatapp.view.start


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

import com.example.aichatapp.data.UserPreferences

import com.example.aichatapp.view.chat.ChatScreen
import com.example.aichatapp.view.login.LoginScreen
import com.example.aichatapp.view.register.RegisterScreen



@Composable
fun StartScreen(navController: NavController){


    // 获取Android上下文
    val context = LocalContext.current



    // 创建UserPreferences
    val userPreferences =
        remember {

            UserPreferences(context)

        }





    val token by
    userPreferences.accessToken
        .collectAsState(initial = null)
val hasRegistered by userPreferences.hasRegistered.collectAsState(initial = false)

    when {

        token != null -> {
            ChatScreen()
        }

        hasRegistered -> {
            LoginScreen(navController)
        }

        else -> {
            RegisterScreen(navController)
        }
    }


}