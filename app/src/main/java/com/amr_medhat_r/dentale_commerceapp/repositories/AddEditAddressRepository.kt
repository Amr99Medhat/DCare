package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.models.Address
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class AddEditAddressRepository {

    private var mGetCurrentUserIDResult: MutableStateFlow<String> = MutableStateFlow("")
    private var mUpdateAddressResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    private var mAddAddressResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    suspend fun getCurrentUserID(): MutableStateFlow<String> {
        mGetCurrentUserIDResult.emit(FirebaseAuthClass().getCurrentUserID())
        return mGetCurrentUserIDResult
    }

    suspend fun addAddress(
        addressInfo: Address
    ): MutableStateFlow<HashMap<String, Any>> {
        mAddAddressResult = MutableStateFlow(HashMap())
        FireStoreClass().addAddress(addressInfo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mAddAddressResponse = HashMap<String, Any>()
                    mAddAddressResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mAddAddressResult.emit(
                            mAddAddressResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val mAddAddressResponse = HashMap<String, Any>()
                    mAddAddressResponse[Constants.KEY_RESPONSE_STATE] = false
                    mAddAddressResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mAddAddressResult.emit(
                            mAddAddressResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val mAddAddressResponse = HashMap<String, Any>()
                mAddAddressResponse[Constants.KEY_RESPONSE_STATE] = false
                mAddAddressResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mAddAddressResult.emit(
                        mAddAddressResponse
                    )
                }
            }

        return mAddAddressResult
    }

    suspend fun updateAddress(
        addressInfo: Address,
        addressId: String
    ): MutableStateFlow<HashMap<String, Any>> {
        mUpdateAddressResult = MutableStateFlow(HashMap())
        FireStoreClass().updateAddress(addressInfo, addressId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mUpdateAddressResponse = HashMap<String, Any>()
                    mUpdateAddressResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mUpdateAddressResult.emit(
                            mUpdateAddressResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val mUpdateAddressResponse = HashMap<String, Any>()
                    mUpdateAddressResponse[Constants.KEY_RESPONSE_STATE] = false
                    mUpdateAddressResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mUpdateAddressResult.emit(
                            mUpdateAddressResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val mUpdateAddressResponse = HashMap<String, Any>()
                mUpdateAddressResponse[Constants.KEY_RESPONSE_STATE] = false
                mUpdateAddressResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mUpdateAddressResult.emit(
                        mUpdateAddressResponse
                    )
                }
            }

        return mUpdateAddressResult
    }


}