package com.example.ecommerce.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddToCartRequest(
    @SerializedName("variant_id") val variantId: String,
    val quantity: Int
) : Parcelable

@Parcelize
data class AddToCartResponse(
    val message: String,
    val cartItem: CartItem,
): Parcelable

@Parcelize
data class CartQuantityResponse(
    val message: String,
    val quantityItemsCart: Int
): Parcelable

@Parcelize
data class CartItem(
    val _id: String,
    val user_id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
): Parcelable

@Parcelize
data class CartResponseItem(
    val productId: String,
    val item_id: String,
    val name: String,
    val thumbnail: String,
    val originalPrice: Double,
    val sellingPrice: Double,
    val quantity: Int,
    val shopName: String,
    val shopId: String,
    val stock: Int,
    val attributes: List<AttributePCart>
): Parcelable

@Parcelize
data class AttributePCart(
    val value: String
) : Parcelable

@Parcelize
data class CartDeleteResponse(
    val message: String
) : Parcelable

@Parcelize
data class ListCartItemResponse(
    val message: String,
    val page : Int,
    val limit: Int,
    val cart: CartItem,
    val items: List<CartResponseItem>
): Parcelable

@Parcelize
data class UpdateCartRequest(
    val item_id: String,
    val quantity: Int
): Parcelable

@Parcelize
data class UpdateCartResponse(
    val message: String,
    val updatedItem: UpdatedItem
): Parcelable

@Parcelize
data class UpdatedItem(
    val item_id: String,
    val quantity: Int
): Parcelable

