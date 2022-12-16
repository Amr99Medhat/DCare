package com.amr_medhat_r.dentale_commerceapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amr_medhat_r.dentale_commerceapp.activities.MyOrderDetailsActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemOrderLayoutBinding
import com.amr_medhat_r.dentale_commerceapp.models.Order
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.GlideLoader

class MyOrdersListAdapter(
    private val context: Context,
    private val orders: ArrayList<Order>
) : RecyclerView.Adapter<MyOrdersListAdapter.MyOrdersViewHolder>() {

    inner class MyOrdersViewHolder(var itemOrderLayoutBinding: ItemOrderLayoutBinding) :
        RecyclerView.ViewHolder(itemOrderLayoutBinding.root) {
        @SuppressLint("SetTextI18n")
        fun setLabData(order: Order) {
            GlideLoader(context).loadProductImage(
                order.image,
                itemOrderLayoutBinding.ivOrderItemImage
            )
            itemOrderLayoutBinding.tvOrderItemTitle.text = order.title
            itemOrderLayoutBinding.tvOrderItemPrice.text = "$${order.total_amount}"

            itemOrderLayoutBinding.root.setOnClickListener {
                val intent = Intent(context, MyOrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, order)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        val binding =
            ItemOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyOrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        holder.setLabData(orders[position])

    }

    override fun getItemCount(): Int {
        return orders.size
    }
}