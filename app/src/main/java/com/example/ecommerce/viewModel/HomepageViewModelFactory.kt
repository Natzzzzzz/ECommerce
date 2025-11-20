package com.example.ecommerce.features.home_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import drawable.home_page.HomepageRepository

class HomepageViewModelFactory(private val repository: HomepageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomepageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomepageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
