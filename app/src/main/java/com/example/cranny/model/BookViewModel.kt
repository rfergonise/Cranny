package com.example.cranny.model

import androidx.lifecycle.LiveData
import com.example.cranny.BuildConfig
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cranny.Book
import com.example.cranny.BookRepository
import com.example.cranny.network.googlebooks.GoogleBooksApi
import com.example.cranny.network.googlebooks.RetrofitInstance
import com.example.cranny.network.googlebooks.apiKey
import kotlinx.coroutines.launch

class BooksViewModel(private val bookRepository: BookRepository) : ViewModel() {
    val searchResults = MutableLiveData<List<Book>>()

    // Live data that will hold the book details
    private val _bookDetails = MutableLiveData<Book>()
    val bookDetails: LiveData<Book> = _bookDetails

    // Function to fetch book details
    fun fetchBookDetails(id: String) {
        viewModelScope.launch {
            val book = bookRepository.getBookDetails(id)
            book?.let {
                _bookDetails.value = it
            }
        }
    }



    fun searchBooks(query: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.googleBooksApi.searchBooks(query, BuildConfig.GOOGLE_BOOKS_API_KEY)
            if (response.isSuccessful) {
                val results = response.body()?.items?.map { volume ->
                    val volumeInfo = volume.volumeInfo
                    Book(
                        id = volume.id,
                        title = volumeInfo.title,
                        authorNames = volumeInfo.authors?.joinToString(", "),
                        publicationDate = "",
                        starRating = 0f,
                        publisher = volumeInfo.publisher,
                        description = volumeInfo.description,
                        pageCount = 0,
                        thumbnail = volumeInfo.imageLinks?.thumbnail,
                        journalEntry = "",
                        userProgress = 0,
                        userFinished = false,
                        startDate = "",
                        endDate = "",
                        prevReadCount = 0,
                        purchasedFrom = "",
                        mainCharacters = "",
                        genres = "",
                        tags = "",
                        lastReadDate = 0,
                        lastReadTime = 0,
                        isFav = false,
                        totalPagesRead = 0,
                        totalPageCount = 0
                    )
                } ?: emptyList()
                searchResults.postValue(results)
            }
        }
    }
}