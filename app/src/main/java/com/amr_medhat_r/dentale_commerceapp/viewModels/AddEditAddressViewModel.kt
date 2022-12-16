package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.models.Address
import com.amr_medhat_r.dentale_commerceapp.repositories.AddEditAddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddEditAddressViewModel : ViewModel() {

    private var mAddEditAddressRepository: AddEditAddressRepository =
        AddEditAddressRepository()

    private var _getCurrentUserIDResult: MutableStateFlow<String> = MutableStateFlow("")
    val getCurrentUserIDResult: StateFlow<String> get() = _getCurrentUserIDResult

    private var _addAddressResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val addAddressResult: StateFlow<HashMap<String, Any>>
        get() = _addAddressResult

    private var _updateAddressResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val updateAddressResult: StateFlow<HashMap<String, Any>>
        get() = _updateAddressResult

    suspend fun getCurrentUserID() {
        _getCurrentUserIDResult = mAddEditAddressRepository.getCurrentUserID()
    }

    suspend fun addAddress(
        addressInfo: Address,
    ) {
        _addAddressResult = mAddEditAddressRepository.addAddress(addressInfo)
    }

    suspend fun updateAddress(
        addressInfo: Address,
        addressId: String
    ) {
        _updateAddressResult = mAddEditAddressRepository.updateAddress(addressInfo, addressId)
    }


}