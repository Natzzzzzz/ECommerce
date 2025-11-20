package com.example.ecommerce.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddToWishlistRequest(
    @SerializedName("product_id") val _id: String,
    val status: Boolean
) : Parcelable

@Parcelize
data class AddToWishlistResponse(
    val success: Boolean,
    val message: String?
): Parcelable

@Parcelize
data class WishlistItem(
    @SerializedName("product_id") val _id: String,
    val name: String,
    val salePrice: Float,
    val price: Float,
    val thumbnail: String,
    val status: Boolean
): Parcelable