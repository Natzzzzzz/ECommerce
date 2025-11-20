package com.example.ecommerce.network

import com.example.ecommerce.model.AddToCartRequest
import com.example.ecommerce.model.AddToCartResponse
import com.example.ecommerce.model.AddToWishlistRequest
import com.example.ecommerce.model.AddToWishlistResponse
import com.example.ecommerce.model.CartQuantityResponse
import com.example.ecommerce.model.WishlistItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface WishlistAPI {

    //Add to WishList
    @POST("wishlist/favorite")
    suspend fun addToWishlist(
        @Header("Authorization") token: String,
        @Body request: AddToWishlistRequest
    ): Response<AddToWishlistResponse>

    @GET("wishlist")
    suspend fun getDataWishlist(
        @Header("Authorization") token: String,
    ): Response<List<WishlistItem>>


}