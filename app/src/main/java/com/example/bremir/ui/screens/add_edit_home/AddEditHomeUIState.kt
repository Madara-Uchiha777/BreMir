package com.example.bremir.ui.screens.add_edit_home

import com.example.bremir.ui.screens.main.MainScreenUIState

sealed class AddEditHomeUIState {
    object Loading : AddEditHomeUIState()
    class Error(val error: String) : AddEditHomeUIState()
    class HomeDeleted : AddEditHomeUIState()
    class ScreenDataChanged(val data: AddEditHomeScreenData) : AddEditHomeUIState()
    class ReturnBack() : AddEditHomeUIState()
}