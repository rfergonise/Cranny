package com.example.cranny.model

data class BookDetailsResponse(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publishedDate: String?,
    val publisher: String?,
    val description: String?,
    val pageCount: Int?,
    val imageLinks: ImageLinks?,
    val categories: List<String>?,
    val industryIdentifiers: List<IndustryIdentifier>?

)


