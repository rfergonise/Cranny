package com.example.cranny

import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class LibraryActivity : AppCompatActivity() {

    private lateinit var libraryRecycler: RecyclerView
    private val libraryBookList = ArrayList<LibraryBookRecyclerData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        libraryRecycler = findViewById(R.id.rvLibraryBookList)
        getLibraryRecyclerData()

        // Go back to Main Activity until menu is created
        val menuBTN = findViewById<ImageButton>(R.id.menuButton)
        menuBTN.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Floating add book button, goes to Add Book Page
        val addBookBTN = findViewById<FloatingActionButton>(R.id.fbAddBook)
        addBookBTN.setOnClickListener {
            val intent = Intent(this, AddBookPage::class.java)
            startActivity(intent)
        }
    }

    private fun getLibraryRecyclerData() {
        //val libraryBookList = ArrayList<LibraryBookRecyclerData>()

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

                        libraryBookList.add(LibraryBookRecyclerData(bookTitle, bookAuthors, bookImage))

                    }

                    libraryRecycler.adapter = LibraryBookAdapter(libraryBookList)
                    libraryRecycler.layoutManager = LinearLayoutManager(this)
                    libraryRecycler.setHasFixedSize(true)
                }
            }
            bookRepository.stopBookListener()
        })
    }

}
