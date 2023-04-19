package com.example.cranny

data class SocialFeed(
    val id: String,
    val bookTitle: String,
    val bookAuthor: String,
    val isBookComplete: Boolean,
    val status: String,
    val bookCoverURL: String,
    val lastReadDate: String,
    val lastReadTime: String,
    val username: String
)