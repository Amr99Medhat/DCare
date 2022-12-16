package com.amr_medhat_r.dentale_commerceapp.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.amr_medhat_r.dentale_commerceapp.adapters.DashboardItemsListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivitySearchBinding
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemDashboardLayoutBinding
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.SpacesItemDecoration
import kotlinx.coroutines.cancel
import java.util.*

class SearchActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var mProductList: ArrayList<Product> = ArrayList()
    private lateinit var mDashboardItemsListAdapter: DashboardItemsListAdapter
    private var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getProducts()
        setListeners()
        observeOnSearchText()
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
    }

    @Suppress("DEPRECATION")
    private fun getProducts() {
        if (intent.hasExtra(Constants.PRODUCTS)) {
            // Get the user details from intent as parcelableExtra
            mProductList = intent.getParcelableArrayListExtra(Constants.PRODUCTS)!!
            setupAdapter(mProductList, 30)
        }
    }

    private fun observeOnSearchText() {
        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timer?.cancel()
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNotEmpty()) {
                    val newList: ArrayList<Product> = ArrayList()
                    timer = Timer()
                    timer!!.schedule(object : TimerTask() {
                        override fun run() {
                            lifecycleScope.launchWhenResumed {
                                for (i in mProductList) {
                                    if (i.title.contains(s.toString().uppercase().trim())) {
                                        newList.add(i)
                                    }
                                }
                                setupAdapter(newList, 0)

                            }
                        }
                    }, 800)

                } else {
                    binding.rvSearchProducts.visibility = View.GONE
                }
            }
        })
        binding.inputSearch.requestFocus()

    }

    private fun setupAdapter(products: ArrayList<Product>, decoration: Int) {
        if (products.size > 0) {
            binding.rvSearchProducts.visibility = View.VISIBLE
            binding.rvSearchProducts.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.rvSearchProducts.setHasFixedSize(true)
            mDashboardItemsListAdapter = DashboardItemsListAdapter(this@SearchActivity, products)
            // If decoration = 0 so it's passing new values only.
            if (decoration != 0) {
                binding.rvSearchProducts.addItemDecoration(
                    SpacesItemDecoration(
                        decoration
                    )
                )
            }
            binding.rvSearchProducts.adapter = mDashboardItemsListAdapter

            mDashboardItemsListAdapter.setOnClickListener(object :
                DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product, viewBinding: ViewBinding?) {
                    val intent = Intent(this@SearchActivity, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.PRODUCT, product)
                    val view = viewBinding as ItemDashboardLayoutBinding
                    val pairs: Array<android.util.Pair<View, String>?> = arrayOfNulls(3)
                    pairs[0] =
                        android.util.Pair(view.ivProductImage, Constants.IMAGE_TRANSITION)
                    pairs[1] =
                        android.util.Pair(view.tvProductTitle, Constants.TITLE_TRANSITION)
                    pairs[2] =
                        android.util.Pair(view.tvProductPrice, Constants.PRICE_TRANSITION)

                    val options =
                        ActivityOptions.makeSceneTransitionAnimation(this@SearchActivity, *pairs)
                    startActivity(intent, options.toBundle())
                }
            })
        } else {
            binding.rvSearchProducts.visibility = View.GONE
        }
    }

    /**
     * Override onDestroy() to close all resources after leaving, to avoid memory leak.
     */
    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
        lifecycleScope.cancel()
    }

}