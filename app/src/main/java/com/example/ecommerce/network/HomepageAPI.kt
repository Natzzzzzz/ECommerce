package com.example.ecommerce.network

import com.example.ecommerce.model.AddToCartRequest
import com.example.ecommerce.model.AddToCartResponse
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.ImageItem
import com.example.ecommerce.model.LoginRequest
import com.example.ecommerce.model.LoginResponse
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.ProductResponse
import com.example.ecommerce.model.SearchSuggestionResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HomepageAPI {

    //Ads
    @GET("ads/active")
    suspend fun getDataAds(): List<ImageItem>

    //Categories
    @GET("categories/")
    suspend fun getDataCategories(): List<Category>

    //Get Product by Category
    @GET("products/category")
    suspend fun getProductsByCategory(
        @Query("categoryID") type: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Product>

    //Get Popular Product 2023
    @GET("products/popular")
    suspend fun getDataPProduct(): List<Product>

    //Get Furniture Collection
    @GET("products/category?categoryID=67ca8e4e6c470100c9c094da")
    suspend fun getDataFurniture(): List<Product>

    //Get New Shoes Collection
    @GET("products/category/latest?categoryID=67ca8e4e6c470100c9c094db")
    suspend fun getDataNewShoes(): List<Product>

    //Get Top Selling
    @GET("products/top-selling")
    suspend fun getDataTopSelling(): List<Product>

    @GET("products/popular")
    suspend fun getDataAllProduct(): List<Product>

    //Filter Search
    @GET("search/suggestions")
    suspend fun searchSuggestion(
        @Query("keyword") keyword: String
    ): Response<SearchSuggestionResponse>

    //Filter Search
    @GET("products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<Product>>



}
