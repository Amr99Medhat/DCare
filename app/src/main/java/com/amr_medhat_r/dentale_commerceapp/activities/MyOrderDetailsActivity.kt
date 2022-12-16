package com.amr_medhat_r.dentale_commerceapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.adapters.CartItemsListAdapter
import com.amr_medhat_r.dentale_commerceapp.base.BaseActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ActivityMyOrderDetailsBinding
import com.amr_medhat_r.dentale_commerceapp.models.Order
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityMyOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()

        var myOrderDetails = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails = intent.getParcelableExtra(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }

        setupUI(myOrderDetails)
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

    @SuppressLint("SetTextI18n")
    private fun setupUI(orderDetails: Order) {
        binding.tvOrderId.text = orderDetails.title

        val dateFormat = "dd MMMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calender = Calendar.getInstance()
        calender.timeInMillis = orderDetails.order_datetime
        val orderDateTime = formatter.format(calender.time)
        binding.tvOrderDate.text = orderDateTime

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)

        when {
            diffInHours < 1 -> {
                binding.tvStatusPending.text =
                    resources.getString(R.string.order_status_pending)
                binding.tvStatusPending.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.color_Primary
                    )
                )
            }

            diffInHours < 2 -> {
                binding.tvStatusPending.text =
                    resources.getString(R.string.order_status_in_process)
                binding.tvStatusPending.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }

            else -> {
                binding.tvStatusPending.text =
                    resources.getString(R.string.order_status_delivered)
                binding.tvStatusPending.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }

        binding.rvMyOrderListItems.layoutManager =
            LinearLayoutManager(this@MyOrderDetailsActivity)
        binding.rvMyOrderListItems.setHasFixedSize(true)

        val cartListAdapter =
            CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        binding.rvMyOrderListItems.adapter = cartListAdapter

        binding.tvOrderDetailsAddressType.text = orderDetails.address.type
        binding.tvOrderDetailsFullName.text = orderDetails.address.name
        binding.tvOrderDetailsAddress.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        binding.tvOrderDetailsAdditionalNote.text =
            orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            binding.tvOrderDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvOrderDetailsOtherDetails.text =
                orderDetails.address.otherDetails
        } else {
            binding.tvOrderDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvOrderDetailsMobileNumber.text =
            orderDetails.address.mobileNumber

        binding.tvSubTotal.text = orderDetails.sub_total_amount
        binding.tvShippingCharge.text = orderDetails.shipping_charge
        binding.tvTotalAmount.text = orderDetails.total_amount
    }
}