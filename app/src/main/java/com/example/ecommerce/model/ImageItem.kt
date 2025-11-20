package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageItem(
    val _id : String,
    val title: String,
    val discountInfo: String,
    val description: String,
    val image: String,
    val startTime: String,
    val endTime: String
) : Parcelable