package com.amr_medhat_r.dentale_commerceapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityProductDetailsBinding
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.GlideLoader
import com.amr_medhat_r.dentale_commerceapp.viewModels.ProductDetailsViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest

class ProductDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var viewModel: ProductDetailsViewModel
    private var mProduct: Product? = null
    private lateinit var mProductDetails: Product
    private var mUserID: String = ""
    private var isFavProduct = false
    private var favProductId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]
        setListeners()
        getUserID()
        getProduct()
        setProductData(mProduct!!)


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
     * A function to handle user clicks.
     */
    @Suppress("DEPRECATION")
    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
        binding.btnGoToCart.setOnClickListener {
            startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
        }
        binding.ivFavProduct.setOnClickListener {
            if (!isFavProduct) {
                addFavProduct(mUserID, mProduct!!.product_id)
            } else {
                deleteFavProduct(favProductId)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getProduct() {
        if (intent.hasExtra(Constants.PRODUCT)) {
            // Get the user details from intent as parcelableExtra
            mProduct = intent.getParcelableExtra(Constants.PRODUCT)!!
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setProductData(product: Product) {
        mProductDetails = product

        GlideLoader(this@ProductDetailsActivity).loadProductImage(
            product.image,
            binding.ivProductImage
        )
        binding.tvProductTitle.text = product.title
        binding.tvProductCategory.text = product.category
        binding.tvProductPrice.text = "$${product.price}"
        binding.tvProductDescription.text = product.description
        binding.productStockQuantity.text =
            "Stock : ${product.stock_quantity}"

        if (product.stock_quantity.toInt() == 0) {
            binding.btnAddToCart.visibility = View.GONE
            binding.productStockQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)
            binding.productStockQuantity.setTextColor(
                ContextCompat.getColor(this@ProductDetailsActivity, R.color.colorSnackBarError)
            )
        } else {
            lifecycleScope.launchWhenResumed {
                viewModel.checkIfItemExistInCart(product.product_id)
            }
            lifecycleScope.launchWhenResumed {
                observeCheckIfItemExistInCartResult()
            }
        }

        if (!isFavProduct) {
            lifecycleScope.launchWhenResumed {
                viewModel.checkIfFavProduct(product.product_id)
            }
            lifecycleScope.launchWhenResumed {
                observeCheckIfFavProductResult()
            }
        }

    }

    private fun addToCart() {
        val cartItem = CartItem(
            mUserID,
            mProduct!!.product_id,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(this@ProductDetailsActivity)
        addCartItems(cartItem)
    }

    private fun addCartItems(cartItem: CartItem) {
        lifecycleScope.launchWhenResumed {
            viewModel.addCartItems(cartItem)
        }
        lifecycleScope.launchWhenResumed {
            observeAddCartItemsResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeAddCartItemsResult() {
        viewModel.addCartItemsResult.collectLatest { addResult ->
            when (addResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if Add to Cart was successful.
                true -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@ProductDetailsActivity,
                        resources.getString(R.string.success_message_item_added_to_cart),
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.btnAddToCart.visibility = View.GONE
                    binding.btnGoToCart.visibility = View.VISIBLE
                }
                // Handle if Add to Cart was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        this@ProductDetailsActivity,
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
    private suspend fun observeCheckIfItemExistInCartResult() {
        viewModel.checkIfItemExistInCartResult.collectLatest { addResult ->
            when (addResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if Add to Cart was successful.
                true -> {
//                    hideProgressDialog()
                    binding.btnAddToCart.visibility = View.GONE
                    binding.btnGoToCart.visibility = View.VISIBLE
                }
                // Handle if Add to Cart was not successful.
                false -> {
//                    hideProgressDialog()
                    binding.btnAddToCart.visibility = View.VISIBLE
                    binding.btnGoToCart.visibility = View.GONE
                }
            }
        }
    }

    private suspend fun observeCheckIfFavProductResult() {
        viewModel.checkIfFavProductResult.collectLatest { favProduct ->
            when (favProduct[Constants.KEY_RESPONSE_STATE]) {
                // Handle if Add to Cart was successful.
                true -> {
                    isFavProduct = true
                    favProductId = favProduct[Constants.KEY_RESPONSE_VALUE] as String
                    binding.ivFavProduct.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_favorite
                        )
                    )
                }
                // Handle if Add to Cart was not successful.
                false -> {
                }
            }
        }
    }

    private fun addFavProduct(userID: String, productId: String) {
        lifecycleScope.launchWhenResumed {
            viewModel.addFavProduct(userID, productId)
        }
        lifecycleScope.launchWhenResumed {
            observeAddFavProductResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    private suspend fun observeAddFavProductResult() {
        viewModel.addFavProductResult.collectLatest { addResult ->
            when (addResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if Add to Cart was successful.
                true -> {
                    isFavProduct = true

                    lifecycleScope.launchWhenResumed {
                        viewModel.checkIfFavProduct(mProductDetails.product_id)
                    }
                    lifecycleScope.launchWhenResumed {
                        observeCheckIfFavProductResult()
                    }
                }
                // Handle if Add to Cart was not successful.
                false -> {
                    Toast.makeText(
                        this@ProductDetailsActivity,
                        addResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteFavProduct(favoriteProductId: String) {
        lifecycleScope.launchWhenResumed {
            viewModel.deleteFavProduct(favoriteProductId)
        }
        lifecycleScope.launchWhenResumed {
            observeDeleteFavProductResult()
        }
    }

    private suspend fun observeDeleteFavProductResult() {
        viewModel.deleteFavProductResult.collectLatest { deleteResult ->
            when (deleteResult[Constants.KEY_RESPONSE_STATE]) {
                true -> {
                    isFavProduct = false
                    binding.ivFavProduct.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_not_favorite
                        )
                    )
                }
                false -> {
                    Toast.makeText(
                        this@ProductDetailsActivity,
                        deleteResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_SHORT
                    ).show()
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