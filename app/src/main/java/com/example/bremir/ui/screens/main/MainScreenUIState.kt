package com.example.bremir.ui.screens.main

sealed class MainScreenUIState {
    object Loading : MainScreenUIState()
    class Error(val error: String) : MainScreenUIState()
    class ScreenDataChanged(val data: MainScreenData) : MainScreenUIState()
    class LoggedOut : MainScreenUIState()
}