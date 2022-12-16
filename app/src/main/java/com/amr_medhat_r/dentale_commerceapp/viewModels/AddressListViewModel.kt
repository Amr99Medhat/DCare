package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.repositories.AddressListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddressListViewModel : ViewModel() {

    private var mAddressListRepository: AddressListRepository =
        AddressListRepository()

    private var _getAddressesListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getAddressesListResult: StateFlow<HashMap<String, Any>>
        get() = _getAddressesListResult

    private var _deleteAddressResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val deleteAddressResult: StateFlow<HashMap<String, Any>>
        get() = _deleteAddressResult


    suspend fun getAddressesList() {
        _getAddressesListResult = mAddressListRepository.getAddressesList()
    }

    suspend fun deleteAddress(addressId: String) {
        _deleteAddressResult = mAddressListRepository.deleteAddress(addressId)
    }
}