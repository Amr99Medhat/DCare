package com.amr_medhat_r.dentale_commerceapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr_medhat_r.dentale_commerceapp.adapters.CartItemsListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityCheckoutBinding
import com.amr_medhat_r.dentale_commerceapp.models.Address
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.Order
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.viewModels.CheckoutViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel
    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order
    private var mUserID: String = ""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]

        setListeners()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text =
                "${mAddressDetails?.address}, ${mAddressDetails?.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.visibility = View.VISIBLE
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            } else {
                binding.tvCheckoutOtherDetails.visibility = View.GONE
            }

            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }

        getProductsList()
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }
    }

    private fun getProductsList() {
        lifecycleScope.launch(Dispatchers.Main) {
            showProgressDialog(this@CheckoutActivity)
        }
        lifecycleScope.launchWhenResumed {
            viewModel.getAllProductsList()
        }
        lifecycleScope.launchWhenResumed {
            observeDashboardItemsListResult()
        }
    }


    private fun placeAnOrder() {
        showProgressDialog(this@CheckoutActivity)

        getUserID()

        if (mAddressDetails != null) {
            mOrderDetails = Order(
                mUserID,
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            placeOrder(mOrderDetails)
        }

    }

    private fun placeOrder(OrderDetails: Order) {
        lifecycleScope.launchWhenResumed {
            viewModel.placeOrder(OrderDetails)
        }
        lifecycleScope.launchWhenResumed {
            observePlaceOrderResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeDashboardItemsListResult() {
        viewModel.getAllProductsListResult.collectLatest { productsResult ->
            when (productsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    val productsList =
                        productsResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<Product>
                    mProductList = productsList
                    getCartItemsList()
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                }
            }
        }
    }

    private suspend fun getCartItemsList() {
        lifecycleScope.launchWhenResumed {
            viewModel.getCartList()
        }
        lifecycleScope.launchWhenResumed {
            observeGetCartItemsListResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @SuppressLint("SetTextI18n")
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeGetCartItemsListResult() {
        viewModel.getCartListResult.collectLatest { cartListResult ->
            when (cartListResult[Constants.KEY_RESPONSE_STATE]) {
                true -> {
                    hideProgressDialog()
                    val cartList =
                        cartListResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<CartItem>

                    for (product in mProductList) {
                        for (cartItem in cartList) {
                            if (product.product_id == cartItem.product_id) {
                                cartItem.stock_quantity = product.stock_quantity
                            }
                        }
                    }
                    mCartItemsList = cartList

                    binding.rvCartListItems.layoutManager =
                        LinearLayoutManager(this@CheckoutActivity)
                    binding.rvCartListItems.setHasFixedSize(true)

                    val cartListAdapter =
                        CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
                    binding.rvCartListItems.adapter = cartListAdapter

                    for (item in mCartItemsList) {
                        val availableQuantity = item.stock_quantity.toInt()

                        if (availableQuantity > 0) {
                            val price = item.price.toDouble()
                            val quantity = item.cart_quantity.toInt()
                            mSubTotal += (price * quantity)
                        }
                    }

                    binding.tvSubTotal.text = "$${mSubTotal}"
                    binding.tvShippingCharge.text = "$10.0"

                    if (mSubTotal > 0) {
                        binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

                        mTotalAmount = mSubTotal + 10.0
                        binding.tvTotalAmount.text = "$${mTotalAmount}"
                    } else {
                        binding.llCheckoutPlaceOrder.visibility = View.GONE
                    }
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@CheckoutActivity,
                        cartListResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observePlaceOrderResult() {
        viewModel.placeOrderResult.collectLatest { productsResult ->
            when (productsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    updateAllDetails()
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                }
            }
        }
    }

    private fun updateAllDetails() {
        lifecycleScope.launchWhenResumed {
            viewModel.updateAllDetails(mCartItemsList, mOrderDetails)
        }
        lifecycleScope.launchWhenResumed {
            observeUpdateAllDetailsResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeUpdateAllDetailsResult() {
        viewModel.updateAllDetailsResult.collectLatest { productsResult ->
            when (productsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Your order was placed successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                }
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