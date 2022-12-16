package com.amr_medhat_r.dentale_commerceapp.models

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoriteProduct(
    val user_id: String = "",
    var product_id: String = ""
) : Parcelable