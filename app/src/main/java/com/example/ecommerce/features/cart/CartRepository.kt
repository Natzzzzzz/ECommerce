package com.example.ecommerce.features.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.model.AddToCartRequest
import com.example.ecommerce.model.AddToCartResponse
import com.example.ecommerce.model.CartResponseItem
import com.example.ecommerce.model.ListCartItemResponse
import com.example.ecommerce.model.UpdateCartRequest
import com.example.ecommerce.model.UpdateCartResponse
import com.example.ecommerce.model.WishlistItem
import com.example.ecommerce.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class CartRepository() {
    private val api = RetrofitInstance.cartAPI

    private val _addToCartResult = MutableLiveData<Result<AddToCartResponse>>()
    val addToCartResult: LiveData<Result<AddToCartResponse>> get() = _addToCartResult

    private val _cartQuantity = MutableLiveData<Result<Int>>()
    val cartQuantity: LiveData<Result<Int>> get() = _cartQuantity

    private val _cartListItem = MutableLiveData<Result<List<CartResponseItem>>>()
    val cartListItem: LiveData<Result<List<CartResponseItem>>> get() = _cartListItem

    suspend fun addToCart(token: String, request: AddToCartRequest) {
        try {
            val response = withContext(Dispatchers.IO) {
                api.addToCart("Bearer $token", request)
            }

            if (response.isSuccessful) {
                response.body()?.let {
                    _addToCartResult.postValue(Result.success(it))
                } ?: run {
                    _addToCartResult.postValue(Result.failure(Exception("Response body is null")))
                }
            } else {
                val errorMessage = "Error ${response.code()}: ${response.errorBody()?.string()}"
                Log.e("CartRepository", errorMessage)
                _addToCartResult.postValue(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception: ${e.message}", e)
            _addToCartResult.postValue(Result.failure(e))
        }
    }


    suspend fun fetchCartQuantity(token: String) {
        try {
            val response = api.getCartQuantity("Bearer $token")

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("CartRepository", "Cart quantity response: $responseBody")
                    _cartQuantity.postValue(Result.success(responseBody.quantityItemsCart))
                } else {
                    Log.e("CartRepository", "Response body is null")
                    _cartQuantity.postValue(Result.failure(Exception("Response body is null")))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Log.e("CartRepository", "Error fetching cart quantity: $errorMessage")
                _cartQuantity.postValue(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception: ${e.message}")
            _cartQuantity.postValue(Result.failure(e))
        }
    }

    suspend fun removeItem(token: String,itemId: String): Result<String> {
        return try {
            val response = api.removeItemFromCart(token, itemId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Item removed successfully")
            } else {
                Log.d("Repo","asdasd${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCartItems(userId: String, page: Int, limit: Int): Result<ListCartItemResponse> {
        return try {
            val response = api.getListCartItem(userId, page, limit)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItem(token: String, itemId: String, quantity: Int): Result<UpdateCartResponse> {
        return try {
            val response = api.updateCartItem(token, UpdateCartRequest(itemId, quantity))
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception(response.errorBody()?.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




}
