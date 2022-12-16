package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.Order
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class CheckoutRepository {

    private var mGetDashboardItemsListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mGetCartListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mPlaceOrderResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mUpdateAllDetailsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mGetCurrentUserIDResult: MutableStateFlow<String> = MutableStateFlow("")


    suspend fun getAllProductsList(): MutableStateFlow<HashMap<String, Any>> {
        mGetDashboardItemsListResult = MutableStateFlow(HashMap())
        FireStoreClass().getDashboardItemsList()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val productsList: ArrayList<Product> = ArrayList()

                    for (p in task.result.documents) {
                        val product = p.toObject(Product::class.java)
                        product!!.product_id = p.id

                        productsList.add(product)
                    }

                    val logInRegisteredUserResponse = HashMap<String, Any>()
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = true
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_VALUE] = productsList
                    runBlocking {
                        mGetDashboardItemsListResult.emit(
                            logInRegisteredUserResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val logInRegisteredUserResponse = HashMap<String, Any>()
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = false
                    logInRegisteredUserResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mGetDashboardItemsListResult.emit(
                            logInRegisteredUserResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val logInRegisteredUserResponse = HashMap<String, Any>()
                logInRegisteredUserResponse[Constants.KEY_RESPONSE_STATE] = false
                logInRegisteredUserResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mGetDashboardItemsListResult.emit(
                        logInRegisteredUserResponse
                    )
                }
            }

        return mGetDashboardItemsListResult
    }

    suspend fun getCartList(): MutableStateFlow<HashMap<String, Any>> {
        mGetCartListResult = MutableStateFlow(HashMap())
        FireStoreClass().getCartList()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list: ArrayList<CartItem> = ArrayList()

                    for (i in task.result.documents) {
                        val cartItem = i.toObject(CartItem::class.java)!!
                        cartItem.id = i.id

                        list.add(cartItem)
                    }

                    val getCartListResultResponse = HashMap<String, Any>()
                    getCartListResultResponse[Constants.KEY_RESPONSE_STATE] = true
                    getCartListResultResponse[Constants.KEY_RESPONSE_VALUE] = list
                    runBlocking {
                        mGetCartListResult.emit(
                            getCartListResultResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val getCartListResultResponse = HashMap<String, Any>()
                    getCartListResultResponse[Constants.KEY_RESPONSE_STATE] = false
                    getCartListResultResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mGetCartListResult.emit(
                            getCartListResultResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val getCartListResultResponse = HashMap<String, Any>()
                getCartListResultResponse[Constants.KEY_RESPONSE_STATE] = false
                getCartListResultResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mGetCartListResult.emit(
                        getCartListResultResponse
                    )
                }
            }

        return mGetCartListResult
    }

    suspend fun getCurrentUserID(): MutableStateFlow<String> {
        mGetCurrentUserIDResult.emit(FirebaseAuthClass().getCurrentUserID())
        return mGetCurrentUserIDResult
    }

    suspend fun placeOrder(order: Order): MutableStateFlow<HashMap<String, Any>> {
        mPlaceOrderResult = MutableStateFlow(HashMap())
        FireStoreClass().placeOrder(order)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val placeOrderResponse = HashMap<String, Any>()
                    placeOrderResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mPlaceOrderResult.emit(
                            placeOrderResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val placeOrderResponse = HashMap<String, Any>()
                    placeOrderResponse[Constants.KEY_RESPONSE_STATE] = false
                    placeOrderResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mPlaceOrderResult.emit(
                            placeOrderResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val placeOrderResponse = HashMap<String, Any>()
                placeOrderResponse[Constants.KEY_RESPONSE_STATE] = false
                placeOrderResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mPlaceOrderResult.emit(
                        placeOrderResponse
                    )
                }
            }

        return mPlaceOrderResult
    }

    suspend fun updateAllDetails(cartList: ArrayList<CartItem>, order: Order): MutableStateFlow<HashMap<String, Any>> {
        mUpdateAllDetailsResult = MutableStateFlow(HashMap())
        FireStoreClass().updateAllDetails(cartList, order)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateAllDetailsResponse = HashMap<String, Any>()
                    updateAllDetailsResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mUpdateAllDetailsResult.emit(
                            updateAllDetailsResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val updateAllDetailsResponse = HashMap<String, Any>()
                    updateAllDetailsResponse[Constants.KEY_RESPONSE_STATE] = false
                    updateAllDetailsResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mUpdateAllDetailsResult.emit(
                            updateAllDetailsResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val updateAllDetailsResponse = HashMap<String, Any>()
                updateAllDetailsResponse[Constants.KEY_RESPONSE_STATE] = false
                updateAllDetailsResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mUpdateAllDetailsResult.emit(
                        updateAllDetailsResponse
                    )
                }
            }

        return mUpdateAllDetailsResult
    }
}