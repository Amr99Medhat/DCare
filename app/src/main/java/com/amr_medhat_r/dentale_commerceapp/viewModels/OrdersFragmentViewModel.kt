package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.repositories.OrdersFragmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OrdersFragmentViewModel : ViewModel() {

    private var mOrdersFragmentRepository: OrdersFragmentRepository =
        OrdersFragmentRepository()

    private var _getMyOrdersListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getMyOrdersListResult: StateFlow<HashMap<String, Any>>
        get() = _getMyOrdersListResult

    suspend fun getMyOrdersList() {
        _getMyOrdersListResult = mOrdersFragmentRepository.getMyOrdersList()
    }
}