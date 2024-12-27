package com.example.bremir.ui.screens.home_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bremir.R
import com.example.bremir.auth.AuthOperationResult
import com.example.bremir.firestoreDB.IDatabaseRepository
import com.example.bremir.model.FoodItem
import com.example.bremir.model.ShopItem
import com.example.bremir.ui.screens.add_edit_home.AddEditHomeUIState
import com.example.bremir.ui.screens.main.MainScreenUIState
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
class HomeDetailViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val stringResourcesProvider: StringResourcesProvider
) : ViewModel(), HomeDetailActions{

    private var data: HomeDetailScreenData = HomeDetailScreenData()

    private val error: String = stringResourcesProvider.getString(R.string.something_went_wrong)

    private val _uiState: MutableStateFlow<HomeDetailUIState> =
        MutableStateFlow(value = HomeDetailUIState.Loading)

    val uiState: StateFlow<HomeDetailUIState> get() = _uiState.asStateFlow()

    override fun shopItemNameChanged(shopItemName: String?) {
        data.shopItem.name = shopItemName ?: ""
        _uiState.update {
            HomeDetailUIState.ScreenDataChanged(data)
        }
    }

    override fun shopItemNoteChanged(shopItemNote: String?) {
        data.shopItem.note = shopItemNote ?: ""
        _uiState.update {
            HomeDetailUIState.ScreenDataChanged(data)
        }
    }

    override fun addShopItem() {
        if (data.shopItem.name.isNotEmpty() && data.shopItem !in data.home.shopItems) {
            data.home.shopItems.add(data.shopItem)
            data.shopItem = ShopItem("")
            _uiState.update {
                HomeDetailUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun editShopItem(shopItemName: String?, shopItemNote: String?, index: Int) {
        if (!shopItemName.isNullOrEmpty()){
            data.home.shopItems[index]?.name = shopItemName
            data.home.shopItems[index]?.note = shopItemNote ?: ""
            data.shopItem = ShopItem("")
            _uiState.update {
                HomeDetailUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun removeShopItem(shopItem: ShopItem?) {
        data.home.shopItems.remove(shopItem)
        _uiState.update {
            HomeDetailUIState.ScreenDataChanged(data)
        }
    }

    override fun foodItemNameChanged(foodItemName: String?) {
        data.foodItem.name = foodItemName ?: ""
        _uiState.update {
            HomeDetailUIState.ScreenDataChanged(data)
        }
    }

    override fun foodItemNoteChanged(foodItemNote: String?) {
        data.foodItem.note = foodItemNote ?: ""
        _uiState.update {
            HomeDetailUIState.ScreenDataChanged(data)
        }
    }

    override fun addFoodItem() {
        if (data.foodItem.name.isNotEmpty() && data.foodItem !in data.home.foodItems) {
            data.home.foodItems.add(data.foodItem)
            data.foodItem = FoodItem("")
            _uiState.update {
                HomeDetailUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun editFoodItem(foodItemName: String?, foodItemNote: String?, index: Int) {
        if (!foodItemName.isNullOrEmpty()){
            data.home.foodItems[index]?.name = foodItemName
            data.home.foodItems[index]?.note = foodItemNote ?: ""
            data.foodItem = FoodItem("")
            _uiState.update {
                HomeDetailUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun removeFoodItem(foodItem: FoodItem?) {
        data.home.foodItems.remove(foodItem)
        _uiState.update {
            HomeDetailUIState.ScreenDataChanged(data)
        }
    }

    override fun loadHome(id: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                databaseRepository.getHome(id)
            }
            when(result){
                is AuthOperationResult.Error -> {
                    Log.d("error home", result.message.toString())
                    _uiState.update {
                        HomeDetailUIState.Error(error)
                    }
                }
                is AuthOperationResult.Loading -> {
                    Log.d("loading home", "yes")
                }
                is AuthOperationResult.Success -> {
                    Log.d("homes fetch", result.data!!.foodItems.toString())
                    data.home = result.data!!.apply {
                        this.id = result.data.id
                        this.owner = result.data.owner
                        this.members = result.data.members
                        this.foodItems = result.data.foodItems
                        this.shopItems = result.data.shopItems
                        this.notes = result.data.notes
                    }
                    _uiState.update {
                        HomeDetailUIState.ScreenDataChanged(data)
                    }
                }
            }
        }
    }

    override fun saveHomeDetails() {
        viewModelScope.launch {
            databaseRepository.updateHome(data.home)
            _uiState.update {
                HomeDetailUIState.ReturnBack()
            }
        }
    }

    fun returnBack(){
        _uiState.update {
            HomeDetailUIState.ReturnBack()
        }
    }
}