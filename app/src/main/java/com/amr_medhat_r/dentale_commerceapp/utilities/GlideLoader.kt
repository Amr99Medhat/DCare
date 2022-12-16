package com.amr_medhat_r.dentale_commerceapp.utilities

import android.content.Context
import android.widget.ImageView
import com.amr_medhat_r.dentale_commerceapp.R
import com.bumptech.glide.Glide

class GlideLoader(val context: Context) {

    fun loadUserImage(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image) // URI of the image
                .placeholder(R.drawable.ic_doctor_placeholder) // A default place holder if image is failed load.
                .into(imageView) // The view in which the image will be loaded.
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun loadProductImage(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image) // URI of the image
                .into(imageView) // The view in which the image will be loaded.
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}