package com.amr_medhat_r.dentale_commerceapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityUserProfileBinding
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.GlideLoader
import com.amr_medhat_r.dentale_commerceapp.viewModels.UserProfileViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import java.io.FileNotFoundException

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        getUserDetails()
        setUserDetailsOnScreen()
        setListeners()

        // If the Intent came from Settings Activity.
        handleProfileEditScreen()
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
            if (validateUserProfileDetails()) {
                showProgressDialog(this@UserProfileActivity)

                if (mSelectedImageFileUri != null) {
                    lifecycleScope.launchWhenResumed {
                        viewModel.uploadImageToCloudStorage(
                            this@UserProfileActivity,
                            mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE
                        )

                    }
                    lifecycleScope.launchWhenResumed {
                        observeUploadImageToCloudStorageResult()
                    }

                } else {
                    lifecycleScope.launchWhenResumed {
                        updateUserProfileDetails()
                    }
                    lifecycleScope.launchWhenResumed {
                        observeUpdateUserProfileDataResult()
                    }
                }
            }
        }
        binding.ivUserPhoto.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.pickImage(pickImage)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getUserDetails() {
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as parcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
    }

    private fun setUserDetailsOnScreen() {
        binding.edFirstName.isEnabled = false
        binding.edFirstName.setText(mUserDetails.firstName)
        binding.edLastName.isEnabled = false
        binding.edLastName.setText(mUserDetails.lastName)
        binding.edEmailId.isEnabled = false
        binding.edEmailId.setText(mUserDetails.email)
        if (mUserDetails.profileCompleted == 0) {
            binding.edPhoneNumber.requestFocus()
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                binding.edPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_mobile_number), true)
                false
            }
            // If the user live in egypt.
//            mActivityUserProfileBinding.edPhoneNumber.text.toString()
//                .trim { it <= ' ' }.length < 11 -> {
//                showErrorSnackBar(
//                    resources.getString(R.string.err_msg_mobile_number_less_than_11),
//                    true
//                )
//                false
//            }
            else -> {
                true
            }
        }
    }

    /**
    If the Intent came from Settings Activity.
     */
    private fun handleProfileEditScreen() {
        if (mUserDetails.profileCompleted != 0) {
            // Edit the Screen info
            binding.tvTitle.text = resources.getString(R.string.edit_profile)
            binding.ivBack.visibility = View.VISIBLE

            // Add the Missing Data about the User.
            GlideLoader(this@UserProfileActivity).loadUserImage(
                mUserDetails.image,
                binding.ivUserPhoto
            )
            if (mUserDetails.mobile.isNotEmpty()) {
                binding.edPhoneNumber.setText(mUserDetails.mobile)
            }
            if (mUserDetails.gender == Constants.MALE) {
                binding.rbMale.isChecked = true
            } else {
                binding.rbFemale.isChecked = true
            }

            // Enable access and edit the EditTexts.
            binding.edFirstName.isEnabled = true
            binding.edLastName.isEnabled = true
            binding.edFirstName.backgroundTintList = null
            binding.edLastName.backgroundTintList = null

        }
    }

    private suspend fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = binding.edFirstName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        val lastName = binding.edLastName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }
        val mobileNumber =
            binding.edPhoneNumber.text.toString()
                .trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile) {
            userHashMap[Constants.MOBILE] = mobileNumber
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        viewModel.updateUserProfileData(userHashMap)
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeUpdateUserProfileDataResult() {
        viewModel.updateUserProfileDataResult.collectLatest { UpdateResult ->
            when (UpdateResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if reset password was successful.
                true -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@UserProfileActivity,
                        resources.getString(R.string.msg_profile_updated_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@UserProfileActivity, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    ContextCompat.startActivity(this@UserProfileActivity, intent, null)

//                    startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
//                    finish()
                }
                // Handle if reset password was not successful.
                false -> {
                    // Hide the progress dialog.
                    hideProgressDialog()
                    showErrorSnackBar(
                        UpdateResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        true
                    )
                }
            }
        }
    }

    private suspend fun observeUploadImageToCloudStorageResult() {
        viewModel.uploadImageToCloudStorageResult.collectLatest { UploadResult ->
            when (UploadResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if reset password was successful.
                true -> {
                    mUserProfileImageURL = UploadResult[Constants.KEY_RESPONSE_VALUE].toString()

                    lifecycleScope.launchWhenResumed {
                        updateUserProfileDetails()
                    }
                    lifecycleScope.launchWhenResumed {
                        observeUpdateUserProfileDataResult()
                    }

                }
                // Handle if reset password was not successful.
                false -> {
                    // Hide the progress dialog.
                    hideProgressDialog()
                    showErrorSnackBar(
                        UploadResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        true
                    )
                }
            }
        }
    }

    private val pickImage: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    mSelectedImageFileUri = result.data!!.data
                    try {
                        GlideLoader(this@UserProfileActivity).loadUserImage(
                            mSelectedImageFileUri!!,
                            binding.ivUserPhoto
                        )

                    } catch (e: FileNotFoundException) {
                        showErrorSnackBar(
                            resources.getString(R.string.image_selection_failed),
                            true
                        )
                    }
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                // A log printed when user close or cancel the image selection.
                Log.d("Request Cancelled", "Image Selection Cancelled")
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