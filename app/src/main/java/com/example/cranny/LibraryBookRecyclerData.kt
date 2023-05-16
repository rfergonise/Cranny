package com.example.cranny

import android.support.annotation.DrawableRes
import androidx.room.PrimaryKey

data class LibraryBookRecyclerData(
    val bookID: String,
    var bookTitle: String,
    var bookAuthor: String?,
    var bookPubDate: String?,
    var bookRating: Float?,
    var bookPublisher: String?,
    var bookSummary: String?,
    var bookLastPage: Int?,
    var bookImage: String?,
    var bookReview: String?,
    var bookFinished: Boolean,
    var bookIsFav: Boolean?,
    var bookPurchasedFrom: String?,
    var bookCharacters: String?,
    var bookGenres: String?,
    var bookTags: String?,
    var bookLastReadDate: Long?,
    var bookLastReadTime: Long?,
    var bookPrevReadCount: Int?,
    var bookStartDate: String,
    var bookEndDate: String?,
    var totalPagesRead: Int
)
