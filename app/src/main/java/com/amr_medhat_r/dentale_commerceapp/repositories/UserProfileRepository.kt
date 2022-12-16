package com.amr_medhat_r.dentale_commerceapp.repositories

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.amr_medhat_r.dentale_commerceapp.firebase.FireStorageClass
import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class UserProfileRepository {

    private var mUpdateUserProfileDataResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mUploadImageToCloudStorageResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    fun pickImage(resultLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        resultLauncher.launch(intent)
    }

    suspend fun updateUserProfileData(userHashMap: HashMap<String, Any>): MutableStateFlow<HashMap<String, Any>> {
        mUpdateUserProfileDataResult = MutableStateFlow(HashMap())
        // update User Profile Data with FireStore
        FireStoreClass().updateUserProfileData(userHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateUserProfileDataResponse = HashMap<String, Any>()
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mUpdateUserProfileDataResult.emit(
                            updateUserProfileDataResponse
                        )
                    }
                } else {
                    // If the reset is not successful then show error message.
                    val updateUserProfileDataResponse = HashMap<String, Any>()
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = false
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mUpdateUserProfileDataResult.emit(
                            updateUserProfileDataResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val updateUserProfileDataResponse = HashMap<String, Any>()
                updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = false
                updateUserProfileDataResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mUpdateUserProfileDataResult.emit(
                        updateUserProfileDataResponse
                    )
                }
            }
        return mUpdateUserProfileDataResult
    }

    suspend fun uploadImageToCloudStorage(
        activity: Activity,
        imageFileURI: Uri?,
        imageType: String
    ): MutableStateFlow<HashMap<String, Any>> {
        mUploadImageToCloudStorageResult = MutableStateFlow(HashMap())
        // update User Profile Data with FireStore
        FireStorageClass().uploadImageToCloudStorage(activity, imageFileURI, imageType)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the downloadable url from the task snapshot
                    task.result.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            val uploadImageToCloudStorageResponse = HashMap<String, Any>()
                            uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_STATE] = true
                            uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_VALUE] = uri.toString()
                            runBlocking {
                                mUploadImageToCloudStorageResult.emit(
                                    uploadImageToCloudStorageResponse
                                )
                            }
                        }
                        .addOnFailureListener {
                            val uploadImageToCloudStorageResponse = HashMap<String, Any>()
                            uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_STATE] = false
                            uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                            runBlocking {
                                mUploadImageToCloudStorageResult.emit(
                                    uploadImageToCloudStorageResponse
                                )
                            }
                        }
                } else {
                    // If the reset is not successful then show error message.
                    val uploadImageToCloudStorageResponse = HashMap<String, Any>()
                    uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_STATE] = false
                    uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mUploadImageToCloudStorageResult.emit(
                            uploadImageToCloudStorageResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val uploadImageToCloudStorageResponse = HashMap<String, Any>()
                uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_STATE] = false
                uploadImageToCloudStorageResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mUploadImageToCloudStorageResult.emit(
                        uploadImageToCloudStorageResponse
                    )
                }
            }
        return mUploadImageToCloudStorageResult
    }
}