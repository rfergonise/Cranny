package com.example.cranny
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

import java.util.*


@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    var title: String,
    var authorNames: String?,
    var publicationDate: String?,
    var starRating: Float? = 0f,
    var publisher: String?,
    var description: String?,
    var pageCount: Int?,
    var thumbnail: String?,
    var journalEntry: String? = "",
    var userProgress: Int? = 0,
    var userFinished: Boolean = false,
    var isFav: Boolean? = false,
    var purchasedFrom: String? = "",
    var mainCharacters: String? = "",
    var genres: String? = "",
    var tags: String? = "",
    var lastReadDate: Long? = 0,
    var lastReadTime: Long? = 0,
    var prevReadCount: Int? = 0,
    var startDate: String = "",
    var endDate: String? = "",
    var totalPageCount: Int? = 0,
    var totalPagesRead: Int = 0,
    var isbn: String? = "",


) : Serializable

