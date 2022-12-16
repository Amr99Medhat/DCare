package com.amr_medhat_r.dentale_commerceapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr_medhat_r.dentale_commerceapp.adapters.MyOrdersListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseFragment
import com.amr_medhat_r.dentale_commerceapp.databinding.FragmentOrdersBinding
import com.amr_medhat_r.dentale_commerceapp.models.Order
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.viewModels.OrdersFragmentViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest

class OrdersFragment : BaseFragment() {
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var viewModel: OrdersFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[OrdersFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }


    private fun getMyOrdersList() {
        showProgressDialog(requireContext())
        lifecycleScope.launchWhenResumed {
            viewModel.getMyOrdersList()
        }
        lifecycleScope.launchWhenResumed {
            observeGetMyOrdersListResult()
        }
    }

    /**
     * A function to observe on results from viewModel.
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun observeGetMyOrdersListResult() {
        viewModel.getMyOrdersListResult.collectLatest { OrdersListResult ->
            when (OrdersListResult[Constants.KEY_RESPONSE_STATE]) {
                // Handle if login was successful.
                true -> {
                    hideProgressDialog()
                    val orderList =
                        OrdersListResult[Constants.KEY_RESPONSE_VALUE] as ArrayList<Order>

                    if (orderList.size > 0) {
                        binding.rvMyOrderItems.visibility = View.VISIBLE
                        binding.tvNoOrdersFound.visibility = View.GONE

                        binding.rvMyOrderItems.layoutManager =
                            LinearLayoutManager(activity)
                        binding.rvMyOrderItems.setHasFixedSize(true)

                        val myOrderAdapter = MyOrdersListAdapter(requireActivity(), orderList)
                        binding.rvMyOrderItems.adapter = myOrderAdapter
                    } else {
                        binding.rvMyOrderItems.visibility = View.GONE
                        binding.tvNoOrdersFound.visibility = View.VISIBLE
                    }
                }
                // Handle if login was not successful.
                false -> {
                    hideProgressDialog()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getMyOrdersList()
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