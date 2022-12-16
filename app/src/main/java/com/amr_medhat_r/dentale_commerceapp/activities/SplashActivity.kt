package com.amr_medhat_r.dentale_commerceapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivitySplashBinding
import com.amr_medhat_r.dentale_commerceapp.viewModels.SplashViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        setupView()

//        lifecycleScope.launchWhenResumed {
//            setAppLanguage()
//        }

        lifecycleScope.launchWhenResumed {
            setAnimation()
        }

        lifecycleScope.launchWhenResumed {
            delay(4000)
            lifecycleScope.launchWhenResumed {
                moveToSignIn()
            }
            lifecycleScope.launchWhenResumed {
                observeSignInState()
            }
        }

    }

    private fun setupView() {
        // Light mode always on.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    /**
     * A function to handle the views animation on the screen.
     */
    private fun setAnimation() {
        // Show the Views after a few seconds.
        binding.lavDentalAnimationView.animate().apply {
            interpolator = LinearInterpolator()
            alpha(1f)
            startDelay = 500
            start()
        }
        binding.tvAppName.animate().apply {
            interpolator = LinearInterpolator()
            alpha(1f)
            startDelay = 500
            start()
        }
        binding.lavLoadingAnimationView.animate().apply {
            interpolator = LinearInterpolator()
            alpha(1f)
            startDelay = 1300
            start()
        }
    }

    private suspend fun moveToSignIn() {
        viewModel.getSignInState(application)

    }

    private suspend fun observeSignInState() {
        viewModel.getSignInStateResult.collect { state ->
            if (state) {
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                //startActivity(Intent(this@SplashActivity, LoginActivity::class.java))

            } else {
                //startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }
    }

//    private suspend fun setAppLanguage() {
//        mSplashViewModel.getAppLanguage(this@SplashActivity).collect { language ->
//            when (language) {
//                Constants.KEY_EMPTY -> {
//                    SupportClass.setLocale(Constants.KEY_LANGUAGE_ENGLISH, resources)
//                }
//                Constants.KEY_LANGUAGE_ENGLISH -> {
//                    SupportClass.setLocale(Constants.KEY_LANGUAGE_ENGLISH, resources)
//                }
//                Constants.KEY_LANGUAGE_ARABIC -> {
//                    SupportClass.setLocale(Constants.KEY_LANGUAGE_ARABIC, resources)
//                }
//            }
//        }
//    }

    /**
     * Override onDestroy() to close all resources after leaving, to avoid memory leak.
     */
    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
        lifecycleScope.cancel()
        viewModel.viewModelScope.cancel()
    }
}