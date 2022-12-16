package com.amr_medhat_r.dentale_commerceapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityAddEditAddressBinding
import com.amr_medhat_r.dentale_commerceapp.models.Address
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.viewModels.AddEditAddressViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest

class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditAddressBinding
    private lateinit var viewModel: AddEditAddressViewModel
    private var mAddressDetails: Address? = null
    private var mUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AddEditAddressViewModel::class.java]

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                binding.tvTitle.text =
                    resources.getString(R.string.title_edit_address)
                binding.btnSubmit.text =
                    resources.getString(R.string.btn_lbl_update)

                binding.edFullName.setText(mAddressDetails?.name)
                binding.edPhoneNumber.setText(mAddressDetails?.mobileNumber)
                binding.edAddress.setText(mAddressDetails?.address)
                binding.edZipCode.setText(mAddressDetails?.zipCode)
                binding.edAdditionalNote.setText(mAddressDetails?.additionalNote)


                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        binding.rbHome.isChecked = true
                    }
                    Constants.OFFICE -> {
                        binding.rbOffice.isChecked = true
                    }
                    else -> {
                        binding.rbOther.isChecked = true
                        binding.etOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

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
            saveAddressToFireStore()
        }
        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                binding.etOtherDetails.visibility = View.VISIBLE
            } else {
                binding.etOtherDetails.visibility = View.GONE
            }
        }
    }

    private fun getUserID() {
        lifecycleScope.launchWhenResumed {
            viewModel.getCurrentUserID()
        }
        lifecycleScope.launchWhenResumed {
            viewModel.getCurrentUserIDResult.collectLatest { UserID ->
                mUserID = UserID
            }
        }
    }

    private fun saveAddressToFireStore() {

        if (validateAddressDetails()) {

            //Show the progress dialog.
            showProgressDialog(this@AddEditAddressActivity)

            getUserID()

            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    Constants.HOME
                }

                binding.rbOffice.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }
            val addressModel = Address(mUserID,
                binding.edFullName.text.toString().trim { it <= ' ' },
                binding.edPhoneNumber.text.toString().trim { it <= ' ' },
                binding.edAddress.text.toString().trim { it <= ' ' },
                binding.edZipCode.text.toString().trim { it <= ' ' },
                binding.edAdditionalNote.text.toString().trim { it <= ' ' },
                addressType,
                binding.etOtherDetails.text.toString().trim { it <= ' ' })

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                updateAddress(
                    addressModel,
                    mAddressDetails!!.id
                )

            } else {
                addAddress(
                    addressModel
                )
            }
        }
    }

    private fun updateAddress(addressModel: Address, id: String) {
        lifecycleScope.launchWhenResumed {
            viewModel.updateAddress(addressModel, id)
        }
        lifecycleScope.launchWhenResumed {
            observeUpdateAddress()
        }
    }

    private fun addAddress(addressModel: Address) {
        lifecycleScope.launchWhenResumed {
            viewModel.addAddress(addressModel)
        }
        lifecycleScope.launchWhenResumed {
            observeAddAddress()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeAddAddress() {
        viewModel.addAddressResult.collectLatest { addResult ->
            when (addResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if Add to Cart was successful.
                true -> {
                    hideProgressDialog()

                    val notifySuccessMessage: String =
                        if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                            resources.getString(R.string.msg_your_address_updated_successfully)
                        } else {
                            resources.getString(R.string.your_address_was_added_successfully)
                        }

                    Toast.makeText(
                        this@AddEditAddressActivity,
                        notifySuccessMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                }
                // Handle if Add to Cart was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@AddEditAddressActivity,
                        addResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeUpdateAddress() {
        viewModel.updateAddressResult.collectLatest { addResult ->
            when (addResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if Add to Cart was successful.
                true -> {
                    hideProgressDialog()

                    val notifySuccessMessage: String =
                        if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                            resources.getString(R.string.msg_your_address_updated_successfully)
                        } else {
                            resources.getString(R.string.your_address_was_added_successfully)
                        }

                    Toast.makeText(
                        this@AddEditAddressActivity,
                        notifySuccessMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                }
                // Handle if Add to Cart was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@AddEditAddressActivity,
                        addResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateAddressDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.edFullName.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name), true
                )
                false
            }
            TextUtils.isEmpty(binding.edPhoneNumber.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number), true
                )
                false
            }
            TextUtils.isEmpty(binding.edAddress.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_address), true
                )
                false
            }
            TextUtils.isEmpty(binding.edZipCode.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_zip_code), true
                )
                false
            }
            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etOtherDetails.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_other_details), true
                )
                false
            }
            else -> {
                true
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