package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.models.Address
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class AddressListRepository {

    private var mGetAddressesListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mDeleteAddressResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    suspend fun getAddressesList(): MutableStateFlow<HashMap<String, Any>> {
        mGetAddressesListResult = MutableStateFlow(HashMap())
        FireStoreClass().getAddressesList()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val addressList: ArrayList<Address> = ArrayList()

                    for (a in task.result.documents) {
                        val address = a.toObject(Address::class.java)!!
                        address.id = a.id
                        addressList.add(address)
                    }

                    val getAddressesListResponse = HashMap<String, Any>()
                    getAddressesListResponse[Constants.KEY_RESPONSE_STATE] = true
                    getAddressesListResponse[Constants.KEY_RESPONSE_VALUE] = addressList
                    runBlocking {
                        mGetAddressesListResult.emit(
                            getAddressesListResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val getAddressesListResponse = HashMap<String, Any>()
                    getAddressesListResponse[Constants.KEY_RESPONSE_STATE] = false
                    getAddressesListResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mGetAddressesListResult.emit(
                            getAddressesListResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val getAddressesListResponse = HashMap<String, Any>()
                getAddressesListResponse[Constants.KEY_RESPONSE_STATE] = false
                getAddressesListResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mGetAddressesListResult.emit(
                        getAddressesListResponse
                    )
                }
            }

        return mGetAddressesListResult
    }

    suspend fun deleteAddress(addressId: String): MutableStateFlow<HashMap<String, Any>> {
        mDeleteAddressResult = MutableStateFlow(HashMap())
        FireStoreClass().deleteAddress(addressId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val deleteAddressResponse = HashMap<String, Any>()
                    deleteAddressResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mDeleteAddressResult.emit(
                            deleteAddressResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val deleteAddressResponse = HashMap<String, Any>()
                    deleteAddressResponse[Constants.KEY_RESPONSE_STATE] = false
                    deleteAddressResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mDeleteAddressResult.emit(
                            deleteAddressResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val deleteAddressResponse = HashMap<String, Any>()
                deleteAddressResponse[Constants.KEY_RESPONSE_STATE] = false
                deleteAddressResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mDeleteAddressResult.emit(
                        deleteAddressResponse
                    )
                }
            }

        return mDeleteAddressResult
    }
}