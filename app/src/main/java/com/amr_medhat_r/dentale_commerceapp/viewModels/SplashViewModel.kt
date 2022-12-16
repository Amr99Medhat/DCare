package com.amr_medhat_r.dentale_commerceapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.repositories.SplashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel : ViewModel() {

    private var mSplashRepository: SplashRepository = SplashRepository()

    private var _getSignInStateResult: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val getSignInStateResult: StateFlow<Boolean>
        get() = _getSignInStateResult

    suspend fun getSignInState(context: Context) {
        _getSignInStateResult = mSplashRepository.getSignInState(context)
    }
//
//    suspend fun getAppLanguage(context: Context): MutableStateFlow<String> {
//        return mSplashScreenRepository.getAppLanguage(context)
//    }
}