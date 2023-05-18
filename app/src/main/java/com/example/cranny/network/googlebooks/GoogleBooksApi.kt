package com.example.cranny.network.googlebooks



import com.example.cranny.model.BookDetailsResponse
import com.example.cranny.model.BookSearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

object Constants{
    const val API_KEY = "AIzaSyCAHY3tQWLYAxLa6guoUR_rlO5nM3eKF8Q"//will api key
}
interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String = Constants.API_KEY
    ): Response<BookSearchResult>

    @GET("volumes/{id}")
    suspend fun getBookDetails(
        @Path("id") id: String,
        @Query("key") apiKey: String = Constants.API_KEY
    ): Response<BookDetailsResponse>
}
