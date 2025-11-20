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
import com.example.ecommerce.features.cart.CartViewModel
import com.example.ecommerce.model.CartResponseItem
import com.example.ecommerce.utils.TokenManager
import com.example.ecommerce.viewModel.WishlistViewModel

class CartAdapter(
    private val context: Context,
    private val cartViewModel: CartViewModel
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartItemList: MutableList<CartResponseItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItemList[position])
    }

    override fun getItemCount(): Int = cartItemList.size  // Cập nhật đúng số lượng

    fun updateCartItems(newItems: List<CartResponseItem>) {
        cartItemList.clear()
        cartItemList.addAll(newItems)
        notifyDataSetChanged()
    }


    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgCartItem: ImageView = itemView.findViewById(R.id.imgCartItem)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvAttribute: TextView = itemView.findViewById(R.id.tvAttribute)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvquantity)
        private val txtoriginalPrice: TextView = itemView.findViewById(R.id.txtoriginalPrice)
        private val txtsellingPrice: TextView = itemView.findViewById(R.id.txtsellingPrice)
        private val imgDeleteItem: ImageView = itemView.findViewById(R.id.imgDeleteItem)
        private val btnIncrease: ImageView = itemView.findViewById(R.id.increaseQty)
        private val btnDecrease: ImageView = itemView.findViewById(R.id.decreaseQty)


        fun bind(cartItem: CartResponseItem) {
            val token = TokenManager.getToken(context) ?: ""
            tvProductName.text = cartItem.name
            val attributesString = if (cartItem.attributes.isNotEmpty()) {
                cartItem.attributes.joinToString(" ") { it.value }
            }else {
                "No attributes"
            }
            tvAttribute.text = attributesString
            tvQuantity.text = cartItem.quantity.toString()
            txtoriginalPrice.text = "$${cartItem.originalPrice.toString()}"
            txtsellingPrice.text = "$${cartItem.sellingPrice.toString()}"
            txtoriginalPrice.paintFlags = txtoriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            Glide.with(itemView.context)
                .load(cartItem.thumbnail)
                .placeholder(R.drawable.banner_placeholder)
                .into(imgCartItem)
            imgDeleteItem.setOnClickListener {

                cartViewModel.removeItemFromCart(token, cartItem.item_id)
            }
            val newQuantity = cartItem.quantity + 1
            if (newQuantity <= cartItem.stock) {
                btnIncrease.isEnabled = true
                btnIncrease.alpha = 1.0f
            }else{
                btnIncrease.alpha = 0.5f
                btnIncrease.isEnabled = false
            }
            // Xử lý nút tăng
            btnIncrease.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                if (newQuantity <= cartItem.stock) {
                    cartViewModel.updateCartItemQuantity(token,cartItem.item_id, newQuantity)
                }
            }

            // Xử lý nút giảm
            btnDecrease.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                cartViewModel.updateCartItemQuantity(token,cartItem.item_id, newQuantity)
            }


        }
    }
}