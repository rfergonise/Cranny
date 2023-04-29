package com.example.cranny

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val libraryRecycler: RecyclerView = findViewById(R.id.rvLibraryBookList)
        libraryRecycler.adapter = LibraryBookAdapter(getLibraryRecyclerData())
        libraryRecycler.layoutManager = LinearLayoutManager(this)
        libraryRecycler.setHasFixedSize(true)
    }

    private fun getLibraryRecyclerData(): ArrayList<LibraryBookRecyclerData> {
        val libraryBookList = ArrayList<LibraryBookRecyclerData>()

        val database = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(database)
        bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
            if (isBookDataReady) {
                val bookCount = bookRepository.Library.size

                if (bookCount > 0) {
                    for (books in bookRepository.Library) {
                        val bookAuthors = books.authorNames
                        val bookTitle = books.title
                        val bookImage = books.thumbnail

                        val bookCard = LibraryBookRecyclerData(bookTitle, bookAuthors, bookImage)
                        libraryBookList.add(bookCard)
                    }

                    //libraryRecycler.layoutManager = LinearLayoutManager(this)
                    //libraryRecycler.setHasFixedSize(true)
                    //libraryRecycler.adapter = LibraryBookAdapter(libraryBookList)
                }
            }
            bookRepository.stopBookListener()
        })
        return libraryBookList
    }
}
