package com.amr_medhat_r.dentale_commerceapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityRegisterBinding
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.viewModels.RegisterViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        setListeners()
    }

    /**
     * A function to handle user clicks.
     */
    @Suppress("DEPRECATION")
    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        binding.tvLogin.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        binding.btnRegister.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                registerUserAuth()
            }
            lifecycleScope.launchWhenResumed {
                observeRegisterUserAuthResult()
            }
        }
    }

    /**
     * A function to validate the entries of a new  user.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding.edFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(
                binding.edLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
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
            TextUtils.isEmpty(
                binding.edConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            TextUtils.isEmpty(
                binding.edConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            binding.edPassword.text.toString()
                .trim { it <= ' ' } != binding.edConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !binding.cbTermsAndConditions.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_conditions),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to register a new user and sign out after that and finish the activity.
     */
    private suspend fun registerUserAuth() {
        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            showProgressDialog(this@RegisterActivity)


            val email = binding.edEmailId.text.toString().trim { it <= ' ' }
            val password = binding.edPassword.text.toString().trim { it <= ' ' }


            // Create an instance and create a register a user with email and password.
            viewModel.registerUserAuth(email, password)
        }
    }

    private suspend fun observeRegisterUserAuthResult() {
        viewModel.registerUserAuthResult.collectLatest { authResult ->

            when (authResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if register auth was successful.
                true -> {
                    //create an object from user and pass the user data to FireStore.
                    val user = User(
                        id = authResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        firstName = binding.edFirstName.text.toString()
                            .trim { it <= ' ' },
                        lastName = binding.edLastName.text.toString()
                            .trim { it <= ' ' },
                        email = binding.edEmailId.text.toString()
                            .trim { it <= ' ' }
                    )

                    lifecycleScope.launchWhenResumed {
                        // Upload the user to FireStore
                        viewModel.registerUser(user)
                    }
                    lifecycleScope.launchWhenResumed {
                        // Upload the user to FireStore
                        observeOnRegisterUserResult()
                    }
                }
                // Handle if register auth was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@RegisterActivity,
                        authResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private suspend fun observeOnRegisterUserResult() {
        viewModel.registerUserResult.collectLatest { registerResult ->
            when (registerResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if register was successful.
                true -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@RegisterActivity,
                        resources.getString(R.string.registry_successful),
                        Toast.LENGTH_LONG
                    ).show()

                    viewModel.signOut()
                    finish()
                }
                // Handle if register was not successful.
                false -> {
                    hideProgressDialog()
                    showErrorSnackBar(
                        registerResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        true
                    )
                }
            }

        }
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