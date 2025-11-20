package com.example.ecommerce.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.features.auth.AuthRepository
import com.example.ecommerce.model.LoginResponse
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> get() = _loginResult

    fun login(email: String, password: String) {
        Log.e("AuthViewModel", "login() function called") // Debug

        repository.login(email, password) { result ->
            result.onSuccess { response ->
                Log.e("AuthViewModel", "Login response: $response") // Debug
                _loginResult.postValue(response)
            }.onFailure { e ->
                Log.e("AuthViewModel", "Login error: ${e.message}") // Debug lỗi
                _loginResult.postValue(LoginResponse(null, e.message ?: "Login failed"))
            }
        }
    }




}