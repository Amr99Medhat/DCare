package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.repositories.CartListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartListViewModel : ViewModel() {

    private var mCartListRepository: CartListRepository =
        CartListRepository()

    private var _getCartListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getCartListResult: StateFlow<HashMap<String, Any>>
        get() = _getCartListResult

    private var _getAllProductsListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getAllProductsListResult: StateFlow<HashMap<String, Any>>
        get() = _getAllProductsListResult


    suspend fun getCartList() {
        _getCartListResult = mCartListRepository.getCartList()
    }

    suspend fun getAllProductsList() {
        _getAllProductsListResult = mCartListRepository.getAllProductsList()
    }
}