package com.example.bremir.firestoreDB

import android.util.Log
import com.example.bremir.auth.AuthOperationResult
import com.example.bremir.auth.safeCall
import com.example.bremir.model.FoodItem
import com.example.bremir.model.Home
import com.example.bremir.model.ShopItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.reflect.typeOf

class DatabaseRepositoryImpl : IDatabaseRepository {
    private val homesRef = FirebaseFirestore.getInstance().collection("homes")

    override suspend fun getHomesByUser(uid: String): AuthOperationResult<List<Home>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val snapshot = homesRef.whereArrayContains("members", uid).get().await()
                val homes = snapshot.documents.mapNotNull { document ->
                    val home = Home(
                        name = document.getString("name") ?: ""
                    ).apply {
                        this.id = document.id
                        this.owner = document.getString("owner") ?: ""
                        this.members = document.get("members") as? MutableList<String?> ?: mutableListOf()

                        // Manuální deserializace foodItems
                        val foodItemsList = document.get("foodItems") as? List<Map<String, Any>>
                        this.foodItems = foodItemsList?.map {
                            FoodItem(
                                name = it["name"] as? String ?: "",
                            ).apply {
                                note = it["note"] as? String ?: ""
                                completed = it["completed"] as? Boolean ?: false
                            }
                        }?.toMutableList() ?: mutableListOf()

                        // Manuální deserializace shopItems
                        val shopItemsList = document.get("shopItems") as? List<Map<String, Any>>
                        this.shopItems = shopItemsList?.map {
                            ShopItem(
                                name = it["name"] as? String ?: "",
                            ).apply {
                                note = it["note"] as? String ?: ""
                                completed = it["completed"] as? Boolean ?: false
                            }
                        }?.toMutableList() ?: mutableListOf()

                        this.notes = document.getString("notes") ?: ""
                    }
                    home
                }
                AuthOperationResult.Success(homes)
            }
        }
    }

    override suspend fun getHome(id: String): AuthOperationResult<Home> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val document = homesRef.document(id).get().await()
                if (document.exists()) {
                    val home = Home(
                        name = document.getString("name") ?: ""
                    ).apply {
                        this.id = document.id
                        this.owner = document.getString("owner") ?: ""
                        this.members = document.get("members") as? MutableList<String?> ?: mutableListOf()

                        val foodItemsList = document.get("foodItems") as? List<Map<String, Any>>
                        this.foodItems = foodItemsList?.map {
                            FoodItem(
                                name = it["name"] as? String ?: "",
                            ).apply {
                                note = it["note"] as? String ?: ""
                                completed = it["completed"] as? Boolean ?: false
                            }
                        }?.toMutableList() ?: mutableListOf()

                        val shopItemsList = document.get("shopItems") as? List<Map<String, Any>>
                        this.shopItems = shopItemsList?.map {
                            ShopItem(
                                name = it["name"] as? String ?: "",
                            ).apply {
                                note = it["note"] as? String ?: ""
                                completed = it["completed"] as? Boolean ?: false
                            }
                        }?.toMutableList() ?: mutableListOf()

                        this.notes = document.getString("notes") ?: ""
                    }
                    AuthOperationResult.Success(home)
                } else {
                    AuthOperationResult.Error("Domov nenalezen")
                }
            }
        }
    }



    override suspend fun createHome(home: Home): AuthOperationResult<Unit> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val docRef = homesRef.document()
                home.id = docRef.id
                docRef.set(home).await()
                AuthOperationResult.Success(Unit)
            }
        }
    }

    override suspend fun updateHome(home: Home): AuthOperationResult<Unit> {
        return withContext(Dispatchers.IO) {
            safeCall {
                homesRef.document(home.id).set(home).await()
                AuthOperationResult.Success(Unit)
            }
        }
    }

    override suspend fun deleteHome(home: Home): AuthOperationResult<Unit> {
        return withContext(Dispatchers.IO) {
            safeCall {
                homesRef.document(home.id).delete().await()
                AuthOperationResult.Success(Unit)
            }
        }
    }
}
