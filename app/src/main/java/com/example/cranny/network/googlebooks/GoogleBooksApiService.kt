package com.example.cranny.network.googlebooks

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleBooksApiService {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    private val gson = GsonBuilder().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val googleBooksApi: GoogleBooksApi = retrofit.create(GoogleBooksApi::class.java)
}