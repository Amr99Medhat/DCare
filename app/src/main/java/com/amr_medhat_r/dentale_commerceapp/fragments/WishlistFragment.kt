package com.amr_medhat_r.dentale_commerceapp.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.amr_medhat_r.dentale_commerceapp.activities.ProductDetailsActivity
import com.amr_medhat_r.dentale_commerceapp.adapters.DashboardItemsListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseFragment
import com.amr_medhat_r.dentale_commerceapp.databinding.FragmentWishlistBinding
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemDashboardLayoutBinding
import com.amr_medhat_r.dentale_commerceapp.models.FavoriteProduct
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.SpacesItemDecoration
import com.amr_medhat_r.dentale_commerceapp.viewModels.WishlistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WishlistFragment : BaseFragment() {
    private lateinit var binding: FragmentWishlistBinding
    private lateinit var viewModel: WishlistViewModel
    private lateinit var mProductsList: ArrayList<Product>
    private var decoration: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[WishlistViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistBinding.inflate(inflater)
        return binding.root
    }

    private fun getProductList() {
        lifecycleScope.launch(Dispatchers.Main) {
            showProgressDialog(requireContext())
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
    private suspend fun observeGetAllProductsListResult() {
        viewModel.getAllProductsListResult.collectLatest { productsResult ->
            when (productsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    mProductsList =
                        productsResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<Product>

                    getFavList()

                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    binding.rvMyWishlistItems.visibility = View.GONE
                    binding.tvNoWishlistFound.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getFavList() {
        lifecycleScope.launchWhenResumed {
            viewModel.getFavList()
        }
        lifecycleScope.launchWhenResumed {
            observeGetFavListResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeGetFavListResult() {
        viewModel.getFavListResult.collectLatest { FavListResult ->
            when (FavListResult[Constants.KEY_RESPONSE_STATE]) {
                true -> {
                    hideProgressDialog()
                    val userFavProducts: ArrayList<Product> = ArrayList()
                    val favList =
                        FavListResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<FavoriteProduct>

                    for (product in mProductsList) {
                        for (favItem in favList) {
                            if (product.product_id == favItem.product_id) {
                                userFavProducts.add(product)
                            }
                        }
                    }

                    if (userFavProducts.size > 0) {

                        binding.rvMyWishlistItems.visibility = View.VISIBLE
                        binding.tvNoWishlistFound.visibility = View.GONE

                        binding.rvMyWishlistItems.layoutManager =
                            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        binding.rvMyWishlistItems.setHasFixedSize(true)
                        val dashboardItemsListAdapter =
                            DashboardItemsListAdapter(requireContext(), userFavProducts)
                        // If decoration = 0 so it's passing new values only.
                        if (!decoration) {
                            decoration = true
                            binding.rvMyWishlistItems.addItemDecoration(
                                SpacesItemDecoration(
                                    30
                                )
                            )
                        }
                        binding.rvMyWishlistItems.adapter = dashboardItemsListAdapter

                        dashboardItemsListAdapter.setOnClickListener(object :
                            DashboardItemsListAdapter.OnClickListener {
                            override fun onClick(
                                position: Int,
                                product: Product,
                                viewBinding: ViewBinding?
                            ) {
                                val intent =
                                    Intent(requireContext(), ProductDetailsActivity::class.java)
                                intent.putExtra(Constants.PRODUCT, product)
                                val view = viewBinding as ItemDashboardLayoutBinding
                                val pairs: Array<android.util.Pair<View, String>?> = arrayOfNulls(3)
                                pairs[0] =
                                    android.util.Pair(
                                        view.ivProductImage,
                                        Constants.IMAGE_TRANSITION
                                    )
                                pairs[1] =
                                    android.util.Pair(
                                        view.tvProductTitle,
                                        Constants.TITLE_TRANSITION
                                    )
                                pairs[2] =
                                    android.util.Pair(
                                        view.tvProductPrice,
                                        Constants.PRICE_TRANSITION
                                    )

                                val options =
                                    ActivityOptions.makeSceneTransitionAnimation(
                                        requireActivity(),
                                        *pairs
                                    )
                                startActivity(intent, options.toBundle())
                            }
                        })

                    } else {
                        binding.rvMyWishlistItems.visibility = View.GONE
                        binding.tvNoWishlistFound.visibility = View.VISIBLE
                    }
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    Toast.makeText(
                        requireContext(),
                        FavListResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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