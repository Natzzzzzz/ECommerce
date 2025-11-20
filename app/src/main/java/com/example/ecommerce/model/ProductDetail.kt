package com.example.ecommerce.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductResponse(
    @SerializedName("productData") val productData: ProductData,
    @SerializedName("variants") val variants: List<Variant>
) : Parcelable

@Parcelize
data class ProductData(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("brand_name") val brandName: String,
    @SerializedName("description") val description: String,
    @SerializedName("images") val images: List<String>,
    @SerializedName("variantDefault") val variantDefault: String,
    @SerializedName("shop_id") val shopId: ShopInfo,
    @SerializedName("wishlist") val wishlist: Boolean?
) : Parcelable

@Parcelize
data class Variant(
    @SerializedName("_id") val id: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("salePrice") val salePrice: Double?,
    @SerializedName("stock") val stock: Int,
    @SerializedName("images") val images: String,
    @SerializedName("attributes") val attributes: List<Attribute>
) : Parcelable

@Parcelize
data class Attribute(
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: String
) : Parcelable

@Parcelize
data class ShopInfo(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("owner") val owner: String,
    @SerializedName("logo") val logo: String,
    @SerializedName("description") val description: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("followers") val followers: Int,
    @SerializedName("isActive") val isActive: Boolean
) : Parcelable