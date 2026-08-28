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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aichatapp.viewmodel.LoginViewModel
import androidx.compose.runtime.setValue
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel= hiltViewModel()
){
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    LaunchedEffect(loginSuccess) {
        if (loginSuccess){
navController.navigate("chat"){
    popUpTo("login"){
        inclusive=false
    }
}


        }



    }

    Column{
        TextField(

            value=username,

            onValueChange={
                username=it
            }

        )
        OutlinedTextField(value = password,
            onValueChange = {
                password=it

            }, label = {
                Text(text = "密码")
            }



            )





        Button(
            onClick={
                println("点击登录按钮")
                viewModel.login(username,password)

            }
        ){

            Text("登录")

        }




    }
























}