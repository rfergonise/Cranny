package com.example.cranny.network.googlebooks

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Response<SearchResults>
}