package com.example.ecommerce.features.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.AddToCartRequest
import com.example.ecommerce.model.CartResponseItem
import com.example.ecommerce.model.ListCartItemResponse
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    val addToCartResult = repository.addToCartResult
    val cartQuantity: LiveData<Result<Int>> get() = repository.cartQuantity

    private val _cartItems = MutableLiveData<ListCartItemResponse>()
    val cartItems: LiveData<ListCartItemResponse> get() = _cartItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _removeItemResult = MutableLiveData<Result<String>>()
    val removeItemResult: LiveData<Result<String>> = _removeItemResult

    fun addToCart(token: String, variantId: String, quantity: Int) {
        viewModelScope.launch {
            repository.addToCart(token, AddToCartRequest(variantId, quantity))
        }
    }

    fun getCartQuantity(token: String) {
        Log.d("CartViewModel", "Fetching cart quantity")
        viewModelScope.launch {
            repository.fetchCartQuantity(token)
        }
    }

    fun fetchCartItems(userId: String, page: Int = 1, limit: Int = 10) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.getCartItems(userId, page, limit)
            _isLoading.value = false

            if (result.isSuccess) {
                _cartItems.value = result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Unknown error"
                _cartItems.value = null
            }
        }
    }

    fun removeItemFromCart(token: String, itemId: String) {
        viewModelScope.launch {
            val result = repository.removeItem(token, itemId)
            _removeItemResult.postValue(result)
        }
    }


    fun updateCartItemQuantity(token: String, itemId: String, newQuantity: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val currentItem = _cartItems.value?.items?.find { it.item_id == itemId }
            if (currentItem?.quantity == 1 && newQuantity < 1) {
                removeItemFromCart(token, itemId)
            } else if (newQuantity >= 1) {
                val result = repository.updateCartItem(token, itemId, newQuantity)
                _isLoading.value = false

                if (result.isSuccess) {
                    // Cập nhật item trong danh sách hiện tại
                    val currentCart = _cartItems.value
                    currentCart?.let { cart ->
                        val updatedItems = cart.items.map { item ->
                            if (item.item_id == itemId) item.copy(quantity = newQuantity)
                            else item
                        }
                        _cartItems.value = cart.copy(items = updatedItems)
                    }
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to update quantity"
                }
            } else {
                _isLoading.value = false // Trường hợp newQuantity < 1 nhưng không phải xóa
            }
        }
    }





}
