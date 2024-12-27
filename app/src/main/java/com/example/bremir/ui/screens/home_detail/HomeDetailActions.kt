package com.example.bremir.ui.screens.home_detail

import com.example.bremir.model.FoodItem
import com.example.bremir.model.ShopItem

interface HomeDetailActions {
    fun shopItemNameChanged(shopItemName: String?)
    fun shopItemNoteChanged(shopItemNote: String?)
    fun addShopItem()
    fun editShopItem(shopItemName: String?, shopItemNote: String?, index: Int)
    fun removeShopItem(shopItem: ShopItem?)

    fun foodItemNameChanged(foodItemName: String?)
    fun foodItemNoteChanged(foodItemNote: String?)
    fun addFoodItem()
    fun editFoodItem(foodItemName: String?, foodItemNote: String?, index: Int)
    fun removeFoodItem(foodItem: FoodItem?)

    fun loadHome(id: String)
    fun saveHomeDetails()
}