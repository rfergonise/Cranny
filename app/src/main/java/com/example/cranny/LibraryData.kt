package com.example.cranny

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

data class LibraryData(
    val userID: String,
    val books: MutableList<Book> = mutableListOf()
)


class Library : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }
}

