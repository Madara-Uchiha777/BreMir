package com.example.bremir.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bremir.R
import com.example.bremir.auth.AuthOperationResult
import com.example.bremir.auth.IAuthRepository
import com.example.bremir.datastore.DataStoreConstants
import com.example.bremir.datastore.IDataStoreRepository
import com.example.bremir.utils.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStore: IDataStoreRepository,
    private val stringResourcesProvider: StringResourcesProvider,
    private val authRepository: IAuthRepository

) : ViewModel(), LoginActions {

    private var data: LoginScreenData = LoginScreenData()

    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)
    private val loginError: String = stringResourcesProvider.getString(R.string.failed_to_log_in)

    private val _loginUIState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState.Loading())

    val loginUIState = _loginUIState.asStateFlow()



    fun load(){
        _loginUIState.update {
            LoginUIState.Success()
        }
    }
    fun isLogged(){

        viewModelScope.launch {

            if (!dataStore.getString(DataStoreConstants.EMAIL_KEY.name).isNullOrEmpty()){
                _loginUIState.update {
                    LoginUIState.Logged()
                }
            }
            else{
                load()
            }
        }
    }

    override fun emailChanged(email: String?) {
        data.emailError = if (email == null) textError else null
        data.user.email = email?:""
        _loginUIState.update {
            LoginUIState.ScreenDataChanged(data)
        }
    }

    override fun passwordChanged(password: String?) {
        data.passwordError = if (password == null) textError else null
        data.user.password = password?:""
        _loginUIState.update {
            LoginUIState.ScreenDataChanged(data)
        }
    }

    override fun login() {
        if (data.user.email.isNotEmpty() && data.user.password.isNotEmpty()){
            viewModelScope.launch {
                val result = withContext(Dispatchers.IO) {
                    authRepository.login(data.user.email, data.user.password)
                }
                when(result){
                    is AuthOperationResult.Error -> {
                        data.emailError = loginError
                        data.passwordError = loginError
                        _loginUIState.update {
                            LoginUIState.ScreenDataChanged(data)
                        }
                    }
                    is AuthOperationResult.Loading -> {

                    }
                    is AuthOperationResult.Success -> {
                        val uid = FirebaseAuth.getInstance().currentUser!!.uid
                        dataStore.putString(DataStoreConstants.EMAIL_KEY.name,data.user.email)
                        dataStore.putString(DataStoreConstants.PASSWORD_KEY.name,data.user.password)
                        dataStore.putString(DataStoreConstants.USER_KEY.name, uid)
                        _loginUIState.update {
                            LoginUIState.Logged()
                        }
                    }
                }
            }
        } else {
            if (data.user.email.isEmpty()){
                data.emailError = textError
            }
            if (data.user.password.isEmpty()){
                data.passwordError = textError
            }

            _loginUIState.update {
                LoginUIState.ScreenDataChanged(data)
            }
        }
    }

    private fun isEmailUsed(email: String){
        viewModelScope.launch {
            /*repository.isEmailUsed(email).collect {
                if (!it) {
                    data.emailError = stringResourcesProvider.getString(R.string.wrong_email)
                    _loginUIState.update {
                        LoginUIState.ScreenDataChanged(data)
                    }
                }
            }*/
        }
    }

    private fun isPasswordCorrect(email: String, password: String){
        viewModelScope.launch {
            /*repository.isPasswordCorrect(email, password).collect{
                if (!it){
                    data.passwordError = stringResourcesProvider.getString(R.string.wrong_password)
                    _loginUIState.update {
                        LoginUIState.ScreenDataChanged(data)
                    }
                }
                else{
                    if (!correctCredentials){
                        correctCredentials = true
                        login()
                    }

                }
            }*/
        }
    }

    override fun forgotPassword() {
        _loginUIState.update {
            LoginUIState.NavigateToPasswordRecovery()
        }
    }

    override fun signUp() {
        _loginUIState.update {
            LoginUIState.NavigateToSignUp()
        }
    }

}