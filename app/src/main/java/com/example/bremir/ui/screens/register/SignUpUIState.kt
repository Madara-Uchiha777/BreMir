package com.example.bremir.ui.screens.register

sealed class SignUpUIState {
    class Loading : SignUpUIState()
    class Success : SignUpUIState()
    class SignedUp : SignUpUIState()
    class ScreenDataChanged(val data: SignUpScreenData) : SignUpUIState()
    class ReturnBack() : SignUpUIState()
}