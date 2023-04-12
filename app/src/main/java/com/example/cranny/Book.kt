package com.example.cranny

data class Book(
    var id: String,
    var title: String,
    var authorNames: List<String>,
    var publicationDate: String?,
    var starRating: Int?,
    var publisher: String?,
    var description: String?,
    var pageCount: Int?,
    var thumbnail: String?,
    var journalEntry: String?,
    var userProgress: Int?,


)
