package com.example.cranny
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    var title: String,
    var authorNames: String?,
    var publicationDate: String?,
    var starRating: Float?,
    var publisher: String?,
    var description: String?,
    var pageCount: Int?,
    var thumbnail: String?,
    var journalEntry: String?,
    var userProgress: Int?,
    var userFinished: Boolean,
    var isFav: Boolean?,
    var purchasedFrom: String?,
    var mainCharacters: String?,
    var genres: String?,
    var tags: String?,
    var lastReadDate: Long?,
    var lastReadTime: Long?,
    var prevReadCount: Int?,
    var startDate: String,
    var endDate: String
)
