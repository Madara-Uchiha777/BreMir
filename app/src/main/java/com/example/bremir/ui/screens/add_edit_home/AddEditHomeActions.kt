package com.example.bremir.ui.screens.add_edit_home

import com.example.bremir.model.FoodItem
import com.example.bremir.model.ShopItem

interface AddEditHomeActions {
    fun homeNameChanged(name: String?)
    fun homeNotesChanged(notes: String?)
    fun memberMailChanged(mail: String?)
    fun addMember()
    fun removeMember(member: String?)

    fun loadHome(id: String?)
    fun saveHome()
    fun deleteHome()
}