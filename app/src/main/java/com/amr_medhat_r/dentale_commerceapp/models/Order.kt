package com.amr_medhat_r.dentale_commerceapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val user_id: String = "",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address = Address(),
    val title: String = "",
    val image: String = "",
    var sub_total_amount: String = "",
    var shipping_charge: String = "",
    var total_amount: String = "",
    val order_datetime: Long = 55L,
    var id: String = ""
) : Parcelable