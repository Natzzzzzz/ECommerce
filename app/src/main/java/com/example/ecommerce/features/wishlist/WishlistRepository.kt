package com.example.ecommerce.features.wishlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.model.AddToWishlistRequest
import com.example.ecommerce.model.AddToWishlistResponse
import com.example.ecommerce.model.WishlistItem
import com.example.ecommerce.utils.RetrofitInstance
import retrofit2.HttpException

class WishlistRepository() {
    private val api = RetrofitInstance.wishlistAPI

    private val _addToWishlistResult = MutableLiveData<Result<AddToWishlistResponse>>()
    val addToWishlistResult: LiveData<Result<AddToWishlistResponse>> get() = _addToWishlistResult


    private val _wishlist = MutableLiveData<List<WishlistItem>>()
    val wishlist: LiveData<List<WishlistItem>> = _wishlist
    private val _wishlistQuantity = MutableLiveData<Int>()
    val wishlistQuantity: LiveData<Int> get() = _wishlistQuantity

    suspend fun addToWishlist(token: String, _id: String, status: Boolean) {
        try {
            Log.d("WishlistRepository", "Sending request: token=$token, productId=$_id, status=$status")
            val response = api.addToWishlist("Bearer $token", AddToWishlistRequest(_id, status))
            Log.d("WishlistRepository", " response=$response")
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("WishlistRepository", "Full response: $responseBody")
                if (responseBody != null) {
                    if (responseBody.success) { // Kiểm tra success
                        Log.d("WishlistRepository", "Success: ${responseBody.message}")
                        _addToWishlistResult.postValue(Result.success(responseBody))
                    } else {
                        Log.e("WishlistRepository", "Failed: ${responseBody.message}")
                        _addToWishlistResult.postValue(Result.failure(Exception("Failed: ${responseBody.message}")))
                    }
                } else {
                    Log.e("WishlistRepository", "Failed: Response body is null")
                    _addToWishlistResult.postValue(Result.failure(Exception("Response body is null")))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "Failed: Error ${response.code()}, Response: $errorBody"
                Log.e("WishlistRepository", errorMessage)
                _addToWishlistResult.postValue(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Exception: ${e.message}", e)
            _addToWishlistResult.postValue(Result.failure(e))
        }
    }

    suspend fun fetchWishlist(token: String): List<WishlistItem> {
        return try {
            Log.d("WishlistRepository", "Fetching wishlist with token: Bearer $token")
            val response = api.getDataWishlist("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                Log.d("WishlistRepository", "Fetched wishlist successfully: $body")
                body
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "Failed to fetch wishlist: Code ${response.code()}, Error: $errorBody"
                Log.e("WishlistRepository", errorMessage)
                emptyList()
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = "HttpException: Code ${e.code()}, Error: $errorBody"
            Log.e("WishlistRepository", errorMessage)
            emptyList()
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Exception: ${e.message}", e)
            emptyList()
        }
    }

}
