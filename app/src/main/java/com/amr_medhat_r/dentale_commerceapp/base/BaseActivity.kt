package com.amr_medhat_r.dentale_commerceapp.base

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemLoadingProgressBinding
import com.amr_medhat_r.dentale_commerceapp.utilities.SupportClass
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    private lateinit var mItemLoadingProgressBinding: ItemLoadingProgressBinding
    private lateinit var mAlert: AlertDialog
    private var doubleBackToExitPressedOnce = false

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()
    }

    fun showProgressDialog(context: Context) {
        mItemLoadingProgressBinding = ItemLoadingProgressBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        if (mItemLoadingProgressBinding.root.parent != null) {
            (mItemLoadingProgressBinding.root.parent as ViewGroup).removeView(
                mItemLoadingProgressBinding.root
            )
        }
        builder.setView(mItemLoadingProgressBinding.root)
        mAlert = builder.create()

        if (mAlert.window != null) {
            mAlert.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        mAlert.setCancelable(false)
        mAlert.setCanceledOnTouchOutside(false)
        mAlert.show()
    }

    fun hideProgressDialog() {
        mAlert.dismiss()
    }

    @Suppress("DEPRECATION")
    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            this.doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        // Hide the StatusBar.
        SupportClass.hideStatusBar(this)

    }
}