package com.example.bremir.model

data class Home(var name: String){
    var id: String = ""
    var owner: String = ""
    var members: MutableList<String?> = mutableListOf()
    var foodItems: MutableList<FoodItem?> = mutableListOf()
    var shopItems: MutableList<ShopItem?> = mutableListOf()
    var notes: String = ""
}
