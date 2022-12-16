package com.amr_medhat_r.dentale_commerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityLoginBinding
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.viewModels.LoginViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        setListeners()
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        binding.tvRegister.setOnClickListener {
            // Launch the register screen when the user clicks on the text.
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                lifecycleScope.launchWhenResumed {
                    logInRegisteredUser()
                }
                lifecycleScope.launchWhenResumed {
                    observeLogInRegisteredUserResult()
                }
            }
        }
        binding.tvForgotPassword.setOnClickListener {
            // Launch the ForgotPassword screen when the user clicks on the text.
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding.edEmailId.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(
                binding.edPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to log-in the user through his email and password.
     */
    private suspend fun logInRegisteredUser() {
        if (validateLoginDetails()) {

            //Show the progress dialog.
            showProgressDialog(this@LoginActivity)

            // Get the text from editText and trim the space.
            val email = binding.edEmailId.text.toString().trim { it <= ' ' }
            val password = binding.edPassword.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            viewModel.logInRegisteredUser(email, password)

        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeLogInRegisteredUserResult() {
        viewModel.logInRegisteredUserResult.collectLatest { loginResult ->
            when (loginResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    lifecycleScope.launchWhenResumed {
                        viewModel.getUserDetails()
                    }
                    lifecycleScope.launchWhenResumed {
                        observeUserDetailsResult()
                    }
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@LoginActivity,
                        loginResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeUserDetailsResult() {
        viewModel.getUserDetailsResult.collectLatest { userDetailsResult ->
            when (userDetailsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    // Hide the progress dialog
                    hideProgressDialog()

                    val user = userDetailsResult[Constants.KEY_RESPONSE_VALUE] as User
                    // Save user's Data in local database (SharedPrefs).
                    saveBasicUserData(user)

                    if (user.profileCompleted == 0) {
                        // If the user profile is incomplete then the UserProfileActivity
                        val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
                        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
                        startActivity(intent)
                    } else {
                        // Redirect the user to MainActivity after log in.
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    }
                    finish()
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@LoginActivity,
                        userDetailsResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * A function to save the basic data about the user in shared prefs.
     */
    private fun saveBasicUserData(user: User) {
        viewModel.saveBasicData(
            this,
            user
        )
    }

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