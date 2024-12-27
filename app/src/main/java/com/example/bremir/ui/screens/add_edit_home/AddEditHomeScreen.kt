package com.example.bremir.ui.screens.add_edit_home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bremir.R
import com.example.bremir.navigation.INavigationRouter
import com.example.bremir.ui.elements.BaseScreen
import com.example.bremir.ui.elements.PlaceholderScreenContent
import com.example.bremir.ui.screens.home_detail.HomeDetailUIState
import com.example.bremir.ui.screens.main.MainScreenUIState
import com.example.bremir.ui.theme.basicMargin
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddEditHomeScreen(
    navigation: INavigationRouter, id: String?
) {
    val viewModel = hiltViewModel<AddEditHomeViewModel>()

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(AddEditHomeScreenData())
    }

    state.value.let {
        when(it){
            is AddEditHomeUIState.HomeDeleted -> {
                LaunchedEffect(it) {
                    navigation.returnBack()
                }
            }

            AddEditHomeUIState.Loading -> {
                viewModel.loadHome(id)
            }

            is AddEditHomeUIState.ReturnBack -> {
                LaunchedEffect(it) {
                    navigation.returnBack()
                }
            }

            is AddEditHomeUIState.ScreenDataChanged -> {
                data = it.data
            }

            is AddEditHomeUIState.Error -> {

            }
        }
    }

    BaseScreen(
        topBarText = if (id == null) "Nová domácnost" else data.home.name,
        onBackClick = {
            viewModel.returnBack()
        },
        placeholderScreenContent = if (state.value is AddEditHomeUIState.Error) {
            PlaceholderScreenContent(
                image = null,
                title = null,
                text = (state.value as HomeDetailUIState.Error).error,
                buttonText = "Aktualizovat",
                onButtonClick = { viewModel.loadHome(id) }
            )
        } else null,
        actions = {
            if (id != null){
                if (state.value is AddEditHomeUIState.ScreenDataChanged){
                    IconButton(
                        onClick = {
                            viewModel.deleteHome()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                    }
                }
            }
        }
    ) {
        AddEditHomeScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun AddEditHomeScreenContent(
    paddingValues: PaddingValues,
    viewModel: AddEditHomeViewModel,
    data: AddEditHomeScreenData
){
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = basicMargin())
            .verticalScroll(scrollState)

    ) {
        OutlinedTextField(
            value = data.home.name,
            onValueChange = { viewModel.homeNameChanged(it) },
            label = { Text("Název domácnosti") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            supportingText = {
                if (data.homeNameError != null){
                    Text(text = data.homeNameError!!)
                }
            },
            isError = data.homeNameError != null
        )

        Text(text = "Přidat členy", style = MaterialTheme.typography.titleMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = data.memberMail,
                onValueChange = { viewModel.memberMailChanged(it) },
                label = { Text("E-mail člena") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                viewModel.addMember()
            }) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Přidat člena"
                )
            }
        }

        data.home.members.forEach { member ->
            if (!member.isNullOrEmpty()){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = member, modifier = Modifier.weight(1f)
                    )
                    if (member != FirebaseAuth.getInstance().currentUser!!.email)
                    IconButton(onClick = { viewModel.removeMember(member) }) {
                        Icon(
                            imageVector = Icons.Default.Delete, contentDescription = "Odebrat člena"
                        )
                    }
                }
            }
        }

        OutlinedTextField(value = data.home.notes,
            onValueChange = { viewModel.homeNotesChanged(it) },
            label = { Text("Poznámka") },
            modifier = Modifier.fillMaxWidth().padding(vertical = basicMargin()),
            singleLine = false
        )

        Button(
            onClick = {
                viewModel.saveHome()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Potvrdit")
        }
    }
}