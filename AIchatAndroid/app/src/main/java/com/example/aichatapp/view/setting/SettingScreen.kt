
package com.example.aichatapp.view.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.example.aichatapp.navigation.AppRoute
import com.example.aichatapp.viewmodel.ConversationViewModel
import com.example.aichatapp.viewmodel.LogoutViewModel
import com.example.aichatapp.viewmodel.ThemeViewModel

@Composable
fun SettingScreen(
    navController: NavController,
    conversationId: Int?
) {

    // ViewModel
    val conversationViewModel: ConversationViewModel =
        hiltViewModel()

    val logoutViewModel: LogoutViewModel =
        hiltViewModel()

    val themeViewModel: ThemeViewModel =
        hiltViewModel()

    // 深色模式状态：来自 DataStore
    val darkMode by themeViewModel.darkMode.collectAsState()

    // 登出状态
    val errorMessage by
    logoutViewModel.errorMessage.collectAsState()

    val logoutSuccess by
    logoutViewModel.logoutSuccess.collectAsState()

    // 消息通知状态（暂时为本地状态）
    var notificationEnable by remember {
        mutableStateOf(false)
    }

    // 删除弹窗
    var showClearDialog by remember {
        mutableStateOf(false)
    }

    // 登出成功后跳转登录页面
    if (logoutSuccess) {
        androidx.compose.runtime.LaunchedEffect(logoutSuccess) {
            navController.navigate(AppRoute.LOGIN) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        // 深色模式
        SettingItem(
            title = "深色模式",
            checked = darkMode,
            onCheckedChange = { enabled ->
                themeViewModel.setDarkMode(enabled)
            }
        )

        // 消息通知
        SettingItem(
            title = "消息通知",
            checked = notificationEnable,
            onCheckedChange = { enabled ->
                notificationEnable = enabled
            }
        )

        // 关于 APP
        SettingItem(
            title = "关于APP",
            onClick = {
                navController.navigate(AppRoute.ABOUT)
            }
        )

        // 清除聊天记录
        SettingItem(
            title = "清除聊天记录",
            onClick = {
                showClearDialog = true
            }
        )

        // 退出登录
        SettingItem(
            title = "退出登录",
            onClick = {
                logoutViewModel.logout()
            }
        )
    }

    // 清除聊天记录确认弹窗
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
            },
            title = {
                Text(text = "清除聊天记录")
            },
            text = {
                Text(
                    text = "确定要删除当前会话的聊天记录吗？此操作无法恢复。"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        conversationId?.let { id ->
                            conversationViewModel
                                .clearConversationMessages(id)
                        }
                        showClearDialog = false
                    }
                ) {
                    Text(text = "确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                    }
                ) {
                    Text(text = "取消")
                }
            }
        )
    }

    // 登出错误提示
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                logoutViewModel.clearError()
            },
            title = {
                Text(text = "退出登录失败")
            },
            text = {
                Text(text = errorMessage ?: "")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        logoutViewModel.clearError()
                    }
                ) {
                    Text(text = "确定")
                }
            }
        )
    }
}
