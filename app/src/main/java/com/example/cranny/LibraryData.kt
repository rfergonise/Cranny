package com.example.cranny

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

data class LibraryData(
    val userID: String,
    val books: MutableList<Book> = mutableListOf()
)


class Library : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val libraryRecycler: RecyclerView = findViewById(R.id.rvLibraryBookList)
        libraryRecycler.adapter = LibraryBookAdapter(createBookData())
        libraryRecycler.layoutManager = LinearLayoutManager(this)
    }

    private fun createBookData(): List<LibraryBookRecyclerData> {

        val database = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(database)
        bookRepository.fetchBookData()
        bookRepository.isBookDataReady.observe(this) { isBookDataReady ->
            if (isBookDataReady) {
                for (books in bookRepository.Library) {
                    val bookAuthors = books.authorNames
                    val bookTitle = books.title
                    val bookImage = books.thumbnail

                    val bookCard = LibraryBookRecyclerData(bookTitle, bookAuthors, bookImage)

                }

            }

            bookRepository.stopBookListener()
        }

        return bookCard
    }


