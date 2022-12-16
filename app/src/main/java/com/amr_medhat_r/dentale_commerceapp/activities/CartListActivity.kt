package com.amr_medhat_r.dentale_commerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.adapters.CartItemsListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityCartListBinding
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.viewModels.CartListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartListActivity : BaseActivity() {
    private lateinit var binding: ActivityCartListBinding
    private lateinit var viewModel: CartListViewModel
    private var mProductsList: ArrayList<Product>? = null
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CartListViewModel::class.java]
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
        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
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

    private fun getProductList() {
        lifecycleScope.launch(Dispatchers.Main) {
            showProgressDialog(this@CartListActivity)
        }
        lifecycleScope.launchWhenResumed {
            viewModel.getAllProductsList()
        }
        lifecycleScope.launchWhenResumed {
            observeGetAllProductsListResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeGetCartItemsListResult() {
        viewModel.getCartListResult.collectLatest { cartListResult ->
            when (cartListResult[Constants.KEY_RESPONSE_STATE]) {
                true -> {
                    hideProgressDialog()
                    val cartList =
                        cartListResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<CartItem>

                    for (product in mProductsList!!) {
                        for (cartItem in cartList) {
                            if (product.product_id == cartItem.product_id) {
                                cartItem.stock_quantity = product.stock_quantity
                                if (product.stock_quantity.toInt() == 0) {
                                    cartItem.cart_quantity = product.stock_quantity
                                }
                            }
                        }
                    }

                    mCartListItems = cartList

                    if (mCartListItems.size > 0) {
                        binding.rvCartItemsList.visibility = View.VISIBLE
                        binding.llCheckout.visibility = View.VISIBLE
                        binding.tvNoCartItemFound.visibility = View.GONE


                        binding.rvCartItemsList.layoutManager =
                            LinearLayoutManager(this@CartListActivity)
                        binding.rvCartItemsList.setHasFixedSize(true)
                        val cartListAdapter =
                            CartItemsListAdapter(this@CartListActivity, cartList, true)
                        binding.rvCartItemsList.adapter = cartListAdapter
                        var subTotal = 0.0

                        for (item in mCartListItems) {
                            val availabilityQuantity = item.stock_quantity.toInt()
                            if (availabilityQuantity > 0) {
                                val price = item.price.toDouble()
                                val quantity = item.cart_quantity.toInt()
                                subTotal += (price * quantity)
                            }
                        }
                        binding.tvSubTotal.text = "$${subTotal}"
                        binding.tvShippingCharge.text =
                            "$10.0" // TODO change shipping charge logic

                        if (subTotal > 0) {
                            binding.llCheckout.visibility = View.VISIBLE

                            val total = subTotal + 10 // TODO change logic here
                            binding.tvTotalAmount.text = "$${total}"
                        } else {
                            binding.llCheckout.visibility = View.GONE

                        }
                    } else {
                        binding.rvCartItemsList.visibility = View.GONE
                        binding.llCheckout.visibility = View.GONE
                        binding.tvNoCartItemFound.visibility = View.VISIBLE
                    }
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@CartListActivity,
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
    private suspend fun observeGetAllProductsListResult() {
        viewModel.getAllProductsListResult.collectLatest { productsResult ->
            when (productsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    hideProgressDialog()
                    mProductsList =
                        productsResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<Product>

                    if (mProductsList != null) {

                        getCartItemsList()
                    }

                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@CartListActivity,
                        productsResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        lifecycleScope.launchWhenResumed {
            getCartItemsList()
        }
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        lifecycleScope.launchWhenResumed {
            getCartItemsList()
        }
    }

    override fun onResume() {
        super.onResume()

        getProductList()
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