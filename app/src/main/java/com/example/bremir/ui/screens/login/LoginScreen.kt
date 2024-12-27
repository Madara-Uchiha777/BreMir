package com.example.bremir.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bremir.R
import com.example.bremir.navigation.INavigationRouter
import com.example.bremir.ui.theme.basicMargin

@Composable
fun LoginScreen(navigationRouter: INavigationRouter){
    val viewModel = hiltViewModel<LoginViewModel>()

    val state = viewModel.loginUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(LoginScreenData())
    }


    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        viewModel.isLogged()
    }

    state.value.let {
        when (it) {
            is LoginUIState.Loading -> {
                viewModel.isLogged()
            }

            is LoginUIState.Logged -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToMainScreen()
                }
            }

            is LoginUIState.NavigateToPasswordRecovery -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToPasswordRecovery()
                }
                viewModel.isLogged()
            }

            is LoginUIState.NavigateToSignUp -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToSignUp()
                }
                viewModel.isLogged()
            }

            is LoginUIState.ScreenDataChanged -> {
                data = it.data
            }

            is LoginUIState.Success -> {

            }
        }
    }

    Scaffold {
        LoginScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun LoginScreenContent(
    paddingValues: PaddingValues,
    viewModel: LoginViewModel,
    data: LoginScreenData
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = basicMargin())
    ) {
        Row(
            modifier = Modifier
                .height((LocalConfiguration.current.screenHeightDp/5).dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "BreMir",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
        ) {
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

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description)
                        }
                    }
                )

                Button(
                    onClick = {
                        viewModel.login()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.log_in))
                }

                /*
                TODO dodělat
                TextButton(
                    onClick = {
                        viewModel.forgotPassword()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.forgot_password))
                }*/
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
        ) {

            OutlinedButton(
                onClick = {
                    viewModel.signUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Bottom)
            ) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }

}