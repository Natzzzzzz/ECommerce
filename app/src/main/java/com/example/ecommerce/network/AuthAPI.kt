package com.example.ecommerce.network

import com.example.ecommerce.model.LoginRequest
import com.example.ecommerce.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {

    //Login
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}