package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val _id : String,
    val parent_id: Int,
    val name: String,
    val icon: String,
    val total_sales: String,
) : Parcelable