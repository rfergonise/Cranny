package com.example.cranny.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cranny.network.googlebooks.GoogleBooksApi
import com.example.cranny.model.BooksViewModel
import com.example.cranny.network.googlebooks.RetrofitInstance
import com.example.cranny.BookRepository

class BooksViewModelFactory(private val bookRepository: BookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(bookRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}