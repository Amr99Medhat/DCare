package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.repositories.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private var mRegisterRepository: RegisterRepository =
        RegisterRepository()

    private var _registerUserAuthResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val registerUserAuthResult: StateFlow<HashMap<String, Any>>
        get() = _registerUserAuthResult

    private var _registerUserResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val registerUserResult: StateFlow<HashMap<String, Any>>
        get() = _registerUserResult


    suspend fun registerUserAuth(
        email: String,
        password: String
    ) {
        _registerUserAuthResult = mRegisterRepository.registerUserAuth(email, password)
    }

    suspend fun registerUser(
        userInfo: User
    ) {
        _registerUserResult = mRegisterRepository.registerUser(userInfo)
    }

    fun signOut() {
        viewModelScope.launch {
            mRegisterRepository.signOut()
        }
    }
}