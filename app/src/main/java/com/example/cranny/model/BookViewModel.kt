package com.example.cranny.model

import androidx.lifecycle.LiveData
import com.example.cranny.BuildConfig
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cranny.Book
import com.example.cranny.BookRepository

import com.example.cranny.network.googlebooks.RetrofitInstance

import kotlinx.coroutines.launch

class BooksViewModel(private val bookRepository: GoogleBooksRepository) : ViewModel() {
    private val _searchResults = MutableLiveData<List<Book>>()
    val searchResults: LiveData<List<Book>> get() = _searchResults

    private val _bookDetails = MutableLiveData<Book>()
    val bookDetails: LiveData<Book> get() = _bookDetails

    fun searchBooks(query: String) {
        viewModelScope.launch {
            val books = bookRepository.searchBooks(query)
            _searchResults.value = books
        }
    }

    fun fetchBookDetails(id: String) {
        viewModelScope.launch {
            val book = bookRepository.getBookDetails(id)
            _bookDetails.value = book
        }
    }
}


