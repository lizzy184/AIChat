package com.example.aichatapp.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.navigation.NavHostController
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.navArgument


import com.example.aichatapp.view.chat.ChatScreen
import com.example.aichatapp.view.conversation.ConversationScreen
import com.example.aichatapp.view.history.HistoryScreen


import com.example.aichatapp.view.login.LoginScreen
import com.example.aichatapp.view.register.RegisterScreen
import com.example.aichatapp.view.setting.SettingScreen
import com.example.aichatapp.view.setting.AboutScreen
import com.example.aichatapp.view.start.StartScreen



@Composable
fun AppNavigation(

    navController: NavHostController,

    modifier: Modifier = Modifier

){
    var currentConversationId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }


    NavHost(

        navController = navController,

        startDestination = AppRoute.START,

        modifier = modifier

    ){



        /*
        启动页
        */

        composable(
            AppRoute.START
        ){

            StartScreen(
                navController
            )

        }





        /*
        注册
        */

        composable(
            AppRoute.REGISTER
        ){

            RegisterScreen(
                navController
            )

        }





        /*
        登录
        */

        composable(
            AppRoute.LOGIN
        ){

            LoginScreen(
                navController
            )

        }





        /*
        =========================
        会话列表
        =========================

        登录成功后进入这里

        */

        composable(
            AppRoute.CONVERSATIONS
        ){


            ConversationScreen(

                onConversationClick = {

                        conversationId ->



                    navController.navigate(

                        AppRoute.CHAT_WITH_ID.replace(
                            "{conversationId}",
                            conversationId.toString()
                        )

                    )

                },

                        onNewConversation = {

                    navController.navigate(
                        AppRoute.NEW_CHAT
                    )

                }
            )


        }

        composable("history") {
            HistoryScreen(currentConversationId )
        }




        /*
        =========================
        新聊天
        =========================
        */






        /*
        =========================
        已存在会话聊天
        =========================
        */
        composable(

            route = AppRoute.NEW_CHAT

        ){


            ChatScreen(

                conversationId = null,

                onConversationIdChanged = { id ->
                    currentConversationId = id
                },
                onBack = {

                    navController.popBackStack()

                }


            )


        }

        composable(

            route = AppRoute.CHAT_WITH_ID,


            arguments = listOf(

                navArgument(
                    "conversationId"
                ){

                    type =
                        NavType.IntType

                }

            )


        ){entry ->




            val id =

                entry.arguments
                    ?.getInt(
                        "conversationId"
                    )




            ChatScreen(


                conversationId = id,



                onBack = {



                        needRefresh ->




                    /*
                    返回会话列表

                    */

                    navController.popBackStack()



                },
                onConversationIdChanged = {newId ->
                    currentConversationId = newId



                }



            )



        }




        /*
        设置
        */


        composable(
            AppRoute.SETTING
        ){


            SettingScreen(
                navController
            )

        }





        /*
        关于
        */


        composable(
            AppRoute.ABOUT
        ){


            AboutScreen(
                navController
            )


        }



    }

}