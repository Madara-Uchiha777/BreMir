package com.example.bremir.ui.screens.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bremir.R
import com.example.bremir.auth.AuthOperationResult
import com.example.bremir.auth.IAuthRepository
import com.example.bremir.datastore.DataStoreConstants
import com.example.bremir.datastore.IDataStoreRepository
import com.example.bremir.ui.screens.login.LoginUIState
import com.example.bremir.ui.screens.password_recovery.PasswordRecoveryUIState
import com.example.bremir.utils.StringResourcesProvider
import com.google.firebase.auth.ActionCodeSettings
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
class SignUpViewModel @Inject constructor(
    private val dataStore: IDataStoreRepository,
    private val stringResourcesProvider: StringResourcesProvider,
    private val authRepository: IAuthRepository

) : ViewModel(), SignUpActions {

    private var data: SignUpScreenData = SignUpScreenData()

    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)
    private val mustBeSixCharsError: String = stringResourcesProvider.getString(R.string.must_be_six_chars)
    private val signUpError: String = stringResourcesProvider.getString(R.string.something_went_wrong)

    private val _signUpUIState: MutableStateFlow<SignUpUIState> =
        MutableStateFlow(SignUpUIState.Loading())

    val signUpUIState = _signUpUIState.asStateFlow()

    fun load(){
        _signUpUIState.update {
            SignUpUIState.Success()
        }
    }

    override fun emailChanged(email: String?) {
        data.emailError = if (email == null) textError else null
        data.user.email = email ?: ""
        _signUpUIState.update {
            SignUpUIState.ScreenDataChanged(data)
        }
    }

    override fun passwordChanged(password: String?) {
        data.passwordError = if (password == null) textError else null
        data.user.password = password ?: ""
        _signUpUIState.update {
            SignUpUIState.ScreenDataChanged(data)
        }
    }

    override fun passwordAgainChanged(password: String?) {
        data.passwordAgainError = if (password == null) textError else null
        data.passwordAgain = password ?: ""
        _signUpUIState.update {
            SignUpUIState.ScreenDataChanged(data)
        }
    }


    override fun signUp() {
        if (data.user.email.isNotEmpty() && data.user.password.isNotEmpty() &&
            data.user.password == data.passwordAgain && data.passwordAgain.isNotEmpty()
        ) {
            if (data.user.password.length<6){
                data.passwordError = mustBeSixCharsError
                _signUpUIState.update {
                    SignUpUIState.ScreenDataChanged(data)
                }
            }
            else if (!isEmailUsed(data.user.email)) {
                viewModelScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        authRepository.register(
                            data.user.email,
                            data.user.email.split("@").first(),
                            data.user.password
                        )
                    }
                    when (result) {
                        is AuthOperationResult.Error -> {
                            data.emailError = signUpError
                            data.passwordError = signUpError
                            _signUpUIState.update {
                                SignUpUIState.ScreenDataChanged(data)
                            }
                        }

                        is AuthOperationResult.Loading -> {

                        }

                        is AuthOperationResult.Success -> {
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            dataStore.putString(DataStoreConstants.EMAIL_KEY.name, data.user.email)
                            dataStore.putString(
                                DataStoreConstants.PASSWORD_KEY.name,
                                data.user.password
                            )
                            dataStore.putString(DataStoreConstants.USER_KEY.name, uid)
                            _signUpUIState.update {
                                SignUpUIState.SignedUp()
                            }
                        }
                    }
                }
            }
        }
       else{
            if (data.user.email.isEmpty()){
                data.emailError = textError
            }
            if (data.user.password.isEmpty()){
                data.passwordError = textError
            }
            if (data.passwordAgain.isEmpty()){
                data.passwordAgainError = textError
            }
            else if (data.user.password != data.passwordAgain){
                data.passwordAgainError = stringResourcesProvider.getString(R.string.passwords_must_match)
            }

            _signUpUIState.update {
                SignUpUIState.ScreenDataChanged(data)
            }
        }
    }

    private fun isEmailUsed(email: String): Boolean {
        var res = false
        viewModelScope.launch {
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        // Pokud seznam není prázdný, e-mail je již použit
                        res = !signInMethods.isNullOrEmpty()
                        data.emailError = stringResourcesProvider.getString(R.string.email_is_already_used)
                        _signUpUIState.update {
                            SignUpUIState.ScreenDataChanged(data)
                        }
                    } else {
                        res = false
                    }
                }
        }
        return res
    }

    fun returnBack(){
        _signUpUIState.update {
            SignUpUIState.ReturnBack()
        }
    }

}