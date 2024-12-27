package com.example.bremir.navigation

sealed class Destination(
    val route: String
){
    object LoginScreen : Destination("login")
    object SignUpScreen : Destination("sign_up")
    object PasswordRecoveryScreen : Destination("forgot_password")
    object MainScreen : Destination("main")
    object AddEditHomeScreen : Destination(route = "add_edit_home")
    object HomeDetailScreen: Destination(route = "home_detail")
}
