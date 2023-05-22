package com.example.cranny.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cranny.model.GoogleBooksRepository
import com.example.cranny.network.googlebooks.RetrofitInstance

class BooksViewModelFactory : ViewModelProvider.Factory {

    private val googleBookRepository = GoogleBooksRepository(RetrofitInstance.googleBooksApi)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(googleBookRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}