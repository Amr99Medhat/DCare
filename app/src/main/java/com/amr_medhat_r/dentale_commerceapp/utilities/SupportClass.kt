package com.amr_medhat_r.dentale_commerceapp.utilities

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import android.webkit.MimeTypeMap

class SupportClass {

    companion object {

        /**
         * A function to hide the notification bar.
         */
        fun hideStatusBar(activity: Activity) {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }

        fun getFileExtension(activity: Activity, uri: Uri?): String? {
            /*MineTypeMap : Two-way map that maps MIME-types to file extensions and vice verse.

            getSingleton() : Get the singleton instance of MineTypeMap.

            getExtensionFromMineType : Return the registered extension for the given MIME type.

            contentResolver.getType : Return the MIME type of the given content URL.*/

            return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
        }
    }
}