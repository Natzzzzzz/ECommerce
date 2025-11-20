package com.example.ecommerce.features.product_detail

import com.example.ecommerce.model.ProductResponse
import com.example.ecommerce.utils.RetrofitInstance
import retrofit2.Response

class ProductDetailRepository {
    suspend fun getProductDetail(id: String): Response<ProductResponse> {
        return RetrofitInstance.productDetailAPI.getProductDetail(id)
    }
}