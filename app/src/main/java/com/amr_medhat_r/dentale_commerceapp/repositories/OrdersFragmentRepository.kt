package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.models.Order
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class OrdersFragmentRepository {

    private var mGetMyOrdersListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())


    suspend fun getMyOrdersList(): MutableStateFlow<HashMap<String, Any>> {
        mGetMyOrdersListResult = MutableStateFlow(HashMap())
        FireStoreClass().getMyOrdersList()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val ordersList: ArrayList<Order> = ArrayList()

                    for (p in task.result.documents) {
                        val orderItem = p.toObject(Order::class.java)
                        orderItem!!.id = p.id

                        ordersList.add(orderItem)
                    }

                    val getMyOrdersListResponse = HashMap<String, Any>()
                    getMyOrdersListResponse[Constants.KEY_RESPONSE_STATE] = true
                    getMyOrdersListResponse[Constants.KEY_RESPONSE_VALUE] = ordersList
                    runBlocking {
                        mGetMyOrdersListResult.emit(
                            getMyOrdersListResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val getMyOrdersListResponse = HashMap<String, Any>()
                    getMyOrdersListResponse[Constants.KEY_RESPONSE_STATE] = false
                    getMyOrdersListResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mGetMyOrdersListResult.emit(
                            getMyOrdersListResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val getMyOrdersListResponse = HashMap<String, Any>()
                getMyOrdersListResponse[Constants.KEY_RESPONSE_STATE] = false
                getMyOrdersListResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mGetMyOrdersListResult.emit(
                        getMyOrdersListResponse
                    )
                }
            }

        return mGetMyOrdersListResult
    }
}