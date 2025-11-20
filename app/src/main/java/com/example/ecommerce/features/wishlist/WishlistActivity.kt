package com.example.ecommerce.features.wishlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.MyWishlistAdapter
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.WishlistViewModel
import com.example.ecommerce.viewModel.WishlistViewModelFactory

class WishlistActivity : AppCompatActivity() {

    private lateinit var rcv_myWishlist: RecyclerView
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var myWishlistAdapter: MyWishlistAdapter
    private lateinit var backButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        wishlistViewModel = ViewModelProvider(
            this,
            WishlistViewModelFactory(WishlistRepository())
        )[WishlistViewModel::class.java]

        rcv_myWishlist = findViewById(R.id.rcv_myWishlist)
        backButton = findViewById(R.id.backButton)
        rcv_myWishlist.layoutManager = LinearLayoutManager(this)
        myWishlistAdapter = MyWishlistAdapter(this, wishlistViewModel)
        rcv_myWishlist.adapter = myWishlistAdapter

        backButton.setOnClickListener {
            finish()
        }
        // Quan sát dữ liệu từ ViewModel và cập nhật Adapter
        wishlistViewModel.wishlistItems.observe(this) { wishlistItems ->
            Log.d("WishlistActivity", "Wishlist updated: $wishlistItems")
            myWishlistAdapter.setWishlistItems(wishlistItems)
        }

        // Gọi API lấy danh sách wishlist
        val token = TokenManager.getToken(this) ?: ""
        wishlistViewModel.getWishlist(token)

    }
}