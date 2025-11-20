package com.example.ecommerce.features.auth

import android.util.Log
import com.example.ecommerce.model.LoginRequest
import com.example.ecommerce.model.LoginResponse
import com.example.ecommerce.utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    fun login(email: String, password: String, callback: (Result<LoginResponse>) -> Unit) {
        Log.e("AuthRepository", "Attempting login with email: $email") // Debug

        RetrofitInstance.authAPI.login(LoginRequest(email, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        Log.e("AuthRepository", "Login success: ${response.body()}") // Debug
                        callback(Result.success(response.body()!!))
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("AuthRepository", "Login failed: $errorBody") // Debug lỗi
                        callback(Result.failure(Exception(errorBody ?: "Login failed")))
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("AuthRepository", "Exception: ${t.message}")
                    callback(Result.failure(Exception("Login failed: ${t.message}")))
                }
            })
    }
}


