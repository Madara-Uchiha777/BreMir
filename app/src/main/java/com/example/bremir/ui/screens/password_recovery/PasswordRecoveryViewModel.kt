package com.example.bremir.ui.screens.password_recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bremir.R
import com.example.bremir.utils.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val stringResourcesProvider: StringResourcesProvider

) : ViewModel(), PasswordRecoveryActions {

    private var data: PasswordRecoveryScreenData = PasswordRecoveryScreenData()

    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)

    private val _passwordRecoveryUIState: MutableStateFlow<PasswordRecoveryUIState> =
        MutableStateFlow(PasswordRecoveryUIState.Loading())

    val passwordRecoveryUIState = _passwordRecoveryUIState.asStateFlow()


    override fun emailChanged(email: String?) {
        data.emailError = if (email == null) textError else null
        data.email = email ?: ""
        _passwordRecoveryUIState.update {
            PasswordRecoveryUIState.ScreenDataChanged(data)
        }
    }

    override fun recoverPassword() {
        if (data.email.isNotEmpty()){
            if (!isEmailUsed(data.email)){
                viewModelScope.launch {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(data.email) //TODO POSLAT HESLO
                }
            }

        }
        else{
            if (data.email.isEmpty()){
                data.emailError = textError
                _passwordRecoveryUIState.update {
                    PasswordRecoveryUIState.ScreenDataChanged(data)
                }
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
                    } else {
                        res = false
                        data.emailError = stringResourcesProvider.getString(R.string.invalid_email)
                        _passwordRecoveryUIState.update {
                            PasswordRecoveryUIState.ScreenDataChanged(data)
                        }
                    }
                }
        }
        return res
    }

    fun returnBack(){
        _passwordRecoveryUIState.update {
            PasswordRecoveryUIState.ReturnBack()
        }
    }

}