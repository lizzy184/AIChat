package com.example.aichatapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState

import com.example.aichatapp.navigation.AppNavigation
import com.example.aichatapp.navigation.BottomNavigationBar
import com.example.aichatapp.ui.theme.AIchatAPPTheme

import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.LaunchedEffect

import com.example.aichatapp.data.UserPreferences

import kotlinx.coroutines.flow.drop

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)


        enableEdgeToEdge()


        setContent {


            AIchatAPPTheme {


                val navController =
                    rememberNavController()
                val navBackStackEntry by
                navController.currentBackStackEntryAsState()
                val userPreferences = UserPreferences(
                    applicationContext
                )

                val currentRoute =
                    navBackStackEntry?.destination?.route

                LaunchedEffect(Unit) {

                    userPreferences.accessToken
                        .drop(1)
                        .collect { token ->

                            if (token == null) {

                                navController.navigate("login") {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            }
                        }
                }
                Scaffold(

                    modifier = Modifier.fillMaxSize(),




                    bottomBar = {


                        BottomNavigationBar(
                            navController = navController,
                            currentRoute = currentRoute

                        )


                    }


                ){  innerPadding ->



                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)

                    )


                }


            }


        }


    }


}