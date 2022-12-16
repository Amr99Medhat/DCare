package com.amr_medhat_r.dentale_commerceapp.firebase

import android.content.Context
import com.amr_medhat_r.dentale_commerceapp.activities.CartListActivity
import com.amr_medhat_r.dentale_commerceapp.models.*
import com.amr_medhat_r.dentale_commerceapp.utilities.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(userInfo: User): Task<Void> {
        // Create a collection in FireStore and upload the user's data. If the collection name is exists there is no problem.
        return mFireStore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
    }

    fun getUserDetails(): Task<DocumentSnapshot> {
        //Here we pass the collection name from which we wants the data.
        return mFireStore.collection(Constants.USERS)
            // The document id to get the fields of user.
            .document(FirebaseAuthClass().getCurrentUserID())
            .get()
    }

    fun updateUserProfileData(userHashMap: HashMap<String, Any>): Task<Void> {
        return mFireStore.collection(Constants.USERS)
            .document(FirebaseAuthClass().getCurrentUserID())
            .update(userHashMap)
    }

    fun getDashboardItemsList(): Task<QuerySnapshot> {
        return mFireStore.collection(Constants.PRODUCTS)
            .get()
    }

    fun addCartItems(addToCart: CartItem): Task<Void> {
        return mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
    }

    fun checkIfItemExistInCart(productId: String): Task<QuerySnapshot> {
        return mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
    }

    fun getCartList(): Task<QuerySnapshot> {
        return mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .get()
    }

    fun updateMyCart(context: Context, cartId: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cartId)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
            }
    }

    fun placeOrder(order: Order): Task<Void> {
        return mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
    }

    fun updateAllDetails(cartList: ArrayList<CartItem>, order: Order): Task<Void> {
        val writeBatch = mFireStore.batch()

        for (cartItem in cartList) {
            val productHasMap = HashMap<String, Any>()

            productHasMap[Constants.STOCK_QUANTITY] =
                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()


            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                .document(cartItem.product_id)

            writeBatch.update(documentReference, productHasMap)
        }

        for (cartItem in cartList) {
            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)

            writeBatch.delete(documentReference)

        }
        return writeBatch.commit()
    }

    fun removeItemFromCart(context: Context, cartId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cartId)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
            }
    }

    fun getAddressesList(): Task<QuerySnapshot> {
        return mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .get()
    }

    fun addAddress(addressInfo: Address): Task<Void> {
        return mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
    }

    fun updateAddress(addressInfo: Address, addressId: String): Task<Void> {
        return mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
    }

    fun deleteAddress(addressId: String): Task<Void> {
        return mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
    }

    fun getMyOrdersList(): Task<QuerySnapshot> {
        return mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .get()
    }

    fun checkIfFavProduct(productId: String): Task<QuerySnapshot> {
        return mFireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
    }

    fun addUserFavProduct(favoriteProduct: FavoriteProduct): Task<Void> {
        return mFireStore.collection(Constants.FAVORITES)
            .document()
            .set(favoriteProduct, SetOptions.merge())
    }

    fun deleteUserFavProduct(favoriteProductId: String): Task<Void> {
        return mFireStore.collection(Constants.FAVORITES)
            .document(favoriteProductId)
            .delete()
    }

    fun getUserFavProducts(): Task<QuerySnapshot> {
        return mFireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .get()
    }
}