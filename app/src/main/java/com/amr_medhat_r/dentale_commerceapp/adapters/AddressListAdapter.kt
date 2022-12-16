package com.amr_medhat_r.dentale_commerceapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amr_medhat_r.dentale_commerceapp.activities.AddEditAddressActivity
import com.amr_medhat_r.dentale_commerceapp.activities.CheckoutActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemAddressLayoutBinding
import com.amr_medhat_r.dentale_commerceapp.models.Address
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants

class AddressListAdapter(
    private val context: Context,
    private val list: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<AddressListAdapter.AddressItemsListHolder>() {

    inner class AddressItemsListHolder(var itemAddressLayoutBinding: ItemAddressLayoutBinding) :
        RecyclerView.ViewHolder(itemAddressLayoutBinding.root) {
        @SuppressLint("SetTextI18n")
        fun setLabData(address: Address) {
            itemAddressLayoutBinding.tvAddressFullName.text = address.name
            itemAddressLayoutBinding.tvAddressType.text = address.type
            itemAddressLayoutBinding.tvAddressDetails.text =
                "${address.address}, ${address.zipCode}"
            itemAddressLayoutBinding.tvAddressMobileNumber.text = address.mobileNumber

            if (selectAddress) {
                itemAddressLayoutBinding.root.setOnClickListener {
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, address)
                    context.startActivity(intent)
                }
            }
        }
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressItemsListHolder {
        val binding =
            ItemAddressLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressItemsListHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressItemsListHolder, position: Int) {
        holder.setLabData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}