package com.example.ecommerce.network

import com.example.ecommerce.model.AddToCartRequest
import com.example.ecommerce.model.AddToCartResponse
import com.example.ecommerce.model.CartDeleteResponse
import com.example.ecommerce.model.CartQuantityResponse
import com.example.ecommerce.model.ListCartItemResponse
import com.example.ecommerce.model.UpdateCartRequest
import com.example.ecommerce.model.UpdateCartResponse
import com.example.ecommerce.model.WishlistItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartAPI {
    //Add to cart
    @POST("cart/add")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<AddToCartResponse>

    //List of cart item
    @GET("cart/quantity")
    suspend fun getCartQuantity(
        @Header("Authorization") token: String
    ): Response<CartQuantityResponse>

    //List of cart item
    @GET("cart/")
    suspend fun getListCartItem(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ListCartItemResponse>

    @DELETE("cart/{item_id}")
    suspend fun removeItemFromCart(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: String
    ): Response<CartDeleteResponse>

    @PUT("cart/update")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Body updateRequest: UpdateCartRequest
    ): Response<UpdateCartResponse>
}