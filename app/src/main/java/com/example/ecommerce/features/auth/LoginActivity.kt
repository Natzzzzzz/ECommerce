package com.example.ecommerce.features.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce.R
import com.example.ecommerce.features.home_page.AuthViewModelFactory
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var  btn_login: Button
    private lateinit var  backButton: ImageView
    private lateinit var  edt_username: EditText
    private lateinit var  edt_password: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(AuthRepository())
        )[AuthViewModel::class.java]

        btn_login = findViewById(R.id.btn_login)
        backButton = findViewById(R.id.backButton)
        edt_username = findViewById(R.id.edt_username)
        edt_password = findViewById(R.id.edt_password)

        backButton.setOnClickListener {
            finish()
        }
        btn_login.setOnClickListener {
            val email = edt_username.text.toString()
            val password = edt_password.text.toString()

            Log.e("LoginActivity", "Login button clicked with email: $email") // Debug

            viewModel.login(email, password)
        }


        viewModel.loginResult.observe(this) { response ->
            Log.e("LoginActivity", "Received loginResult: $response") // Debug

            if (response != null && response.token != null) {
                TokenManager.saveToken(this, response.token)
                Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Log.e("LoginActivity", "Login failed: response = $response") // Debug
                Toast.makeText(this, "Your Email or Password is wrong", Toast.LENGTH_SHORT).show()
            }
        }



    }
}