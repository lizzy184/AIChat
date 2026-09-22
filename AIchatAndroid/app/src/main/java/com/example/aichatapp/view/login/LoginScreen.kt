
package com.example.aichatapp.view.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aichatapp.navigation.AppRoute

import com.example.aichatapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }


        LaunchedEffect(loginSuccess) {

            if(loginSuccess){


                navController.navigate(
                    AppRoute.CONVERSATIONS
                ){

                    popUpTo(
                        AppRoute.LOGIN
                    ){

                        inclusive=true

                    }

                }

            }

        }

    Column {

        TextField(
            value = username,
            onValueChange = {
                username = it
                viewModel.clearError()
            },
            label = {
                Text("用户名")
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.clearError()
            },
            label = {
                Text("密码")
            },
            visualTransformation = PasswordVisualTransformation()
        )

        // 登录失败时显示错误信息
        if (errorMessage != null) {
            Text(
                text = errorMessage!!
            )
        }

        Button(
            onClick = {
                println("点击登录按钮")
                viewModel.login(username, password)
            }
        ) {
            Text("登录")
        }
    }
}

