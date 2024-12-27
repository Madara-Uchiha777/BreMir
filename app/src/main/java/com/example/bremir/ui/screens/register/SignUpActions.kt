package com.example.bremir.ui.screens.register

interface SignUpActions {
    fun emailChanged(email: String?)
    fun passwordChanged(password: String?)
    fun passwordAgainChanged(password: String?)
    fun signUp()
}