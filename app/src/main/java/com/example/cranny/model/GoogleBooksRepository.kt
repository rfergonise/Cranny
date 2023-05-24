package com.example.cranny.model

import android.util.Log
import com.example.cranny.Book
import com.example.cranny.network.googlebooks.Constants.API_KEY
import com.example.cranny.network.googlebooks.GoogleBooksApi


class GoogleBooksRepository(private val api: GoogleBooksApi) {
    suspend fun searchBooks(query: String): List<Book> {
        val response = api.searchBooks(query, API_KEY)

        if (response.isSuccessful) {
            return response.body()?.items?.map { bookItem ->
                mapBookItemToBook(bookItem)
            } ?: emptyList()
        } else {
            throw Exception("Error fetching books")
        }
    }

    private fun mapBookItemToBook(bookItem: BookItem): Book {
        val isbn = bookItem.volumeInfo.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier
        return Book(
            id = bookItem.id,
            title = bookItem.volumeInfo.title,
            authorNames = bookItem.volumeInfo.authors.toString(),
            publicationDate = bookItem.volumeInfo.publishedDate,
            starRating = 0f,
            publisher = bookItem.volumeInfo.publisher,
            description = bookItem.volumeInfo.description,
            pageCount = bookItem.volumeInfo.pageCount,
            thumbnail = bookItem.volumeInfo.imageLinks?.thumbnail,
            journalEntry = "",
            userProgress = 0,
            userFinished = false,
            isFav = false,
            purchasedFrom = "",
            mainCharacters = "",
            genres = bookItem.volumeInfo.categories?.joinToString(", "),
            tags = "",
            lastReadDate = 0,
            lastReadTime = 0,
            prevReadCount = 0,
            startDate = "",
            endDate = "",
            totalPageCount = 0,
            totalPagesRead = 0,
            isbn = isbn
        )
    }

    suspend fun getBookDetails(id: String): Book? {
        val response = api.getBookDetails(id, API_KEY)

        if (response.isSuccessful) {
            return response.body()?.let { bookDetailsResponse ->
                mapBookDetailsResponseToBook(bookDetailsResponse)
            }
        } else {
            Log.e("GoogleBooksRepository", "Response unsuccessful, error: ${response.errorBody()?.string()}")
            return null
        }
    }

    private fun mapBookDetailsResponseToBook(bookDetailsResponse: BookDetailsResponse): Book {
        val isbn = bookDetailsResponse.volumeInfo.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier
        return Book(
            id = bookDetailsResponse.id,
            title = bookDetailsResponse.volumeInfo.title,
            authorNames = bookDetailsResponse.volumeInfo.authors.toString(),
            publicationDate = bookDetailsResponse.volumeInfo.publishedDate,
            starRating = 0f,
            publisher = bookDetailsResponse.volumeInfo.publisher,
            description = bookDetailsResponse.volumeInfo.description,
            pageCount = bookDetailsResponse.volumeInfo.pageCount,
            thumbnail = bookDetailsResponse.volumeInfo.imageLinks?.thumbnail,
            journalEntry = "",
            userProgress = 0,
            userFinished = false,
            isFav = false,
            purchasedFrom = "",
            mainCharacters = "",
            genres = bookDetailsResponse.volumeInfo.categories?.joinToString(", "),
            tags = "",
            lastReadDate = 0,
            lastReadTime = 0,
            prevReadCount = 0,
            startDate = "",
            endDate = "",
            totalPageCount = 0,
            totalPagesRead = 0,
            isbn = isbn
        )
    }
}