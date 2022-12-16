package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class CartListRepository {

    private var mGetCartListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mGetAllProductsListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

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

    suspend fun getAllProductsList(): MutableStateFlow<HashMap<String, Any>> {
        mGetAllProductsListResult = MutableStateFlow(HashMap())
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
                        mGetAllProductsListResult.emit(
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
                        mGetAllProductsListResult.emit(
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
                    mGetAllProductsListResult.emit(
                        logInRegisteredUserResponse
                    )
                }
            }

        return mGetAllProductsListResult
    }
}