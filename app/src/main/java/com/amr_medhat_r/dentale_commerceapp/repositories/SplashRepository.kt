package com.amr_medhat_r.dentale_commerceapp.repositories

import android.content.Context
import com.amr_medhat_r.dentale_commerceapp.database.PreferenceManager
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow

class SplashRepository {
    // TODO ("Handle shared Prefs and languages")
     private lateinit var mPreferenceManager: PreferenceManager
    private var mSignInState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    //private var mLanguage: MutableStateFlow<String> = MutableStateFlow(Constants.KEY_EMPTY)
    //private var mDarkModeState: MutableStateFlow<String> = MutableStateFlow(Constants.KEY_EMPTY)


    suspend fun getSignInState(context: Context): MutableStateFlow<Boolean> {
        mSignInState = MutableStateFlow(false)
        mPreferenceManager = PreferenceManager(context)
        val state = mPreferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)
        if (state) {
            this.mSignInState.emit(true)
        } else {
            this.mSignInState.emit(false)
        }
        return mSignInState
    }

//    suspend fun getAppLanguage(context: Context): MutableStateFlow<String> {
//        mLanguage = MutableStateFlow(Constants.KEY_EMPTY)
//        mPreferenceManager = PreferenceManager(context)
//        val appLanguage = mPreferenceManager.getString(Constants.KEY_APP_LANGUAGE)
//        if (appLanguage != null) {
//            this.mLanguage.emit(appLanguage)
//        }
//        return this.mLanguage
//    }

}