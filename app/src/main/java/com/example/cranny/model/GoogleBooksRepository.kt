package com.example.cranny.model

import android.content.Context
import android.widget.Toast
import com.example.cranny.Book
import com.example.cranny.BuildConfig
import com.example.cranny.network.googlebooks.Constants.API_KEY
import com.example.cranny.network.googlebooks.GoogleBooksApi
import com.example.cranny.network.googlebooks.RetrofitInstance

class GoogleBooksRepository(private val api: GoogleBooksApi) {
    suspend fun searchBooks(query: String): List<Book> {
        val response = api.searchBooks(query, API_KEY)
        if (response.isSuccessful) {
            return response.body()?.items?.map { volume ->
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
        } else {
            throw Exception("Error fetching books")
        }
    }

    suspend fun getBookDetails(id: String): Book? {
        val response = RetrofitInstance.googleBooksApi.getBookDetails(id)

        if (response.isSuccessful) {
            return response.body()?.let { bookDetailsResponse ->
                // Map the response to your Book model
                // Please replace the mapping logic according to your needs
                Book(
                    id = bookDetailsResponse.id,
                    title = bookDetailsResponse.volumeInfo.title,
                    authorNames = bookDetailsResponse.volumeInfo.authors?.joinToString(", "),
                    publicationDate = "",
                    starRating = 0f,
                    publisher = bookDetailsResponse.volumeInfo.publisher,
                    description = bookDetailsResponse.volumeInfo.description,
                    pageCount = 0,
                    thumbnail = bookDetailsResponse.volumeInfo.imageLinks?.thumbnail,
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
            }
        } else {
            // Handle the error according to your requirements
            return null
        }
    }
}