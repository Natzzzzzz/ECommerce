package com.example.ecommerce.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.features.product_detail.ProductDetailRepository
import com.example.ecommerce.model.ProductResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class ProductDetailViewModel(
    private val repository: ProductDetailRepository
) : ViewModel() {

    // LiveData để lưu trữ toàn bộ ProductResponse
    private val _product = MutableLiveData<ProductResponse>()
    val product: LiveData<ProductResponse> = _product

    // LiveData để lưu trữ lỗi
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchProductDetail(id: String) {
        viewModelScope.launch {
            try {
                val response: Response<ProductResponse> = repository.getProductDetail(id)
                if (response.isSuccessful) {
                    _product.postValue(response.body())
                    Log.d("PDVM", "response: ${response.body()}")
                } else {
                    _errorMessage.postValue("Lỗi: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Lỗi: ${e.message}")
            }
        }
    }
}