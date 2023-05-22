package com.example.cranny.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cranny.Book
import kotlinx.coroutines.launch

class BooksViewModel(private val googleBooksRepository: GoogleBooksRepository) : ViewModel() {
    private val _searchResults = MutableLiveData<List<Book>>() // Changed to Book
    val searchResults: LiveData<List<Book>> get() = _searchResults // Changed to Book

    private val _bookDetails = MutableLiveData<Book>() // Changed to Book
    val bookDetails: LiveData<Book> get() = _bookDetails // Changed to Book

    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                val books = googleBooksRepository.searchBooks(query)
                _searchResults.value = books
                Log.i("BooksViewModel", "searchBooks: ${_searchResults.value}")
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error occurred while searching books", e)
            }
        }
    }

    fun fetchBookDetails(id: String) {
        viewModelScope.launch {
            try {
                val book = googleBooksRepository.getBookDetails(id)
                _bookDetails.value = book!!
                Log.i("BooksViewModel", "fetchBookDetails: ${_bookDetails.value}")
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error occurred while fetching book details", e)
            }
        }
    }
}


