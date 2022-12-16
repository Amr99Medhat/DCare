package com.amr_medhat_r.dentale_commerceapp.viewModels

import androidx.lifecycle.ViewModel
import com.amr_medhat_r.dentale_commerceapp.models.CartItem
import com.amr_medhat_r.dentale_commerceapp.repositories.ProductDetailsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductDetailsViewModel : ViewModel() {

    private var mProductDetailsRepository: ProductDetailsRepository =
        ProductDetailsRepository()

    private var _getCurrentUserIDResult: MutableStateFlow<String> = MutableStateFlow("")
    val getCurrentUserIDResult: StateFlow<String> get() = _getCurrentUserIDResult

    private var _addCartItemsResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val addCartItemsResult: StateFlow<HashMap<String, Any>>
        get() = _addCartItemsResult

    private var _checkIfItemExistInCartResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val checkIfItemExistInCartResult: StateFlow<HashMap<String, Any>>
        get() = _checkIfItemExistInCartResult

    private var _checkIfFavProductResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val checkIfFavProductResult: StateFlow<HashMap<String, Any>>
        get() = _checkIfFavProductResult

    private var _addFavProductResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val addFavProductResult: StateFlow<HashMap<String, Any>>
        get() = _addFavProductResult

    private var _deleteFavProductResult: MutableStateFlow<HashMap<String, Any>> =
        MutableStateFlow(HashMap())
    val deleteFavProductResult: StateFlow<HashMap<String, Any>>
        get() = _deleteFavProductResult

    suspend fun getCurrentUserID() {
        _getCurrentUserIDResult = mProductDetailsRepository.getCurrentUserID()
    }

    suspend fun addCartItems(addToCart: CartItem) {
        _addCartItemsResult = mProductDetailsRepository.addCartItems(addToCart)
    }

    suspend fun checkIfItemExistInCart(productId: String) {
        _checkIfItemExistInCartResult = mProductDetailsRepository.checkIfItemExistInCart(productId)
    }

    suspend fun checkIfFavProduct(productId: String) {
        _checkIfFavProductResult = mProductDetailsRepository.checkIfFavProduct(productId)
    }

    suspend fun addFavProduct(
        userID: String,
        productId: String
    ) {
        _addFavProductResult = mProductDetailsRepository.addFavProduct(userID, productId)
    }

    suspend fun deleteFavProduct(
        favoriteProductId: String
    ) {
        _deleteFavProductResult = mProductDetailsRepository.deleteFavProduct(favoriteProductId)
    }
}