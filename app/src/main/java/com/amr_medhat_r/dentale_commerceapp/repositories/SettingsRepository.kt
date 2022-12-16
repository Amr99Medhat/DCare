package com.amr_medhat_r.dentale_commerceapp.repositories

import android.content.Context
import com.amr_medhat_r.dentale_commerceapp.database.PreferenceManager
import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class SettingsRepository {

    private var mGetUserDetailsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private lateinit var mPreferenceManager: PreferenceManager

    suspend fun getUserDetails(): MutableStateFlow<HashMap<String, Any>> {
        mGetUserDetailsResult = MutableStateFlow(HashMap())
        // Log-In using FirebaseAuth
        FireStoreClass().getUserDetails()
            .addOnCompleteListener { document ->
                if (document.isSuccessful) {
                    // Here we received the document snapshot which is converted into the user Data model object.
                    val user = document.result.toObject(User::class.java)
                    val logInRegisteredUserResponse = HashMap<String, Any>()
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = true
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_VALUE] =
                        user!!
                    runBlocking {
                        mGetUserDetailsResult.emit(
                            logInRegisteredUserResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val logInRegisteredUserResponse = HashMap<String, Any>()
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = false
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_VALUE] =
                        document.exception!!.message.toString()
                    runBlocking {
                        mGetUserDetailsResult.emit(
                            logInRegisteredUserResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val logInRegisteredUserResponse = HashMap<String, Any>()
                logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = false
                logInRegisteredUserResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mGetUserDetailsResult.emit(
                        logInRegisteredUserResponse
                    )
                }
            }

        return mGetUserDetailsResult
    }

    fun signOut(context: Context) {
        // Clear SharedPreference Data Before signOut
        mPreferenceManager = PreferenceManager(context)
        mPreferenceManager.clear()

        FirebaseAuthClass().signOut()
    }
}