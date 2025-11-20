package com.example.ecommerce.features.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.CartAdapter
import com.example.ecommerce.adapter.MyWishlistAdapter
import com.example.ecommerce.features.wishlist.WishlistRepository
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.CartViewModelFactory
import com.example.ecommerce.viewModel.WishlistViewModel
import com.example.ecommerce.viewModel.WishlistViewModelFactory

class CartActivity : AppCompatActivity() {

    private lateinit var rcv_myCart: RecyclerView
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter
    private lateinit var backButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartViewModel = ViewModelProvider(
            this,
            CartViewModelFactory(CartRepository())
        )[CartViewModel::class.java]

        rcv_myCart = findViewById(R.id.rcv_myCart)
        backButton = findViewById(R.id.backButton)
        rcv_myCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(this, cartViewModel)
        rcv_myCart.adapter = cartAdapter

        backButton.setOnClickListener {
            finish()
        }

        val token = TokenManager.getToken(this)
        if(token != null){
            cartViewModel.fetchCartItems(token,1,20)
        }
        cartViewModel.cartItems.observe(this) { cartResponse ->
            cartResponse?.let {
                // Handle success - display items
                val items = it.items
                cartAdapter.updateCartItems(it.items)
                // Update RecyclerView adapter with items
            }
        }

        cartViewModel.removeItemResult.observe(this) { result ->
            result.onSuccess { message ->
                val token = TokenManager.getToken(this) ?: ""
                Log.d("CartA","message: ${message}")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                cartViewModel.fetchCartItems(token) // Cập nhật lại danh sách giỏ hàng
            }.onFailure { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }



    }

}