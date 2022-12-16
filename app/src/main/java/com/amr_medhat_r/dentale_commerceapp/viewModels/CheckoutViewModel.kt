package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.Order
import com.amr_medhat_r.dentale_commerceapp.repositories.CheckoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CheckoutViewModel : ViewModel() {

    private var mCheckoutRepository: CheckoutRepository =
        CheckoutRepository()

    private var _getAllProductsListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getAllProductsListResult: StateFlow<HashMap<String, Any>>
        get() = _getAllProductsListResult

    private var _placeOrderResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val placeOrderResult: StateFlow<HashMap<String, Any>>
        get() = _placeOrderResult

    private var _getCurrentUserIDResult: MutableStateFlow<String> = MutableStateFlow("")
    val getCurrentUserIDResult: StateFlow<String> get() = _getCurrentUserIDResult

    private var _getCartListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getCartListResult: StateFlow<HashMap<String, Any>>
        get() = _getCartListResult

    private var _updateAllDetailsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val updateAllDetailsResult: StateFlow<HashMap<String, Any>>
        get() = _updateAllDetailsResult

    suspend fun getAllProductsList() {
        _getAllProductsListResult = mCheckoutRepository.getAllProductsList()
    }

    suspend fun getCartList() {
        _getCartListResult = mCheckoutRepository.getCartList()
    }

    suspend fun getCurrentUserID() {
        _getCurrentUserIDResult = mCheckoutRepository.getCurrentUserID()
    }

    suspend fun placeOrder(order: Order) {
        _placeOrderResult = mCheckoutRepository.placeOrder(order)
    }

    suspend fun updateAllDetails(cartList: ArrayList<CartItem>, order: Order) {
        _updateAllDetailsResult = mCheckoutRepository.updateAllDetails(cartList, order)
    }
}