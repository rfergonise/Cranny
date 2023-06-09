package com.example.cranny.model

import com.google.gson.annotations.SerializedName

data class BookSearchResult(
    @SerializedName("items")
    val items: List<BookItem>
)

data class BookItem(
    @SerializedName("volumeInfo")
    val volumeInfo: BookVolumeInfo,

    @SerializedName("id")
    val id: String,
)

data class BookVolumeInfo(
    @SerializedName("title")
    val title: String,

    @SerializedName("authors")
    val authors: List<String>?,

    @SerializedName("publisher")
    val publisher: String?,

    @SerializedName("publishedDate")
    val publishedDate: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("imageLinks")
    val imageLinks: ImageLinks?,

    @SerializedName("categories")
    val categories: List<String>?,

    @SerializedName("industryIdentifiers")
    val industryIdentifiers: List<IndustryIdentifier>?,

    @SerializedName("pageCount")
    val pageCount: Int?
)

data class ImageLinks(
    @SerializedName("thumbnail")
    val thumbnail: String?
)

data class IndustryIdentifier(
    @SerializedName("type")
    val type: String,

    @SerializedName("identifier")
    val identifier: String
)