package com.example.ecommerce.network

import com.example.ecommerce.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductDetailAPI {

    //Product Detail
    @GET("products/detail/{id}")
    suspend fun getProductDetail(@Path("id") id: String): Response<ProductResponse>
}