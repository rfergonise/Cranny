package com.example.cranny

import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cranny.databinding.ActivityLibraryBinding
import com.example.cranny.databinding.ActivitySettingsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class LibraryActivity : AppCompatActivity() {

    private lateinit var libraryRecycler: RecyclerView
    private val libraryBookList = ArrayList<LibraryBookRecyclerData>()

    // Used for view binding
    lateinit var activityLibraryBinding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        // binding activity to menu
        activityLibraryBinding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(activityLibraryBinding.root)

        libraryRecycler = findViewById(R.id.rvLibraryBookList)
        getLibraryRecyclerData()

        // Go back to Dashboard Activity
        val btLibraryActivity : Button = findViewById(R.id.btLibraryActivity)
        btLibraryActivity.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
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
