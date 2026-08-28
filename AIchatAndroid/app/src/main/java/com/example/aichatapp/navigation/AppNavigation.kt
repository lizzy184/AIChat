package com.example.aichatapp.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

import com.example.aichatapp.view.chat.ChatScreen
import com.example.aichatapp.view.history.HistoryScreen
import com.example.aichatapp.view.login.LoginScreen
import com.example.aichatapp.view.register.RegisterScreen
import com.example.aichatapp.view.setting.SettingScreen

import com.example.aichatapp.view.setting.AboutScreen
import com.example.aichatapp.view.start.StartScreen

@Composable
fun AppNavigation(

    navController: NavHostController,
    modifier: Modifier= Modifier

){



    NavHost(

        navController = navController,

        startDestination = "start",
        modifier=modifier


    ){


        composable(
            route = "chat"
        ){

            ChatScreen()

        }



        composable(
            route = "history"
        ){

            HistoryScreen()

        }



        composable(
            route = "setting"
        ){

            SettingScreen(  navController = navController
            )

        }
        composable(
            route = "about"
        ){

            AboutScreen(navController=navController)

        }


        composable("start"){


            StartScreen()


        }

        composable("register") {
            RegisterScreen(navController)


        }

composable("login") {
    LoginScreen(navController)

}

    }



}