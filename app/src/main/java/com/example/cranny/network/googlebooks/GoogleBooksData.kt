package com.example.cranny.network.googlebooks


data class SearchResults(val items: List<Volume>)

data class Volume(val id: String, val volumeInfo: VolumeInfo)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val description: String?,
    val imageLinks: ImageLinks?,
    val genre: String?
)

data class ImageLinks(val thumbnail: String?)