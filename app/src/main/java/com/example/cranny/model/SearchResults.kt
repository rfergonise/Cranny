package com.example.cranny.model

import com.google.gson.annotations.SerializedName

data class BookSearchResult(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: BookVolumeInfo
)

data class BookVolumeInfo(
    val title: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val imageLinks: ImageLinks,
    val categories: String,
)

data class ImageLinks(
    val thumbnail: String
)