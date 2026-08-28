package com.example.aichatapp.view.setting


import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import com.example.aichatapp.data.UserPreferences

import com.example.aichatapp.navigation.AppRoute
import com.example.aichatapp.viewmodel.ChatViewModel
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(

    navController: NavController

){
    val scope = rememberCoroutineScope()

val context= LocalContext.current
    /**
     *
     * 获取ChatViewModel
     *
     * 用于调用清空聊天记录
     *
     */


val userPreferences=remember {
        UserPreferences(context)


    }

    val userId by
    userPreferences.userId
        .collectAsState(null)
    val viewModel: ChatViewModel = hiltViewModel()






    var darkMode by remember {


        mutableStateOf(false)


    }





    var notificationEnable by remember {


        mutableStateOf(false)


    }





    /**
     *
     * 控制删除确认弹窗显示
     *
     */

    var showClearDialog by remember {


        mutableStateOf(false)


    }






    Column(

        modifier = Modifier

            .padding(16.dp)

    ) {





        // 深色模式

        SettingItem(

            title = "深色模式",

            checked = darkMode,

            onCheckedChange = {


                darkMode = it


            }

        )







        // 消息通知

        SettingItem(

            title = "消息通知",

            checked = notificationEnable,

            onCheckedChange = {


                notificationEnable = it


            }

        )







        // 关于APP

        SettingItem(

            title = "关于APP",

            onClick = {


                navController.navigate(

                    AppRoute.ABOUT

                )


            }

        )







        // 清除聊天记录

        SettingItem(

            title = "清除聊天记录",

            onClick = {


                showClearDialog = true


            }

        )

SettingItem(
    title = "退出登录",
    onClick = {
        scope.launch {
            userPreferences.clearUserId()
            navController.navigate(
                "login"
            ){

                popUpTo(0)

            }
        }



}









)



    }







    /**
     *
     * 删除确认弹窗
     *
     */

    if(showClearDialog){



        AlertDialog(



            onDismissRequest = {


                showClearDialog = false


            },





            title = {


                Text(

                    text = "清除聊天记录"

                )


            },





            text = {


                Text(

                    text = "确定要删除所有聊天记录吗？此操作无法恢复。"

                )


            },





            confirmButton = {



                TextButton(


                    onClick = {



                        viewModel.clearMessages()



                        showClearDialog = false



                    }


                ){


                    Text(

                        text = "确定"

                    )


                }



            },







            dismissButton = {



                TextButton(


                    onClick = {



                        showClearDialog = false



                    }


                ){



                    Text(

                        text = "取消"

                    )


                }


            }





        )


    }





}