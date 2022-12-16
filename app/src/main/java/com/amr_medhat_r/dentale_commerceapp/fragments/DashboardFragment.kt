package com.amr_medhat_r.dentale_commerceapp.fragments

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.activities.CartListActivity
import com.amr_medhat_r.dentale_commerceapp.activities.ProductDetailsActivity
import com.amr_medhat_r.dentale_commerceapp.activities.SearchActivity
import com.amr_medhat_r.dentale_commerceapp.activities.SettingsActivity
import com.amr_medhat_r.dentale_commerceapp.adapters.DashboardItemsListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseFragment
import com.amr_medhat_r.dentale_commerceapp.databinding.FragmentDashboardBinding
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemCategoriesFilterLayoutBinding
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemDashboardLayoutBinding
import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.models.User
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.GlideLoader
import com.amr_medhat_r.dentale_commerceapp.utilities.SpacesItemDecoration
import com.amr_medhat_r.dentale_commerceapp.viewModels.DashboardFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardFragment : BaseFragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardFragmentViewModel
    private lateinit var mItemCategoriesFilterLayoutBinding: ItemCategoriesFilterLayoutBinding
    private lateinit var mAlert: AlertDialog
    private lateinit var mAdapterProducts: DashboardItemsListAdapter
    private lateinit var mProductsList: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this)[DashboardFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Layouts inflation
        binding = FragmentDashboardBinding.inflate(inflater)
        mItemCategoriesFilterLayoutBinding =
            ItemCategoriesFilterLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
            showProgressDialog(requireContext())
        }
        getUserDetails()
        getDashboardItemsList()
        setListener()

    }

    private fun setListener() {
        binding.ivSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        binding.ivCategoryFilter.setOnClickListener {
            showDialog(requireContext())
        }
        binding.llCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartListActivity::class.java))
        }
        binding.ivSearch.setOnClickListener {
            if (mProductsList.size > 0) {
                val intent = Intent(Intent(requireContext(), SearchActivity::class.java))
                intent.putExtra(Constants.PRODUCTS, mProductsList)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
            }

        }
        mItemCategoriesFilterLayoutBinding.tvAllCategories.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                newList.add(i)
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text = resources.getString(R.string.dental)
        }
        mItemCategoriesFilterLayoutBinding.tvClinicSetup.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                if (i.category == resources.getString(R.string.clinic_set_up)) {
                    newList.add(i)
                }
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text =
                resources.getString(R.string.clinic_set_up)
        }
        mItemCategoriesFilterLayoutBinding.tvSmallEquipments.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                if (i.category == resources.getString(R.string.small_equipments)) {
                    newList.add(i)
                }
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text =
                resources.getString(R.string.small_equipments)
        }
        mItemCategoriesFilterLayoutBinding.tvDentalConsumables.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                if (i.category == resources.getString(R.string.dental_consumables)) {
                    newList.add(i)
                }
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text =
                resources.getString(R.string.dental_consumables)
        }
        mItemCategoriesFilterLayoutBinding.tvLabProducts.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                if (i.category == resources.getString(R.string.lab_products)) {
                    newList.add(i)
                }
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text =
                resources.getString(R.string.lab_products)
        }
        mItemCategoriesFilterLayoutBinding.tvDentalInstruments.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                if (i.category == resources.getString(R.string.dental_instruments)) {
                    newList.add(i)
                }
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text =
                resources.getString(R.string.dental_instruments)
        }
        mItemCategoriesFilterLayoutBinding.tvDentalTraining.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                if (i.category == resources.getString(R.string.dental_training)) {
                    newList.add(i)
                }
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text =
                resources.getString(R.string.dental_training)
        }

        mItemCategoriesFilterLayoutBinding.tvDentalPortable.setOnClickListener {
            mAlert.dismiss()
            val newList: ArrayList<Product> = ArrayList()
            for (i in mProductsList) {
                if (i.category == resources.getString(R.string.dental_portable)) {
                    newList.add(i)
                }
            }
            setupAdapter(newList, 0)
            binding.tvCategoryName.text =
                resources.getString(R.string.dental_portable)
        }

    }

    private fun showDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        if (mItemCategoriesFilterLayoutBinding.root.parent != null) {
            (mItemCategoriesFilterLayoutBinding.root.parent as ViewGroup).removeView(
                mItemCategoriesFilterLayoutBinding.root
            )
        }
        builder.setView(mItemCategoriesFilterLayoutBinding.root)
        mAlert = builder.create()

        if (mAlert.window != null) {
            mAlert.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        mAlert.show()
    }

    private fun getDashboardItemsList() {
        lifecycleScope.launchWhenResumed {
            viewModel.getDashboardItemsList()
        }
        lifecycleScope.launchWhenResumed {
            observeDashboardItemsListResult()
        }
    }

    private fun getUserDetails() {
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
    private suspend fun observeUserDetailsResult() {
        viewModel.getUserDetailsResult.collectLatest { userDetailsResult ->
            when (userDetailsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    val mUserDetails = userDetailsResult[Constants.KEY_RESPONSE_VALUE] as User

                    // Set the User's Image.
                    GlideLoader(requireContext()).loadUserImage(
                        mUserDetails.image,
                        binding.ivUserPhoto
                    )
                }
                // Handle if login was not successful.
                false -> {
                    Toast.makeText(
                        requireContext(),
                        userDetailsResult[Constants.KEY_RESPONSE_VALUE].toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeDashboardItemsListResult() {
        viewModel.getDashboardItemsListResult.collectLatest { productsResult ->
            when (productsResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    hideProgressDialog()
                    mProductsList =
                        productsResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<Product>

                    setupAdapter(mProductsList, 30)

                    getCartItemsList()

                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                    binding.rvDashboardItems.visibility = View.GONE
                    binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
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
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeGetCartItemsListResult() {
        viewModel.getCartListResult.collectLatest { cartListResult ->
            when (cartListResult[Constants.KEY_RESPONSE_STATE]) {
                true -> {
                    val cartList =
                        cartListResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<CartItem>

                    if (cartList.size > 0) {
                        binding.ivCartNumber.text = cartList.size.toString()
                    } else {
                        binding.ivCartNumber.text = "0"
                    }
                }
                else -> {
                    binding.ivCartNumber.text = "0"
                }
            }
        }
    }

    private fun setupAdapter(products: ArrayList<Product>, decoration: Int) {
        if (products.size > 0) {
            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE

            binding.rvDashboardItems.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.rvDashboardItems.setHasFixedSize(true)
            mAdapterProducts = DashboardItemsListAdapter(requireActivity(), products)

            // If decoration = 0 so it's passing new values only.
            if (decoration != 0) {
                binding.rvDashboardItems.addItemDecoration(
                    SpacesItemDecoration(
                        decoration
                    )
                )
            }
            binding.rvDashboardItems.adapter = mAdapterProducts

            mAdapterProducts.setOnClickListener(object : DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product, viewBinding: ViewBinding?) {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.PRODUCT, product)
                    intent.putExtra(
                        Constants.EXTRA_PRODUCT_OWNER_ID,
                        FirebaseAuthClass().getCurrentUserID()
                    )
                    val view = viewBinding as ItemDashboardLayoutBinding
                    val pairs: Array<android.util.Pair<View, String>?> = arrayOfNulls(3)
                    pairs[0] =
                        android.util.Pair(view.ivProductImage, Constants.IMAGE_TRANSITION)
                    pairs[1] =
                        android.util.Pair(view.tvProductTitle, Constants.TITLE_TRANSITION)
                    pairs[2] =
                        android.util.Pair(view.tvProductPrice, Constants.PRICE_TRANSITION)

                    val options =
                        ActivityOptions.makeSceneTransitionAnimation(context as Activity?, *pairs)
                    startActivity(intent, options.toBundle())
                }
            })
        } else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
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