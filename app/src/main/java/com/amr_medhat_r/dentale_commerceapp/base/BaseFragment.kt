package com.amr_medhat_r.dentale_commerceapp.base

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amr_medhat_r.dentale_commerceapp.R
import com.amr_medhat_r.dentale_commerceapp.databinding.ItemLoadingProgressBinding

open class BaseFragment : Fragment() {
    private lateinit var mItemLoadingProgressBinding: ItemLoadingProgressBinding
    private lateinit var mAlert: AlertDialog

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
}