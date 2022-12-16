package com.amr_medhat_r.dentale_commerceapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: String = "",
    val gender: String = "",
    val profileCompleted: Int = 0
) : Parcelable