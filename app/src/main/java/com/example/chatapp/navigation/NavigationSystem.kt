package com.example.chatapp.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatapp.ui.MainViewModel
import com.example.chatapp.ui.screens.ChatScreen
import com.example.chatapp.ui.screens.HomeScreen
import com.example.chatapp.ui.screens.ImageUploadScreen
import com.example.chatapp.ui.screens.LogInScreen
import com.example.chatapp.ui.screens.SignUpScreen

@Composable
fun NavigationSystem(){
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "route1"){

        navigation(
            startDestination = "LogIn_Screen",
            route = "route1"
        ){
            composable("LogIn_Screen"){ entry ->
                val viewModel = entry.mainViewModel<MainViewModel>(navController = navController)
                LogInScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable("SignUp_Screen"){ entry ->
                val viewModel = entry.mainViewModel<MainViewModel>(navController = navController)
                SignUpScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable("ImageUpload_Screen"){ entry ->
                val viewModel = entry.mainViewModel<MainViewModel>(navController = navController)
                ImageUploadScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        navigation(
            startDestination = "Home_Screen",
            route = "route2"
        ){
            composable("Home_Screen"){ entry ->
                val viewModel = entry.mainViewModel<MainViewModel>(navController = navController)
                HomeScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(
                route ="Chat_Screen/{user_name}/{user_uid}",
                arguments = listOf(
                    navArgument("user_name"){
                        type = NavType.StringType
                    },
                    navArgument("user_uid"){
                        type = NavType.StringType
                    }
                )
            ){ entry ->
                val viewModel = entry.mainViewModel<MainViewModel>(navController = navController)
                ChatScreen(
                    viewModel = viewModel,
                    userName = entry.arguments?.getString("user_name"),
                    userUid = entry.arguments?.getString("user_uid")
                )
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.mainViewModel(
    navController: NavController,
): T {
    val navGraphRoute =destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this){
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}