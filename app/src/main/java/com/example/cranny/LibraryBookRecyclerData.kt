package com.example.cranny

import android.support.annotation.DrawableRes

data class LibraryBookRecyclerData(
    val libraryBookTitle: String,
    val libraryAuthorsName: String?,
    // @DrawableRes
    val libraryBookImage: String?
)
