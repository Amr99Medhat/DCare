package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class ForgotPasswordRepository {

    private var mSendEmailToResetPasswordResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    suspend fun sendEmailToResetPassword(email: String): MutableStateFlow<HashMap<String, Any>> {
        mSendEmailToResetPasswordResult = MutableStateFlow(HashMap())
        // Reset password using FirebaseAuth
        FirebaseAuthClass().sendEmailToResetPassword(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val sendEmailToResetPasswordResponse = HashMap<String, Any>()
                    sendEmailToResetPasswordResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mSendEmailToResetPasswordResult.emit(
                            sendEmailToResetPasswordResponse
                        )
                    }
                } else {
                    // If the reset is not successful then show error message.
                    val sendEmailToResetPasswordResponse = HashMap<String, Any>()
                    sendEmailToResetPasswordResponse[Constants.KEY_RESPONSE_STATE] = false
                    sendEmailToResetPasswordResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mSendEmailToResetPasswordResult.emit(
                            sendEmailToResetPasswordResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val sendEmailToResetPasswordResponse = HashMap<String, Any>()
                sendEmailToResetPasswordResponse[Constants.KEY_RESPONSE_STATE] = false
                sendEmailToResetPasswordResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mSendEmailToResetPasswordResult.emit(
                        sendEmailToResetPasswordResponse
                    )
                }
            }
        return mSendEmailToResetPasswordResult
    }

}