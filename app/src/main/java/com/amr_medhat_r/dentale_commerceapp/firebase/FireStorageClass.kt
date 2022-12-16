package com.amr_medhat_r.dentale_commerceapp.firebase

import android.app.Activity
import android.net.Uri
import com.amr_medhat_r.dentale_commerceapp.utilities.SupportClass
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class FireStorageClass {

    fun uploadImageToCloudStorage(
        activity: Activity,
        imageFileURI: Uri?,
        imageType: String
    ): UploadTask {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + SupportClass.getFileExtension(
                activity,
                imageFileURI
            )
        )
        return sRef.putFile(imageFileURI!!)
    }
}