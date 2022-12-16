package com.amr_medhat_r.dentale_commerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.activities.CartListActivity
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemCartListBinding
import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.amr_medhat_r.dentale_commerceapp.utilities.GlideLoader

class CartItemsListAdapter(
    private val context: Context,
    private val list: ArrayList<CartItem>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<CartItemsListAdapter.CartItemsListHolder>() {

    inner class CartItemsListHolder(var itemCartListBinding: ItemCartListBinding) :
        RecyclerView.ViewHolder(itemCartListBinding.root) {
        fun setLabData(item: CartItem) {
            GlideLoader(context).loadProductImage(
                item.image,
                itemCartListBinding.ivCartItemImage
            )
            itemCartListBinding.tvCartItemTitle.text = item.title
            itemCartListBinding.tvCartItemPrice.text = "$${item.price}"
            itemCartListBinding.tvCartQuantity.text = item.cart_quantity

            if (item.cart_quantity == "0") {
                itemCartListBinding.ibRemoveCartItem.visibility = View.GONE
                itemCartListBinding.ibAddCartItem.visibility = View.GONE

                if (updateCartItems) {
                    itemCartListBinding.ibDeleteCartItem.visibility = View.VISIBLE
                } else {
                    itemCartListBinding.ibDeleteCartItem.visibility = View.GONE
                }
                itemCartListBinding.tvCartQuantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                itemCartListBinding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            } else {

                if (updateCartItems){
                    itemCartListBinding.ibRemoveCartItem.visibility = View.VISIBLE
                    itemCartListBinding.ibAddCartItem.visibility = View.VISIBLE
                    itemCartListBinding.ibDeleteCartItem.visibility = View.VISIBLE

                }else{
                    itemCartListBinding.ibRemoveCartItem.visibility = View.GONE
                    itemCartListBinding.ibAddCartItem.visibility = View.GONE
                    itemCartListBinding.ibDeleteCartItem.visibility = View.GONE
                }

                itemCartListBinding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_lightGray
                    )
                )
            }
            itemCartListBinding.ibDeleteCartItem.setOnClickListener {
                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context)
                        context.hideProgressDialog()
                    }
                }
                FireStoreClass().removeItemFromCart(context, item.id)
            }

            itemCartListBinding.ibRemoveCartItem.setOnClickListener {
                if (item.cart_quantity == "1") {
                    FireStoreClass().removeItemFromCart(context, item.id)
                } else {
                    val cartQuantity: Int = item.cart_quantity.toInt()
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // show the progress dialog
                    if (context is CartListActivity) {
                        context.showProgressDialog(context)
                    }
                    FireStoreClass().updateMyCart(context, item.id, itemHashMap)
                }
            }

            itemCartListBinding.ibAddCartItem.setOnClickListener {
                val cartQuantity: Int = item.cart_quantity.toInt()
                if (cartQuantity < item.stock_quantity.toInt()) {
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressDialog(context)
                    }
                    FireStoreClass().updateMyCart(context, item.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                item.stock_quantity
                            ),
                            true
                        )
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsListHolder {
        val binding =
            ItemCartListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemsListHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemsListHolder, position: Int) {
        holder.setLabData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}