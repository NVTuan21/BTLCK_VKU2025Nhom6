package com.example.btlck_ltdd_nhom6.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.btlck_ltdd_nhom6.repository.BookstoreRepository

class BookstoreViewModelFactory(
    private val repository: BookstoreRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookstoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookstoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}