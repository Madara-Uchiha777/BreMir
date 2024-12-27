package com.example.bremir.ui.screens.add_edit_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bremir.R
import com.example.bremir.auth.AuthOperationResult
import com.example.bremir.firestoreDB.IDatabaseRepository
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
class AddEditHomeViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val stringResourcesProvider: StringResourcesProvider
) : ViewModel(), AddEditHomeActions {

    private var data: AddEditHomeScreenData = AddEditHomeScreenData()

    private val error: String = stringResourcesProvider.getString(R.string.something_went_wrong)
    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)

    private val _uiState: MutableStateFlow<AddEditHomeUIState> =
        MutableStateFlow(value = AddEditHomeUIState.Loading)

    val uiState: StateFlow<AddEditHomeUIState> get() = _uiState.asStateFlow()

    override fun homeNameChanged(name: String?) {
        data.home.name = name ?: ""
        if (data.home.name.isNotEmpty())data.homeNameError = null
        _uiState.update {
            AddEditHomeUIState.ScreenDataChanged(data)
        }
    }

    override fun homeNotesChanged(notes: String?) {
        data.home.notes = notes ?: ""
        _uiState.update {
            AddEditHomeUIState.ScreenDataChanged(data)
        }
    }

    override fun memberMailChanged(mail: String?) {
        data.memberMail = mail ?: ""
        _uiState.update {
            AddEditHomeUIState.ScreenDataChanged(data)
        }
    }

    override fun addMember() {
        if (data.memberMail.isNotEmpty() && data.memberMail !in data.home.members) {
            data.home.members.add(data.memberMail.trim())
            data.memberMail = ""
            _uiState.update {
                AddEditHomeUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun removeMember(member: String?) {
        data.home.members.remove(member)
        _uiState.update {
            AddEditHomeUIState.ScreenDataChanged(data)
        }
    }

    override fun loadHome(id: String?) {
        if (id != null){
            viewModelScope.launch {
                val result = withContext(Dispatchers.IO) {
                    databaseRepository.getHome(id)
                }
                when(result){
                    is AuthOperationResult.Error -> {
                        _uiState.update {
                            AddEditHomeUIState.Error(error)
                        }
                    }
                    is AuthOperationResult.Loading -> {

                    }
                    is AuthOperationResult.Success -> {
                        data.home = result.data!!
                        _uiState.update {
                            AddEditHomeUIState.ScreenDataChanged(data)
                        }
                    }
                }
            }
        }
    }

    override fun saveHome() {
        if (data.home.name.isNotEmpty()){
            data.home.owner = FirebaseAuth.getInstance().currentUser!!.uid
            if (FirebaseAuth.getInstance().currentUser!!.email !in data.home.members){
                data.home.members.add(FirebaseAuth.getInstance().currentUser!!.email)
            }
            data.home.name = data.home.name.trim()
            data.home.notes = data.home.notes.trim()
            viewModelScope.launch {
                if (data.home.id.isEmpty()) databaseRepository.createHome(data.home)
                else databaseRepository.updateHome(data.home)
                _uiState.update {
                    AddEditHomeUIState.ReturnBack()
                }
            }
        }else{
            data.homeNameError = textError
            _uiState.update {
                AddEditHomeUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun deleteHome() {
        viewModelScope.launch {
            databaseRepository.deleteHome(data.home)
            _uiState.update {
                AddEditHomeUIState.HomeDeleted()
            }
        }
    }

    fun returnBack(){
        _uiState.update {
            AddEditHomeUIState.ReturnBack()
        }
    }
}