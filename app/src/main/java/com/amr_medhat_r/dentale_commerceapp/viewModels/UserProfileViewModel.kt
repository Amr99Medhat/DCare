package com.amr_medhat_r.dentale_commerceapp.viewModels

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amr_medhat_r.dentale_commerceapp.repositories.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {

    private var mUserProfileRepository: UserProfileRepository = UserProfileRepository()

    private var _updateUserProfileDataResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val updateUserProfileDataResult: StateFlow<HashMap<String, Any>>
        get() = _updateUserProfileDataResult

    private var _uploadImageToCloudStorageResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val uploadImageToCloudStorageResult: StateFlow<HashMap<String, Any>>
        get() = _uploadImageToCloudStorageResult

    fun pickImage(resultLauncher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch {
            mUserProfileRepository.pickImage(resultLauncher)
        }
    }

    suspend fun updateUserProfileData(userHashMap: HashMap<String, Any>) {
        _updateUserProfileDataResult = mUserProfileRepository.updateUserProfileData(userHashMap)
    }


    suspend fun uploadImageToCloudStorage(
        activity: Activity,
        imageFileURI: Uri?,
        imageType: String
    ) {
        _uploadImageToCloudStorageResult =
            mUserProfileRepository.uploadImageToCloudStorage(activity, imageFileURI, imageType)
        Log.d("CloudAmr",uploadImageToCloudStorageResult.toString())
    }
}