package com.example.cranny.network.googlebooks

import com.example.cranny.Book
import com.example.cranny.BookSuggestion
import com.example.cranny.model.BookDetailsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

val apiKey = "AIzaSyCAHY3tQWLYAxLa6guoUR_rlO5nM3eKF8Q"//will api key
val query = "android"
interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Response<SearchResults>

    @GET("volumes/{id}")
    suspend fun getBookDetails(
        @Path("id") id: String,
        @Query("key") apiKey: String
    ): Response<BookDetailsResponse>
}
