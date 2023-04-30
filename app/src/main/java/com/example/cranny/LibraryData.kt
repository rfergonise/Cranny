package com.example.cranny


data class LibraryData(
    val userID: String,
    val books: MutableList<Book> = mutableListOf()
)



