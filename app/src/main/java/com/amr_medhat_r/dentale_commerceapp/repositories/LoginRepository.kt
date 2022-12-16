package com.amr_medhat_r.dentale_commerceapp.repositories

import android.content.Context
import com.amr_medhat_r.dentale_commerceapp.database.PreferenceManager
import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class LoginRepository {

    private var mLogInRegisteredUserResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    private var mGetUserDetailsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private lateinit var mPreferenceManager: PreferenceManager

    suspend fun logInRegisteredUser(
        email: String,
        password: String
    ): MutableStateFlow<HashMap<String, Any>> {
        mLogInRegisteredUserResult = MutableStateFlow(HashMap())
        // Log-In using FirebaseAuth
        FirebaseAuthClass().logInRegisteredUser(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val logInRegisteredUserResponse = HashMap<String, Any>()
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mLogInRegisteredUserResult.emit(
                            logInRegisteredUserResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val logInRegisteredUserResponse = HashMap<String, Any>()
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = false
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mLogInRegisteredUserResult.emit(
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
                    mLogInRegisteredUserResult.emit(
                        logInRegisteredUserResponse
                    )
                }
            }

        return mLogInRegisteredUserResult
    }

    suspend fun getUserDetails() : MutableStateFlow<HashMap<String, Any>>  {
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

    fun saveBasicData(
        context: Context,
        user: User
    ) {

//        mPreferenceManager.putString(Constants.KEY_LAB_ID, user.id)
//        mPreferenceManager.putString(Constants.KEY_IMAGE, user.image)
//        mPreferenceManager.putString(Constants.KEY_LAB_NAME, user.labName)
//        mPreferenceManager.putString(Constants.KEY_LAB_PHONE_NUMBER, user.phoneNumber)
//        mPreferenceManager.putString(Constants.KEY_PASSWORD, user.password)
//        mPreferenceManager.putString(Constants.KEY_LATITUDE, user.latitude)
//        mPreferenceManager.putString(Constants.KEY_LONGITUDE, user.longitude)
//        mPreferenceManager.putString(Constants.KEY_APP_LANGUAGE, Constants.KEY_LANGUAGE_ENGLISH)
//        mPreferenceManager.putString(Constants.KEY_APP_DARK_MODE_STATE, Constants.KEY_LIGHT_MODE)

        mPreferenceManager = PreferenceManager(context)
        mPreferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
        mPreferenceManager.putString(Constants.LOGGED_IN_USERNAME,"${user.firstName} ${user.lastName}")
    }
}