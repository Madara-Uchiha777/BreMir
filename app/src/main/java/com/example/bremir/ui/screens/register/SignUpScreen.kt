package com.example.bremir.ui.screens.register

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bremir.R
import com.example.bremir.ui.theme.basicMargin
import com.example.bremir.navigation.INavigationRouter
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navigationRouter: INavigationRouter){
    val viewModel = hiltViewModel<SignUpViewModel>()

    val state = viewModel.signUpUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(SignUpScreenData())
    }

    state.value.let {
        when(it){
            is SignUpUIState.Loading -> {

            }
            is SignUpUIState.ReturnBack -> {
                LaunchedEffect(it){
                    navigationRouter.returnBack()
                }
            }
            is SignUpUIState.ScreenDataChanged -> {
                data = it.data
            }
            is SignUpUIState.SignedUp -> {
                LaunchedEffect(it){
                    navigationRouter.navigateToMainScreen()
                }
            }

            is SignUpUIState.Success -> {

            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.registration), modifier = Modifier.testTag("registration_title"))
            },
            navigationIcon = {
                IconButton(onClick = {
                    viewModel.returnBack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }
        )
    },
        modifier = Modifier.imePadding()
    ) {
        SignUpScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun SignUpScreenContent(
    paddingValues: PaddingValues,
    viewModel: SignUpViewModel,
    data: SignUpScreenData
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordAgainVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = basicMargin())
            .verticalScroll(scrollState)
            .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Top))
    ) {

        /*Row(
            modifier = Modifier
                .height((LocalConfiguration.current.screenHeightDp/5).dp)
        ) {
            // drawable logo to ImageVector (we don't need recomposition on logo)
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }*/

        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = {
                    Text(text = "E-mail")
                },
                value = data.user.email,
                onValueChange = {
                    viewModel.emailChanged(it)
                },
                supportingText = {
                    if (data.emailError != null) {
                        Text(text = data.emailError!!)
                    }
                },
                isError = data.emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = {
                    Text(text = stringResource(R.string.password))
                },
                value = data.user.password,
                onValueChange = {
                    viewModel.passwordChanged(it)
                },
                supportingText = {
                    if (data.passwordError != null) {
                        Text(text = data.passwordError!!)
                    }
                },
                isError = data.passwordError != null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    // Localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    // Toggle button to hide or display password
                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, description)
                    }
                }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = {
                    Text(text = stringResource(R.string.password_again))
                },
                value = data.passwordAgain,
                onValueChange = {
                    viewModel.passwordAgainChanged(it)
                },
                supportingText = {
                    if (data.passwordAgainError != null) {
                        Text(text = data.passwordAgainError!!)
                    }
                },
                isError = data.passwordAgainError != null,
                visualTransformation = if (passwordAgainVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordAgainVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    val description = if (passwordAgainVisible) "Hide password" else "Show password"

                    // Toggle button to hide or display password
                    IconButton(onClick = {passwordAgainVisible = !passwordAgainVisible}){
                        Icon(imageVector  = image, description)
                    }
                }
            )

            Button(
                onClick = {
                    viewModel.signUp()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }

}