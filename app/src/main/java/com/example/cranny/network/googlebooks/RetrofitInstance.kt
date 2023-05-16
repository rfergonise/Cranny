package com.example.cranny.network.googlebooks

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/" //need to update this to correct url

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val googleBooksApi: GoogleBooksApi by lazy {
        retrofit.create(GoogleBooksApi::class.java)
    }
}