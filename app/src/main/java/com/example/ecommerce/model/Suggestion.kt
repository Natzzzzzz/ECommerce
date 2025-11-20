package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchSuggestionResponse(
    val suggestions: List<Suggestion>
) : Parcelable

@Parcelize
data class Suggestion(
    val _id: String,
    val keyword: String,
    val search_count: Int
) : Parcelable
