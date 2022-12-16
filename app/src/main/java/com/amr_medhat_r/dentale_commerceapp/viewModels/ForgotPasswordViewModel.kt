package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.repositories.ForgotPasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ForgotPasswordViewModel : ViewModel() {
    private var mForgotPasswordRepository: ForgotPasswordRepository =
        ForgotPasswordRepository()

    private var _sendEmailToResetPasswordResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val sendEmailToResetPasswordResult: StateFlow<HashMap<String, Any>>
        get() = _sendEmailToResetPasswordResult

    suspend fun sendEmailToResetPassword(
        email: String
    ) {
        _sendEmailToResetPasswordResult = mForgotPasswordRepository.sendEmailToResetPassword(email)
    }
}