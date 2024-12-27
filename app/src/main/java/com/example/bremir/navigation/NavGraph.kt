package com.example.bremir.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bremir.ui.screens.add_edit_home.AddEditHomeScreen
import com.example.bremir.ui.screens.home_detail.HomeDetailScreen
import com.example.bremir.ui.screens.login.LoginScreen
import com.example.bremir.ui.screens.main.MainScreen
import com.example.bremir.ui.screens.password_recovery.PasswordRecoveryScreen
import com.example.bremir.ui.screens.register.SignUpScreen

@ExperimentalFoundationApi
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    navigation: INavigationRouter = remember { NavigationRouterImpl(navController) },
    startDestination: String
) {

    NavHost(
        navController = navController,
        startDestination = startDestination){

        composable(Destination.LoginScreen.route){
            LoginScreen(navigationRouter = navigation)
        }

        composable(Destination.PasswordRecoveryScreen.route){
            PasswordRecoveryScreen(navigationRouter = navigation)
        }

        composable(Destination.SignUpScreen.route){
            SignUpScreen(navigationRouter = navigation)
        }

        composable(Destination.MainScreen.route) {
            MainScreen(navigation = navigation)
        }

        composable(
            Destination.AddEditHomeScreen.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){
            val id = it.arguments?.getString("id")
            AddEditHomeScreen(navigation = navigation, id = id)
        }

        composable(
            Destination.AddEditHomeScreen.route
        ){
            AddEditHomeScreen(navigation = navigation, id = null)
        }

        composable(
            Destination.HomeDetailScreen.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){
            val id = it.arguments!!.getString("id")
            HomeDetailScreen(navigation = navigation, id = id!!)
        }
    }
}
