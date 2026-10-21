
package com.example.aichatapp

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.aichatapp.navigation.AppNavigation
import com.example.aichatapp.navigation.BottomNavigationBar
import com.example.aichatapp.ui.theme.AIchatAPPTheme
import com.example.aichatapp.viewmodel.ThemeViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            // 主题状态管理
            val themeViewModel: ThemeViewModel = hiltViewModel()

            // 监听当前深色模式状态
            val darkMode by themeViewModel.darkMode.collectAsState()

            // 根据用户设置应用全局主题
            AIchatAPPTheme(
                darkTheme = darkMode,
                dynamicColor = false
            ) {

                val navController = rememberNavController()

                val navBackStackEntry by
                navController.currentBackStackEntryAsState()

                val currentRoute =
                    navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                ) { innerPadding ->

                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
