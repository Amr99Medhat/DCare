package com.amr_medhat_r.dentale_commerceapp.repositories

import com.amr_medhat_r.dentale_commerceapp.firebase.FireStoreClass
import com.amr_medhat_r.dentale_commerceapp.firebase.FirebaseAuthClass
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.models.FavoriteProduct
import com.amr_medhat_r.dentale_commerceapp.models.Product
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class ProductDetailsRepository {

    private var mGetCurrentUserIDResult: MutableStateFlow<String> = MutableStateFlow("")
    private var mAddCartItemsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    private var mCheckIfItemExistInCartResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    private var mCheckIfFavProductResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    private var mAddFavProductResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    private var mDeleteFavProductResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())

    suspend fun getCurrentUserID(): MutableStateFlow<String> {
        mGetCurrentUserIDResult.emit(FirebaseAuthClass().getCurrentUserID())
        return mGetCurrentUserIDResult
    }

    suspend fun addCartItems(addToCart: CartItem): MutableStateFlow<HashMap<String, Any>> {
        mAddCartItemsResult = MutableStateFlow(HashMap())
        // update User Profile Data with FireStore
        FireStoreClass().addCartItems(addToCart)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateUserProfileDataResponse = HashMap<String, Any>()
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mAddCartItemsResult.emit(
                            updateUserProfileDataResponse
                        )
                    }
                } else {
                    // If the reset is not successful then show error message.
                    val updateUserProfileDataResponse = HashMap<String, Any>()
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = false
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mAddCartItemsResult.emit(
                            updateUserProfileDataResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val updateUserProfileDataResponse = HashMap<String, Any>()
                updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = false
                updateUserProfileDataResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mAddCartItemsResult.emit(
                        updateUserProfileDataResponse
                    )
                }
            }
        return mAddCartItemsResult
    }

    suspend fun checkIfItemExistInCart(productId: String): MutableStateFlow<HashMap<String, Any>> {
        mCheckIfItemExistInCartResult = MutableStateFlow(HashMap())
        // update User Profile Data with FireStore
        FireStoreClass().checkIfItemExistInCart(productId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.documents.size > 0) {
                        val updateUserProfileDataResponse = HashMap<String, Any>()
                        updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = true
                        runBlocking {
                            mCheckIfItemExistInCartResult.emit(
                                updateUserProfileDataResponse
                            )
                        }
                    } else {
                        // If the reset is not successful then show error message.
                        val updateUserProfileDataResponse = HashMap<String, Any>()
                        updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = false
                        runBlocking {
                            mCheckIfItemExistInCartResult.emit(
                                updateUserProfileDataResponse
                            )
                        }
                    }

                } else {
                    // If the reset is not successful then show error message.
                    val updateUserProfileDataResponse = HashMap<String, Any>()
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = false
                    updateUserProfileDataResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mCheckIfItemExistInCartResult.emit(
                            updateUserProfileDataResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val updateUserProfileDataResponse = HashMap<String, Any>()
                updateUserProfileDataResponse[Constants.KEY_RESPONSE_STATE] = false
                updateUserProfileDataResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mCheckIfItemExistInCartResult.emit(
                        updateUserProfileDataResponse
                    )
                }
            }
        return mCheckIfItemExistInCartResult
    }

    suspend fun checkIfFavProduct(productId: String): MutableStateFlow<HashMap<String, Any>> {
        mCheckIfFavProductResult = MutableStateFlow(HashMap())
        // update User Profile Data with FireStore
        FireStoreClass().checkIfFavProduct(productId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.documents.size > 0) {
                        val checkIfFavProductResponse = HashMap<String, Any>()
                        checkIfFavProductResponse[Constants.KEY_RESPONSE_STATE] = true
                        checkIfFavProductResponse[Constants.KEY_RESPONSE_VALUE] =
                            task.result.documents[0].id
                        runBlocking {
                            mCheckIfFavProductResult.emit(
                                checkIfFavProductResponse
                            )
                        }
                    } else {
                        // If the reset is not successful then show error message.
                        val checkIfFavProductResponse = HashMap<String, Any>()
                        checkIfFavProductResponse[Constants.KEY_RESPONSE_STATE] = false
                        runBlocking {
                            mCheckIfFavProductResult.emit(
                                checkIfFavProductResponse
                            )
                        }
                    }

                } else {
                    // If the reset is not successful then show error message.
                    val checkIfFavProductResponse = HashMap<String, Any>()
                    checkIfFavProductResponse[Constants.KEY_RESPONSE_STATE] = false
                    checkIfFavProductResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mCheckIfFavProductResult.emit(
                            checkIfFavProductResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val checkIfFavProductResponse = HashMap<String, Any>()
                checkIfFavProductResponse[Constants.KEY_RESPONSE_STATE] = false
                checkIfFavProductResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mCheckIfFavProductResult.emit(
                        checkIfFavProductResponse
                    )
                }
            }
        return mCheckIfFavProductResult
    }

    suspend fun addFavProduct(
        userID: String,
        productId: String
    ): MutableStateFlow<HashMap<String, Any>> {
        mAddFavProductResult = MutableStateFlow(HashMap())
        // update User Profile Data with FireStore
        FireStoreClass().addUserFavProduct(FavoriteProduct(userID, productId))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val addFavProductResponse = HashMap<String, Any>()
                    addFavProductResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mAddFavProductResult.emit(
                            addFavProductResponse
                        )
                    }
                } else {
                    // If the reset is not successful then show error message.
                    val addFavProductResponse = HashMap<String, Any>()
                    addFavProductResponse[Constants.KEY_RESPONSE_STATE] = false
                    addFavProductResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mAddFavProductResult.emit(
                            addFavProductResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val addFavProductResponse = HashMap<String, Any>()
                addFavProductResponse[Constants.KEY_RESPONSE_STATE] = false
                addFavProductResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mAddFavProductResult.emit(
                        addFavProductResponse
                    )
                }
            }
        return mAddFavProductResult
    }

    suspend fun deleteFavProduct(
        favoriteProductId: String
    ): MutableStateFlow<HashMap<String, Any>> {
        mDeleteFavProductResult = MutableStateFlow(HashMap())
        // update User Profile Data with FireStore
        FireStoreClass().deleteUserFavProduct(favoriteProductId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val addFavProductResponse = HashMap<String, Any>()
                    addFavProductResponse[Constants.KEY_RESPONSE_STATE] = true
                    runBlocking {
                        mDeleteFavProductResult.emit(
                            addFavProductResponse
                        )
                    }
                } else {
                    // If the reset is not successful then show error message.
                    val addFavProductResponse = HashMap<String, Any>()
                    addFavProductResponse[Constants.KEY_RESPONSE_STATE] = false
                    addFavProductResponse[Constants.KEY_RESPONSE_VALUE] =
                        task.exception!!.message.toString()
                    runBlocking {
                        mDeleteFavProductResult.emit(
                            addFavProductResponse
                        )
                    }
                }
            }
            .addOnFailureListener {
                val addFavProductResponse = HashMap<String, Any>()
                addFavProductResponse[Constants.KEY_RESPONSE_STATE] = false
                addFavProductResponse[Constants.KEY_RESPONSE_VALUE] =
                    it.message.toString()
                runBlocking {
                    mDeleteFavProductResult.emit(
                        addFavProductResponse
                    )
                }
            }
        return mDeleteFavProductResult
    }


}