package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.repositories.DashboardFragmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardFragmentViewModel : ViewModel() {

    private var mDashboardFragmentRepository: DashboardFragmentRepository =
        DashboardFragmentRepository()

    private var _getDashboardItemsListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getDashboardItemsListResult: StateFlow<HashMap<String, Any>>
        get() = _getDashboardItemsListResult

    private var _getUserDetailsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getUserDetailsResult: StateFlow<HashMap<String, Any>>
        get() = _getUserDetailsResult

    private var _getCartListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val getCartListResult: StateFlow<HashMap<String, Any>>
        get() = _getCartListResult

    suspend fun getDashboardItemsList() {
        _getDashboardItemsListResult = mDashboardFragmentRepository.getDashboardItemsList()
    }

    suspend fun getUserDetails() {
        _getUserDetailsResult = mDashboardFragmentRepository.getUserDetails()
    }

    suspend fun getCartList() {
        _getCartListResult = mDashboardFragmentRepository.getCartList()
    }

}