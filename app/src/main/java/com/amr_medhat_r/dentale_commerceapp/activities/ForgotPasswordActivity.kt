package com.amr_medhat_r.dentale_commerceapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityForgotPasswordBinding
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.viewModels.ForgotPasswordViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
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
        binding.btnSubmit.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                sendEmailToResetPassword()
            }
            lifecycleScope.launchWhenResumed {
                observeSendEmailToResetPasswordResult()
            }
        }
    }

    /**
     * A function to validate the entries of the user.
     */
    private fun validateResetPasswordDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding.edEmailId.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to send a e-mail to the user from firebase to reset the user's password.
     */
    private suspend fun sendEmailToResetPassword() {
        if (validateResetPasswordDetails()) {

            //Show the progress dialog.
            showProgressDialog(this@ForgotPasswordActivity)

            // Get the text from editText and trim the space.
            val email = binding.edEmailId.text.toString().trim { it <= ' ' }

            lifecycleScope.launchWhenResumed {
                // Reset password using FirebaseAuth
                viewModel.sendEmailToResetPassword(email)
            }

        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeSendEmailToResetPasswordResult() {
        viewModel.sendEmailToResetPasswordResult.collectLatest { resetResult ->
            when (resetResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if reset password was successful.
                true -> {
                    // Hide the progress dialog.
                    hideProgressDialog()
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        resources.getString(R.string.email_sent_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Close this activity.
                    finish()
                }
                // Handle if reset password was not successful.
                false -> {
                    // Hide the progress dialog.
                    hideProgressDialog()
                    showErrorSnackBar(
                        resetResult[Constants.KEY_RESPONSE_VALUE].toString(),
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