package com.example.bremir.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bremir.R
import com.example.bremir.auth.AuthOperationResult
import com.example.bremir.datastore.IDataStoreRepository
import com.example.bremir.firestoreDB.IDatabaseRepository
import com.example.bremir.model.FoodItem
import com.example.bremir.model.Home
import com.example.bremir.model.ShopItem
import com.example.bremir.utils.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: IDataStoreRepository,
    private val databaseRepository: IDatabaseRepository,
    private val stringResourcesProvider: StringResourcesProvider
) : ViewModel(){
    private val _uiState: MutableStateFlow<MainScreenUIState> =
        MutableStateFlow(value = MainScreenUIState.Loading)
    val uiState: StateFlow<MainScreenUIState> get() = _uiState.asStateFlow()

    private val error: String = stringResourcesProvider.getString(R.string.something_went_wrong)

    private var data: MainScreenData = MainScreenData()

    init {
        fetchHomesByUser()
    }

    fun fetchHomesByUser() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                databaseRepository.getHomesByUser(FirebaseAuth.getInstance().currentUser!!.email!!)
            }
            when(result){
                is AuthOperationResult.Error -> {
                    Log.d("error homes by user", result.message.toString())
                    _uiState.update {
                        MainScreenUIState.Error(error)
                    }
                }
                is AuthOperationResult.Loading -> {
                    Log.d("loading homes by user", "yes")
                }
                is AuthOperationResult.Success -> {
                    Log.d("homes by user",result.data.toString())
                    data.homes = result.data?.reversed() ?: emptyList()
                    _uiState.update {
                        MainScreenUIState.ScreenDataChanged(data)
                    }
                }
            }
        }
    }

    fun homeFoodItemChecked(home: Home, item: FoodItem){
        var index = data.homes.indexOf(home)
        var itemIndex = data.homes[index].foodItems.indexOf(item)
        data.homes[index].foodItems[itemIndex]!!.completed = !data.homes[index].foodItems[itemIndex]!!.completed
        viewModelScope.launch {
            databaseRepository.updateHome(home)
        }
        _uiState.update {
            MainScreenUIState.ScreenDataChanged(data)
        }
    }

    fun homeShopItemChecked(home: Home, item: ShopItem){
        var index = data.homes.indexOf(home)
        var itemIndex = data.homes[index].shopItems.indexOf(item)
        data.homes[index].shopItems[itemIndex]!!.completed = !data.homes[index].shopItems[itemIndex]!!.completed
        viewModelScope.launch {
            databaseRepository.updateHome(home)
        }
        _uiState.update {
            MainScreenUIState.ScreenDataChanged(data)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            dataStore.clearAll()
            _uiState.update {
                MainScreenUIState.LoggedOut()
            }
        }
    }

    fun removeCompletedFoodItems() {
        data.homes.forEach { home ->
            home.foodItems.removeAll { it!!.completed }
            viewModelScope.launch {
                databaseRepository.updateHome(home)
            }
        }
        _uiState.update {
            MainScreenUIState.ScreenDataChanged(data)
        }
    }

    fun removeCompletedShopItems() {
        data.homes.forEach { home ->
            home.shopItems.removeAll { it!!.completed }
            viewModelScope.launch {
                databaseRepository.updateHome(home)
            }
        }
        _uiState.update {
            MainScreenUIState.ScreenDataChanged(data)
        }
    }
}