package com.example.ecommerce.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Product(
    @SerializedName("_id") val id: String,
    @SerializedName("variant_id") val variantId: String,
    @SerializedName("name") val name: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("brand_name") val brandName: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("quantity_sold") val quantitySold: Int,
    @SerializedName("original_price") val originalPrice: Float,
    @SerializedName("selling_price") val sellingPrice: Float,
    @SerializedName("isFavorite") var isFavorite: Boolean // Chỉnh đúng với JSON
) : Parcelable


data class ProductTest(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = ""
)
