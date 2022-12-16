package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.repositories.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WishlistViewModel : ViewModel() {

    private var mCartListRepository: WishlistRepository =
        WishlistRepository()

    private var _getAllProductsListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getAllProductsListResult: StateFlow<HashMap<String, Any>>
        get() = _getAllProductsListResult

    private var _getFavListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getFavListResult: StateFlow<HashMap<String, Any>>
        get() = _getFavListResult

    suspend fun getAllProductsList() {
        _getAllProductsListResult = mCartListRepository.getAllProductsList()
    }

    suspend fun getFavList() {
        _getFavListResult = mCartListRepository.getFavList()
    }

}