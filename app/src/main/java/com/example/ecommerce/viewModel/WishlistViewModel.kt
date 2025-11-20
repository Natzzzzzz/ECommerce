package com.example.ecommerce.viewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.features.wishlist.WishlistRepository
import com.example.ecommerce.model.WishlistItem
import kotlinx.coroutines.launch

class WishlistViewModel(private val repository: WishlistRepository) : ViewModel() {
    private val _wishlistItems = MutableLiveData<List<WishlistItem>>()
    val wishlistItems: LiveData<List<WishlistItem>> get() = _wishlistItems

    private val _wishlistQuantity = MutableLiveData<Int>()
    val wishlistQuantity: LiveData<Int> get() = _wishlistQuantity
    private val _addToWishlistResult = MutableLiveData<Result<String>>()
    val addToWishlistResult: LiveData<Result<String>> get() = _addToWishlistResult
    fun getWishlist(token: String) {
        viewModelScope.launch {
            try {
                val wishlist = repository.fetchWishlist(token)
                Log.d("WishlistViewModel", "Fetched wishlist: $wishlist") // Thêm log này
                _wishlistItems.postValue(wishlist)
                _wishlistQuantity.postValue(wishlist.size)
                Log.d("WishlistViewModel", "Wishlist size: ${wishlist.size}")
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Failed to fetch wishlist: ${e.message}")
                _wishlistItems.postValue(emptyList())
                _wishlistQuantity.postValue(0)
            }
        }
    }

    fun addToWishlist(productId: String, status: Boolean, token: String) {
        viewModelScope.launch {
            try {
                repository.addToWishlist(token, productId, status)
                getWishlist(token) // Cập nhật lại danh sách sau khi thay đổi
            } catch (e: Exception) {
                Log.e("WishlistViewModel", "Failed to add to wishlist: ${e.message}")
            }
        }
    }
}