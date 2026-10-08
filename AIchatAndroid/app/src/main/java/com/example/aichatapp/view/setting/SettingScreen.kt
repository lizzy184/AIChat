package com.example.aichatapp.view.setting


import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.aichatapp.viewmodel.LogoutViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(

    navController: NavController

){



    /**
     *
     * 获取ChatViewModel
     *
     * 用于调用清空聊天记录
     *
     */




    val viewModel: ChatViewModel = hiltViewModel()

   val logoutViewModel: LogoutViewModel= hiltViewModel()
val errorMessage=logoutViewModel.errorMessage.collectAsState()
val logoutSuccess=logoutViewModel.logoutSuccess.collectAsState()


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


    LaunchedEffect(logoutSuccess) {
        navController.navigate(AppRoute.LOGIN)
        {
            popUpTo(0)
        }

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
        logoutViewModel.logout()

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

    if (errorMessage.value != null) {

        AlertDialog(

            onDismissRequest = {
                logoutViewModel.clearError()
            },

            title = {
                Text(
                    text = "退出登录失败"
                )
            },

            text = {
                Text(
                    text = errorMessage.value ?: ""
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        logoutViewModel.clearError()
                    }
                ) {
                    Text(
                        text = "确定"
                    )
                }
            }
        )
    }



}