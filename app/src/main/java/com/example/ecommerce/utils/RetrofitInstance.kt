package com.example.ecommerce.utils

import com.example.ecommerce.network.AuthAPI
import com.example.ecommerce.network.CartAPI
import com.example.ecommerce.network.HomepageAPI
import com.example.ecommerce.network.ProductDetailAPI
import com.example.ecommerce.network.WishlistAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val BASE_URL = "http://10.0.2.2:5000/api/"
//    private val BASE_URL = "https://ecommerce-gh8q.onrender.com/api/"

    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val homepageAPI by lazy {
        retrofit.create(HomepageAPI::class.java)
    }
    val authAPI by lazy {
        retrofit.create(AuthAPI::class.java)
    }
    val cartAPI by lazy {
        retrofit.create(CartAPI::class.java)
    }
    val wishlistAPI by lazy {
        retrofit.create(WishlistAPI::class.java)
    }
    val productDetailAPI by lazy {
        retrofit.create(ProductDetailAPI::class.java)
    }
}