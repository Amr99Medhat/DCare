package com.amr_medhat_r.dentale_commerceapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private var mSettingsRepository: SettingsRepository =
        SettingsRepository()

    private var _getUserDetailsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getUserDetailsResult: StateFlow<HashMap<String, Any>>
        get() = _getUserDetailsResult


    suspend fun getUserDetails() {
        _getUserDetailsResult = mSettingsRepository.getUserDetails()
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            mSettingsRepository.signOut(context)
        }
    }
}