package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class RegisterRepository {

    private var mRegisterUserAuthResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    private var mRegisterUserFireStoreResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    /**
     * A function to register a new user and sign out after that and finish the activity.
     */
    suspend fun registerUserAuth(
        email: String,
        password: String
    ): MutableStateFlow<HashMap<String, Any>> {
        mRegisterUserAuthResult = MutableStateFlow(HashMap())
        // Create an instance and create a register a user with email and password.
        FirebaseAuthClass().registerUser(email, password)
            .addOnCompleteListener { task ->
                // If the registration is successfully done.
                if (task.isSuccessful) {
                    val authResultResponse = HashMap<String, Any>()
                    authResultResponse[Constants.KEY_RESPONSE_STATE] = true
                    authResultResponse[Constants.KEY_RESPONSE_VALUE] = task.result.user!!.uid
                    runBlocking {
                        mRegisterUserAuthResult.emit(
                            authResultResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val authResultResponse = HashMap<String, Any>()
                    authResultResponse[Constants.KEY_RESPONSE_STATE] = false
                    authResultResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mRegisterUserAuthResult.emit(
                            authResultResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val authResultResponse = HashMap<String, Any>()
                authResultResponse[Constants.KEY_RESPONSE_STATE] = false
                authResultResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mRegisterUserAuthResult.emit(
                        authResultResponse
                    )
                }
            }

        return mRegisterUserAuthResult
    }

    /**
     * A function to register a new user in FireStore.
     */
    suspend fun registerUser(
        userInfo : User
    ): MutableStateFlow<HashMap<String, Any>> {
        mRegisterUserAuthResult = MutableStateFlow(HashMap())
        FireStoreClass().registerUser(userInfo)
            .addOnCompleteListener { task ->
                // If the registration is successfully done.
                if (task.isSuccessful) {
                    val authResultResponse = HashMap<String, Any>()
                    authResultResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mRegisterUserFireStoreResult.emit(
                            authResultResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val authResultResponse = HashMap<String, Any>()
                    authResultResponse[Constants.KEY_RESPONSE_STATE] = false
                    authResultResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mRegisterUserFireStoreResult.emit(
                            authResultResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val authResultResponse = HashMap<String, Any>()
                authResultResponse[Constants.KEY_RESPONSE_STATE] = false
                authResultResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mRegisterUserFireStoreResult.emit(
                        authResultResponse
                    )
                }
            }

        return mRegisterUserFireStoreResult
    }


    fun signOut() {
        FirebaseAuthClass().signOut()
    }


}