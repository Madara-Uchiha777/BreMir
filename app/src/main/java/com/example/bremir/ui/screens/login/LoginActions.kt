package com.example.bremir.ui.screens.login

import android.content.Context

interface LoginActions {
    fun emailChanged(email: String?)
    fun passwordChanged(password: String?)
    fun login()
    fun forgotPassword()
    fun signUp()
}