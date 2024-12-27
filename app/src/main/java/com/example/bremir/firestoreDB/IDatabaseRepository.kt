package com.example.bremir.firestoreDB

import com.example.bremir.auth.AuthOperationResult
import com.example.bremir.model.FoodItem
import com.example.bremir.model.Home
import com.example.bremir.model.ShopItem

interface IDatabaseRepository {
    suspend fun getHomesByUser(uid: String): AuthOperationResult<List<Home>>
    suspend fun getHome(id: String): AuthOperationResult<Home>
    suspend fun createHome(home: Home): AuthOperationResult<Unit>
    suspend fun updateHome(home: Home): AuthOperationResult<Unit>
    suspend fun deleteHome(home: Home): AuthOperationResult<Unit>
}