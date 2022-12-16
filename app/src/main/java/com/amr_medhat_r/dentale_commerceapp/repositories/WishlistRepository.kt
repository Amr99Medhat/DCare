package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.models.FavoriteProduct
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class WishlistRepository {

    private var mGetAllProductsListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    private var mGetFavListResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

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

    suspend fun getFavList(): MutableStateFlow<HashMap<String, Any>> {
        mGetFavListResult = MutableStateFlow(HashMap())
        FireStoreClass().getUserFavProducts()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list: ArrayList<FavoriteProduct> = ArrayList()

                    for (i in task.result.documents) {
                        val favoriteProduct = i.toObject(FavoriteProduct::class.java)!!
//                        favoriteProduct.id = i.id

                        list.add(favoriteProduct)
                    }

                    val getFavListResultResponse = HashMap<String, Any>()
                    getFavListResultResponse[Constants.KEY_RESPONSE_STATE] = true
                    getFavListResultResponse[Constants.KEY_RESPONSE_VALUE] = list
                    runBlocking {
                        mGetFavListResult.emit(
                            getFavListResultResponse
                        )
                    }
                } else {
                    // If the registering is not successful then show error message.
                    val getFavListResultResponse = HashMap<String, Any>()
                    getFavListResultResponse[Constants.KEY_RESPONSE_STATE] = false
                    getFavListResultResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mGetFavListResult.emit(
                            getFavListResultResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val getFavListResultResponse = HashMap<String, Any>()
                getFavListResultResponse[Constants.KEY_RESPONSE_STATE] = false
                getFavListResultResponse[Constants.KEY_RESPONSE_VALUE] = it.message.toString()
                runBlocking {
                    mGetFavListResult.emit(
                        getFavListResultResponse
                    )
                }
            }

        return mGetFavListResult
    }
}