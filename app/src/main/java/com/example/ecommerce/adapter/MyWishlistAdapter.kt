package com.example.ecommerce.adapter

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Variant
import com.example.ecommerce.model.WishlistItem
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.WishlistViewModel

class MyWishlistAdapter(
    private val context: Context,
    private val wishlistViewModel: WishlistViewModel
) : RecyclerView.Adapter<MyWishlistAdapter.WishlistViewHolder>() {

    private val wishlistList: MutableList<WishlistItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_wishlist, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        holder.bind(wishlistList[position])
    }

    override fun getItemCount(): Int = wishlistList.size  // Cập nhật đúng số lượng

    fun setWishlistItems(newItems: List<WishlistItem>) {
        wishlistList.clear()
        wishlistList.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgViewWishlist: ImageView = itemView.findViewById(R.id.imgWishlistItem)
        private val imgDeleteItem: ImageView = itemView.findViewById(R.id.imgDeleteItem)
        private val tvWishlistItem: TextView = itemView.findViewById(R.id.tvWishlistItem)
        private val txtsellingPrice: TextView = itemView.findViewById(R.id.txtsellingPrice)
        private val txtoriginalPrice: TextView = itemView.findViewById(R.id.txtoriginalPrice)

        fun bind(wishlistItem: WishlistItem) {
            tvWishlistItem.text = wishlistItem.name
            txtsellingPrice.text = "$${wishlistItem.salePrice.toString()}"
            txtoriginalPrice.text = "$${wishlistItem.price.toString()}"
            txtoriginalPrice.paintFlags = txtoriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            Glide.with(itemView.context)
                .load(wishlistItem.thumbnail)
                .placeholder(R.drawable.banner_placeholder)
                .into(imgViewWishlist)

            imgDeleteItem.setOnClickListener {
                val productId = wishlistItem._id
                val token = TokenManager.getToken(context) ?: ""
                wishlistViewModel.addToWishlist(productId, false, token)
            }
        }
    }
}
