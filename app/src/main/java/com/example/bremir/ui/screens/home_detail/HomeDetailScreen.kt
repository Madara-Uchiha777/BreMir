package com.example.bremir.ui.screens.home_detail

import android.util.Log
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bremir.model.FoodItem
import com.example.bremir.model.ShopItem
import com.example.bremir.navigation.INavigationRouter
import com.example.bremir.ui.elements.BaseScreen
import com.example.bremir.ui.elements.PlaceholderScreenContent
import com.example.bremir.ui.theme.basicMargin
import kotlinx.coroutines.launch

@Composable
fun HomeDetailScreen(
    navigation: INavigationRouter,
    id: String
){
    val viewModel = hiltViewModel<HomeDetailViewModel>()

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(HomeDetailScreenData())
    }

    state.value.let {
        when(it){
            is HomeDetailUIState.Error -> {

            }
            is HomeDetailUIState.Loading -> {
                viewModel.loadHome(id)
            }
            is HomeDetailUIState.ReturnBack -> {
                LaunchedEffect(it) {
                    navigation.returnBack()
                }
            }
            is HomeDetailUIState.ScreenDataChanged -> {
                data = it.data
            }
        }
    }

    BaseScreen(
        topBarText = data.home.name,
        showLoading = state.value is HomeDetailUIState.Loading,
        placeholderScreenContent = if (state.value is HomeDetailUIState.Error) {
            PlaceholderScreenContent(
                image = null,
                title = null,
                text = (state.value as HomeDetailUIState.Error).error,
                buttonText = "Aktualizovat",
                onButtonClick = { viewModel.loadHome(id) }
            )
        } else null,
        onBackClick = {
            viewModel.returnBack()
        },
        actions = {
            if (state.value is HomeDetailUIState.ScreenDataChanged){
                IconButton(
                    onClick = {
                        viewModel.saveHomeDetails()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "")
                }
            }
        }
    ) {
        HomeDetailScreenContent(paddingValues = it, viewModel = viewModel, data = data)
    }
}

@Composable
fun HomeDetailScreenContent(
    paddingValues: PaddingValues,
    viewModel: HomeDetailViewModel,
    data: HomeDetailScreenData
){
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = basicMargin())
            .verticalScroll(scrollState)
            .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Bottom ))
    ) {
        ItemCard(
            title = "Jídelníček",
            items = data.home.foodItems,
            name = data.foodItem.name,
            note = data.foodItem.note,
            onNameChange = { viewModel.foodItemNameChanged(it) },
            onNoteChange = { viewModel.foodItemNoteChanged(it) },
            onAdd = { viewModel.addFoodItem() },
            onEdit = { name, note, index -> viewModel.editFoodItem(name, note, index) },
            onRemove = { item -> viewModel.removeFoodItem(item as FoodItem) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ItemCard(
            title = "Nákupní seznam",
            items = data.home.shopItems,
            name = data.shopItem.name,
            note = data.shopItem.note,
            onNameChange = { viewModel.shopItemNameChanged(it) },
            onNoteChange = { viewModel.shopItemNoteChanged(it) },
            onAdd = { viewModel.addShopItem() },
            onEdit = { name, note, index -> viewModel.editShopItem(name, note, index) },
            onRemove = { item -> viewModel.removeShopItem(item as ShopItem) }
        )
    }
}

@Composable
fun <T> ItemCard(
    title: String,
    items: List<T>,
    name: String?,
    note: String?,
    onNameChange: (String?) -> Unit,
    onNoteChange: (String?) -> Unit,
    onAdd: () -> Unit,
    onEdit: (String?, String?, Int) -> Unit,
    onRemove: (T) -> Unit
) {
    var editIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name ?: "",
                onValueChange = onNameChange,
                label = { Text("Název položky") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note ?: "",
                onValueChange = onNoteChange,
                label = { Text("Poznámka") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (editIndex == null) {
                        onAdd()
                    } else {
                        onEdit(name, note, editIndex!!)
                        editIndex = null
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (editIndex == null) "Přidat" else "Uložit změny")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 1_000.dp)) {
                itemsIndexed(items){ index, item ->
                    Log.d("item", item.toString())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Název: ${(item as? ShopItem)?.name ?: (item as? FoodItem)?.name}")
                            Text("Poznámka: ${(item as? ShopItem)?.note ?: (item as? FoodItem)?.note}")
                        }

                        Row {
                            IconButton(onClick = {
                                onNameChange((item as? ShopItem)?.name ?: (item as? FoodItem)?.name)
                                onNoteChange((item as? ShopItem)?.note ?: (item as? FoodItem)?.note)
                                editIndex = index
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editovat")
                            }
                            IconButton(onClick = { onRemove(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Smazat")
                            }
                        }
                    }
                }
            }
        }
    }
}