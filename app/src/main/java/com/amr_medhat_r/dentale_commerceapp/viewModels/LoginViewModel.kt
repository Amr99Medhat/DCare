package com.amr_medhat_r.dentale_commerceapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.repositories.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var mLoginRepository: LoginRepository =
        LoginRepository()

    private var _logInRegisteredUserResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val logInRegisteredUserResult: StateFlow<HashMap<String, Any>>
        get() = _logInRegisteredUserResult

    private var _getUserDetailsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getUserDetailsResult: StateFlow<HashMap<String, Any>>
        get() = _getUserDetailsResult

    suspend fun logInRegisteredUser(
        email: String,
        password: String
    ) {
        _logInRegisteredUserResult = mLoginRepository.logInRegisteredUser(email, password)
    }

    suspend fun getUserDetails() {
        _getUserDetailsResult = mLoginRepository.getUserDetails()
    }

    fun saveBasicData(
        context: Context,
        user: User
    ) {
        viewModelScope.launch {
            mLoginRepository.saveBasicData(context, user)
        }
    }
}