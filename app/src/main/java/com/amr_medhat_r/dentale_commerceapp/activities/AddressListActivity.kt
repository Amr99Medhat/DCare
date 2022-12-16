package com.amr_medhat_r.dentale_commerceapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.adapters.AddressListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityAddressListBinding
import com.amr_medhat_r.dentale_commerceapp.models.Address
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.SwipeToDeleteCallback
import com.amr_medhat_r.dentale_commerceapp.utilities.SwipeToEditCallback
import com.amr_medhat_r.dentale_commerceapp.viewModels.AddressListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityAddressListBinding
    private lateinit var viewModel: AddressListViewModel
    private lateinit var mAddressList: ArrayList<Address>
    private var mSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AddressListViewModel::class.java]
        setListeners()
        getAddressList()

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }
        if (mSelectAddress) {
            binding.tvTitle.text =
                resources.getString(R.string.title_select_address)
        }
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
        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }
    }

    private fun getAddressList() {
        lifecycleScope.launch(Dispatchers.Main) {
            showProgressDialog(this@AddressListActivity)
        }
        lifecycleScope.launchWhenResumed {
            viewModel.getAddressesList()
        }
        lifecycleScope.launchWhenResumed {
            observeGetAllProductsListResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeGetAllProductsListResult() {
        viewModel.getAddressesListResult.collectLatest { addressListResult ->
            when (addressListResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    hideProgressDialog()
                    mAddressList =
                        addressListResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<Address>

                    hideProgressDialog()

                    if (mAddressList.size > 0) {
                        binding.rvAddressList.visibility = View.VISIBLE
                        binding.tvNoAddressFound.visibility = View.GONE
                        binding.rvAddressList.layoutManager =
                            LinearLayoutManager(this@AddressListActivity)
                        binding.rvAddressList.setHasFixedSize(true)

                        val addressAdapter =
                            AddressListAdapter(
                                this@AddressListActivity,
                                mAddressList,
                                mSelectAddress
                            )
                        binding.rvAddressList.adapter = addressAdapter

                        if (!mSelectAddress) {
                            val editSwipeHandler =
                                object : SwipeToEditCallback(this@AddressListActivity) {
                                    override fun onSwiped(
                                        viewHolder: RecyclerView.ViewHolder,
                                        direction: Int
                                    ) {
                                        val adapter =
                                            binding.rvAddressList.adapter as AddressListAdapter
                                        adapter.notifyEditItem(
                                            this@AddressListActivity,
                                            viewHolder.adapterPosition
                                        )
                                    }
                                }


                            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                            editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

                            val deleteSwipeHandler =
                                object : SwipeToDeleteCallback(this@AddressListActivity) {
                                    override fun onSwiped(
                                        viewHolder: RecyclerView.ViewHolder,
                                        direction: Int
                                    ) {
                                        //Show the progress dialog.
                                        showProgressDialog(this@AddressListActivity)

                                        deleteAddress(mAddressList[viewHolder.adapterPosition].id)
                                        mAddressList.clear()
                                    }
                                }

                            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                            deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
                        }


                    } else {
                        binding.rvAddressList.visibility = View.GONE
                        binding.tvNoAddressFound.visibility = View.VISIBLE
                    }

                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@AddressListActivity,
                        addressListResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun deleteAddress(id: String) {
        lifecycleScope.launchWhenResumed {
            viewModel.deleteAddress(id)
        }
        lifecycleScope.launchWhenResumed {
            observeDeleteAddressResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeDeleteAddressResult() {
        viewModel.deleteAddressResult.collectLatest { addressListResult ->
            when (addressListResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    hideProgressDialog()

                    Toast.makeText(
                        this@AddressListActivity,
                        resources.getString(R.string.err_your_address_deleted_successfully),
                        Toast.LENGTH_SHORT
                    ).show()

                    getAddressList()
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@AddressListActivity,
                        addressListResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            getAddressList()
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