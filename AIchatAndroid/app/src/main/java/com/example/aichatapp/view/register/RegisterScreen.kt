
package com.example.aichatapp.view.register

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

import com.example.aichatapp.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {

    val registerSuccess by viewModel.registerSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    LaunchedEffect(registerSuccess) {

        if (registerSuccess) {

            navController.navigate("login") {

                popUpTo("register") {
                    inclusive = true
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

        // 注册失败时显示错误
        if (errorMessage != null) {
            Text(
                text = errorMessage!!
            )
        }

        Button(
            onClick = {
                println("点击注册按钮")
                viewModel.register(username, password)
            }
        ) {

            Text("注册")
        }
    }
}

