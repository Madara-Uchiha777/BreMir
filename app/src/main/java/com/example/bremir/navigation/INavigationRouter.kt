package com.example.bremir.navigation

import androidx.navigation.NavController

interface INavigationRouter {
    fun getNavController(): NavController
    fun navigateToLogin()
    fun navigateToSignUp()
    fun navigateToPasswordRecovery()
    fun navigateToMainScreen()
    fun navigateToAddEditHomeScreen(id: String?)
    fun navigateToHomeDetailScreen(id: String?)
    fun returnBack()
}