package com.example.bremir.ui.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bremir.R
import com.example.bremir.model.Home
import com.example.bremir.navigation.INavigationRouter
import com.example.bremir.ui.elements.BaseScreen
import com.example.bremir.ui.elements.PlaceholderScreenContent
import com.example.bremir.ui.screens.home_detail.HomeDetailUIState
import com.example.bremir.ui.theme.basicMargin
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navigation: INavigationRouter){
    val viewModel = hiltViewModel<MainViewModel>()

    val pagerState = rememberPagerState(pageCount = { 2 })

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(MainScreenData())
    }

    LaunchedEffect(Unit) {
        viewModel.fetchHomesByUser()
    }


    state.value.let {
        when (it) {
            is MainScreenUIState.Error -> {

            }

            is MainScreenUIState.Loading -> {
                viewModel.fetchHomesByUser()
            }

            is MainScreenUIState.ScreenDataChanged -> {
                data = it.data
            }

            is MainScreenUIState.LoggedOut -> {
                LaunchedEffect(it) {
                    navigation.navigateToLogin()
                }
            }
        }
    }

    BaseScreen(
        topBarText = stringResource(R.string.list_of_homes),
        actions = {
            IconButton(onClick = {
                viewModel.logOut()
            }) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "")
            }
        },
        showLoading = state.value is MainScreenUIState.Loading,
        placeholderScreenContent = if (state.value is MainScreenUIState.Error) {
            PlaceholderScreenContent(
                image = null,
                title = null,
                text = (state.value as HomeDetailUIState.Error).error,
                buttonText = "Aktualizovat",
                onButtonClick = { viewModel.fetchHomesByUser() }
            )
        } else null,
        floatingActionButton = {
            if (pagerState.currentPage == 1) FloatingActionButton(onClick = {
                navigation.navigateToAddEditHomeScreen(null)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    ) {

        ListOfHomesScreenContent(
            paddingValues = it,
            navigation = navigation,
            pagerState = pagerState,
            data = data,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListOfHomesScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    pagerState: PagerState,
    data: MainScreenData,
    viewModel: MainViewModel
){
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
        Tabs(pagerState = pagerState)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch{
                    isRefreshing = true
                    viewModel.fetchHomesByUser()
                    delay(200)
                    isRefreshing = false

                }
            },
            modifier = Modifier.fillMaxSize()
        ){
            TabsContent(
                pagerState = pagerState,
                navigation = navigation,
                data = data,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Tabs(pagerState: PagerState) {
    val list = listOf(
        stringResource(R.string.summary),
        stringResource(R.string.homes)
    )

    val scope = rememberCoroutineScope()

    PrimaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Text(
                        list[index],
                        color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize // font size by documentation
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@Composable
fun TabsContent(
    pagerState: PagerState,
    navigation: INavigationRouter,
    data: MainScreenData,
    viewModel: MainViewModel
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> TabContentScreenSummary(data = data, viewModel = viewModel)
            1 -> TabContentScreenList(homes = data.homes, navigation = navigation)
        }
    }
}

@Composable
fun TabContentScreenSummary(
    data: MainScreenData,
    viewModel: MainViewModel
){
    val homes = data.homes
    val scrollState = rememberScrollState()

    if (homes.isEmpty()) {
        Column(
            modifier = Modifier
                .padding(horizontal = basicMargin())
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.no_homes),
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                modifier = Modifier.padding(top = basicMargin())
            )
        }
    }
    else {
        Column(
            modifier = Modifier
                .padding(horizontal = basicMargin())
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            if ((homes.maxOfOrNull { it.foodItems.size } ?: 0) == 0 && (homes.maxOfOrNull { it.shopItems.size } ?: 0) == 0){
                Row {
                    Text(
                        text = "Žádná data k zobrazení",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            if ((homes.maxOfOrNull { it.foodItems.size } ?: 0) > 0){
                Row {
                    Text(
                        text = "Jídelníčky",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .weight(1f)
                    )
                    IconButton(onClick = {
                        viewModel.removeCompletedFoodItems()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete, contentDescription = ""
                        )
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = basicMargin()),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 1_000.dp)) {
                    val maxItems = homes.maxOfOrNull { it.foodItems.size } ?: 0

                    // Kombinování položek podle indexu
                    val combinedItems = (0 until maxItems).flatMap { index ->
                        homes.mapNotNull { home ->
                            home.foodItems.getOrNull(index)?.let { home to it }
                        }
                    }

                    // Seřazení podle stavu dokončení
                    val sortedFoodItems = combinedItems.sortedBy { (_, item) -> item.completed }

                    items(sortedFoodItems) { (home, item) ->
                        ListItem(
                            headlineContent = { Text(text = item.name) },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = home.name.substring(0, 2).toUpperCase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            },
                            supportingContent = { if (item.note.isNotEmpty()) Text(text = item.note) else null },
                            trailingContent = {
                                Checkbox(
                                    checked = item.completed,
                                    onCheckedChange = {
                                        viewModel.homeFoodItemChecked(home, item)
                                    }
                                )
                            }
                        )
                    }
                }
            }

            if ((homes.maxOfOrNull { it.shopItems.size } ?: 0) > 0){
                Row {
                    Text(
                        text = "Nákupní seznam",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .weight(1f)
                    )
                    IconButton(onClick = {
                        viewModel.removeCompletedShopItems()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete, contentDescription = ""
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 1_000.dp)) {
                    val sortedShopItems = homes.flatMap { home -> home.shopItems.map { home to it } }
                        .sortedBy { (_, item) -> item!!.completed }

                    items(sortedShopItems) { (home, item) ->
                        ListItem(
                            headlineContent = { Text(text = item?.name ?: "") },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = home.name.substring(0, 2).toUpperCase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            },
                            supportingContent = { if (item!!.note.isNotEmpty()) Text(text = item.note) else null },
                            trailingContent = {
                                Checkbox(
                                    checked = item!!.completed,
                                    onCheckedChange = {
                                        viewModel.homeShopItemChecked(home, item)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun TabContentScreenList(
    homes: List<Home>,
    navigation: INavigationRouter
) {
    if (homes.isEmpty()) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(horizontal = basicMargin())
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.no_homes),
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                modifier = Modifier.padding(top = basicMargin())
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            homes.forEachIndexed { index, home ->
                item {
                    val letter = home.name.first()
                    HomeListRow(
                        home = home, userLetter = letter,
                        onClick = {
                            navigation.navigateToHomeDetailScreen(home.id)
                        },
                        onActionClick = {
                            navigation.navigateToAddEditHomeScreen(home.id)
                        }
                    )
                    if (index < homes.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeListRow(home: Home, userLetter: Char, onClick: () -> Unit, onActionClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userLetter.toUpperCase().toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        trailingContent = {
            if (home.owner == FirebaseAuth.getInstance().currentUser!!.uid){
                IconButton(onClick = {
                    onActionClick()
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                }
            }else null
        },
        headlineContent = { Text(text = home.name) }
    )
}