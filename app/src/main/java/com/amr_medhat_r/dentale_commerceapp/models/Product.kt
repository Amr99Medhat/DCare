package com.amr_medhat_r.dentale_commerceapp.models

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val title: String = "",
    val price: String = "",
    val description: String = "",
    var stock_quantity: String = "",
    val image: String = "",
    var product_id: String = "",
    val category: String = "",
) : Parcelable