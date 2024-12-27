package com.example.bremir.ui.screens.home_detail

import com.example.bremir.model.Home
import com.example.bremir.ui.screens.add_edit_home.AddEditHomeScreenData
import com.example.bremir.ui.screens.add_edit_home.AddEditHomeUIState
import com.example.bremir.ui.screens.main.MainScreenUIState

sealed class HomeDetailUIState {
    object Loading : HomeDetailUIState()
    class Error(val error: String) : HomeDetailUIState()
    class ScreenDataChanged(val data: HomeDetailScreenData) : HomeDetailUIState()
    class ReturnBack() : HomeDetailUIState()
}