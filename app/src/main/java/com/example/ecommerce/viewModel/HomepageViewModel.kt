package com.example.ecommerce.features.home_page

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.ImageItem
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Suggestion
import drawable.home_page.HomepageRepository
import kotlinx.coroutines.launch

class HomepageViewModel(private val repository: HomepageRepository) : ViewModel() {

    private val _adsList = MutableLiveData<List<ImageItem>>()
    val adsList: LiveData<List<ImageItem>> get() = _adsList

    private val _categoriesList = MutableLiveData<List<Category>>()
    val categoriesList: LiveData<List<Category>> get() = _categoriesList

    private val _productsList = MutableLiveData<List<Product>>()
    val productsList: LiveData<List<Product>> get() = _productsList

    private val _furnitureList = MutableLiveData<List<Product>>()
    val furnitureList: LiveData<List<Product>> get() = _furnitureList

    private val _newShoesList = MutableLiveData<List<Product>>()
    val newShoesList: LiveData<List<Product>> get() = _newShoesList

    private val _topSellingList = MutableLiveData<List<Product>>()
    val topSellingList: LiveData<List<Product>> get() = _topSellingList

    private val _allProductList = MutableLiveData<List<Product>>()
    val allProductList: LiveData<List<Product>> get() = _allProductList

    private val _productListByCategory = MutableLiveData<List<Product>>()
    val productListByCategory: LiveData<List<Product>> get() = _productListByCategory

    private val _searchSuggestion = MutableLiveData<List<Suggestion>>(emptyList())
    val searchSuggestion: LiveData<List<Suggestion>> get() = _searchSuggestion

    private val _searchProducts = MutableLiveData<List<Product>>(emptyList())
    val searchProducts: LiveData<List<Product>> get() = _searchProducts



    fun fetchAds() {
        viewModelScope.launch {
            repository.getAds().collect { result ->
                result.fold(
                    onSuccess = { _adsList.value = it.take(4)},
                    onFailure = { it.printStackTrace() }
                )
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { result ->
                result.fold(
                    onSuccess = { _categoriesList.value = it },
                    onFailure = { it.printStackTrace() }
                )
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            repository.getPProducts().collect { result ->
                result.fold(
                    onSuccess = { _productsList.value = it },
                    onFailure = { it.printStackTrace() }
                )
            }
        }
    }



    fun fetchFurniture() {
        viewModelScope.launch {
            repository.getFurniture().collect { result ->
                result.fold(
                    onSuccess = { _furnitureList.value = it },
                    onFailure = { it.printStackTrace() }
                )
            }
        }
    }

    fun fetchNewShoes() {
        viewModelScope.launch {
            repository.getNewShoes().collect { result ->
                result.fold(
                    onSuccess = { _newShoesList.value = it },
                    onFailure = { it.printStackTrace() }
                )
            }
        }
    }

    fun fetchTopSelling() {
        viewModelScope.launch {
            repository.getTopSelling().collect { result ->
                result.fold(
                    onSuccess = { _topSellingList.value = it.take(3) },
                    onFailure = { it.printStackTrace() }
                )
            }
        }
    }

    fun fetchAllProduct() {
        viewModelScope.launch {
            repository.getAllProduct().collect { result ->
                result.fold(
                    onSuccess = { _allProductList.value = it },
                    onFailure = { it.printStackTrace() }
                )
            }
        }
    }

    fun fetchProductsByCategory(categoryId: String, page: Int, limit: Int) {
            viewModelScope.launch {
                repository.getProductsByCategory(categoryId, page, limit).collect { result ->
                    result.fold(
                        onSuccess = {
                            _productListByCategory.value = it
                            println("Data received: ${it.size} items")
                        },
                        onFailure = {
                            println("Error fetching data: ${it.message}")
                        }
                    )
                }
            }
        }



    fun searchProducts(keyword: String, page: Int, limit: Int) {
        Log.d("HomepageViewModel", "Searching for: $keyword") // Kiểm tra xem có gọi hàm không
        viewModelScope.launch {
            repository.searchProducts(keyword, page, limit).collect { result ->
                result.fold(
                    onSuccess = {
                        println("Data received: ${it.size} items")
                        Log.d("HomepageViewModel", "API Response: ${it.size} items")
                        _searchProducts.value = it
                    },
                    onFailure = {
                        Log.e("HomepageViewModel", "API Call Failed: ${it.message}")
                        _searchProducts.value = emptyList()
                    }
                )
            }
        }
    }

    fun searchSuggestion(keyword: String) {
        Log.d("HomepageViewModel", "Searching suggestion: $keyword") // Kiểm tra xem có gọi hàm không
        viewModelScope.launch {
            repository.searchSuggestions(keyword).collect { result ->
                result.fold(
                    onSuccess = {
                        Log.d("HomepageViewModel", "API Response: ${it.size} items")
                        _searchSuggestion.value = it
                    },
                    onFailure = {
                        Log.e("HomepageViewModel", "API Call Failed: ${it.message}")
                        _searchSuggestion.value = emptyList()
                    }
                )
            }
        }
    }




}
