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
fun StartScreen(){


    // 获取Android上下文
    val context = LocalContext.current

val navController= NavController(context)

    // 创建UserPreferences
    val userPreferences =
        remember {

            UserPreferences(context)

        }



    // 监听保存的userid

    val userId by
    userPreferences.userId
        .collectAsState(
            initial = null
        )

val hasRegistered by userPreferences.hasRegistered.collectAsState(initial = false)


    when{


        //已经登录

        userId != null -> {


            ChatScreen()

        }



        //注册过，但是退出了

        hasRegistered -> {


            LoginScreen(navController)


        }



        //第一次使用

        else -> {


            RegisterScreen(navController)


        }


    }


}