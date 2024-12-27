package com.example.bremir.navigation

import androidx.navigation.NavController

class NavigationRouterImpl(private val navController: NavController) : INavigationRouter {

    override fun getNavController(): NavController = navController

    override fun navigateToLogin() {
        navController.navigate(Destination.LoginScreen.route)
    }

    override fun navigateToSignUp() {
        navController.navigate(Destination.SignUpScreen.route)
    }

    override fun navigateToPasswordRecovery() {
        navController.navigate(Destination.PasswordRecoveryScreen.route)
    }

    override fun navigateToMainScreen() {
        navController.navigate(Destination.MainScreen.route)
    }

    override fun navigateToAddEditHomeScreen(id: String?) {
        if (id != null) {
            navController.navigate(Destination.AddEditHomeScreen.route + "/" + id)
        } else {
            navController.navigate(Destination.AddEditHomeScreen.route)
        }
    }

    override fun navigateToHomeDetailScreen(id: String?) {
        if (id != null) {
            navController.navigate(Destination.HomeDetailScreen.route + "/" + id)
        }
    }

    override fun returnBack() {
        navController.popBackStack()
    }
}