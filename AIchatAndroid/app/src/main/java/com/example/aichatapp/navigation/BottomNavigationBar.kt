package com.example.aichatapp.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController


data class BottomNavItem(

    val route:String,

    val title:String,

    val icon:ImageVector

)



val bottomNavItems = listOf(


    BottomNavItem(

        route = AppRoute.CONVERSATIONS,

        title = "聊天",

        icon = Icons.Default.Chat

    ),



    BottomNavItem(

        route = AppRoute.HISTORY,

        title = "历史",

        icon = Icons.Default.History

    ),



    BottomNavItem(

        route = AppRoute.SETTING,

        title = "设置",

        icon = Icons.Default.Settings

    )




)



@Composable
fun BottomNavigationBar(

    navController: NavController,
    currentRoute:String?



){


    NavigationBar {



        bottomNavItems.forEach { item ->



            NavigationBarItem(


                selected =
                    currentRoute == item.route,


                onClick = {


                    if(currentRoute != item.route){


                        navController.navigate(item.route){

                            launchSingleTop = true

                        }

                    }


                },


                icon = {


                    Icon(

                        imageVector = item.icon,

                        contentDescription = item.title

                    )


                },


                label = {


                    Text(item.title)


                }



            )



        }



    }



}