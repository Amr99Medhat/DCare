package com.amr_medhat_r.dentale_commerceapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemDashboardLayoutBinding
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.GlideLoader

class DashboardItemsListAdapter(
    private val context: Context,
    private val mProducts: ArrayList<Product>
) : RecyclerView.Adapter<DashboardItemsListAdapter.AllProductsViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class AllProductsViewHolder(var itemDashboardLayoutBinding: ItemDashboardLayoutBinding) :
        RecyclerView.ViewHolder(itemDashboardLayoutBinding.root) {
        @SuppressLint("SetTextI18n")
        fun setLabData(product: Product) {
            GlideLoader(context).loadProductImage(
                product.image,
                itemDashboardLayoutBinding.ivProductImage
            )
            itemDashboardLayoutBinding.tvProductTitle.text = product.title
            itemDashboardLayoutBinding.tvProductPrice.text = "$${product.price}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProductsViewHolder {
        val binding =
            ItemDashboardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllProductsViewHolder, position: Int) {
        holder.setLabData(mProducts[position])
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(
                    position,
                    mProducts[position],
                    holder.itemDashboardLayoutBinding
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return mProducts.size
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Product, viewBinding: ViewBinding? = null)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}