package com.amr_medhat_r.dentale_commerceapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivitySettingsBinding
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.GlideLoader
import com.amr_medhat_r.dentale_commerceapp.viewModels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
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
        binding.btnLogout.setOnClickListener {
            showProgressDialog(this@SettingsActivity)
            signOut()
        }
        binding.tvEdit.setOnClickListener {
            val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
            startActivity(intent)
        }
        binding.llAddress.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, AddressListActivity::class.java))
        }
    }

    private fun getUserDetails() {
        //Show the progress dialog.
        lifecycleScope.launch(Dispatchers.Main) {
            showProgressDialog(this@SettingsActivity)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.getUserDetails()
        }
        lifecycleScope.launchWhenResumed {
            observeUserDetailsResult()
        }

    }

    /**
     * A function to observe on results from viewModel.
     */
    @SuppressLint("SetTextI18n")
    private suspend fun observeUserDetailsResult() {
        viewModel.getUserDetailsResult.collectLatest { userDetailsResult ->
            when (userDetailsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    mUserDetails = userDetailsResult[Constants.KEY_RESPONSE_VALUE] as User

                    // Hide the progress dialog
                    hideProgressDialog()

                    // Set the User's Image.
                    GlideLoader(this@SettingsActivity).loadUserImage(
                        mUserDetails.image,
                        binding.ivUserPhoto
                    )
                    binding.tvName.text =
                        "${mUserDetails.firstName} ${mUserDetails.lastName}"
                    binding.tvEmail.text = mUserDetails.email
                    binding.tvGender.text = mUserDetails.gender
                    binding.tvMobileNumber.text = mUserDetails.mobile
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    showErrorSnackBar(
                        userDetailsResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        true
                    )
                    finish()
                }
            }
        }
    }

    private fun signOut() {
        //signOut.
        viewModel.signOut(this@SettingsActivity)

        //Go to LoginActivity.
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
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