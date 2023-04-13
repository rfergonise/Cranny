package com.example.cranny

data class Library(
    val userID: String,
    val books: MutableList<Book> = mutableListOf()
)
