package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class LibraryActivity : AppCompatActivity(), LibraryBookAdapter.onBookClickListener {

    private lateinit var libraryRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        libraryRecycler = findViewById(R.id.rvLibraryBookList)
        getLibraryRecyclerData()

        // Go back to Main Activity until menu is created
        val menuBTN = findViewById<ImageButton>(R.id.bpBackButton)
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
        val libraryRecyclerBookList = ArrayList<LibraryBookRecyclerData>()
        val database = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(database)
        bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
            if (isBookDataReady) {
                val bookCount = bookRepository.Library.size

                if (bookCount > 0) {
                    for (books in bookRepository.Library) {

                        libraryRecyclerBookList.add(
                            LibraryBookRecyclerData(
                                books.id,
                                books.title,
                                books.authorNames,
                                books.publicationDate,
                                books.starRating,
                                books.publisher,
                                books.description,
                                books.prevReadCount,
                                books.thumbnail,
                                books.journalEntry,
                                books.userFinished,
                                books.isFav,
                                books.purchasedFrom,
                                books.mainCharacters,
                                books.genres,
                                books.tags,
                                books.lastReadDate,
                                books.lastReadTime,
                                books.prevReadCount,
                                books.startDate,
                                books.endDate,
                            )
                        )
                        libraryRecycler.adapter =
                            LibraryBookAdapter(libraryRecyclerBookList, this@LibraryActivity)
                        libraryRecycler.layoutManager = LinearLayoutManager(this)
                        libraryRecycler.setHasFixedSize(true)

                    }
                }
            }
            bookRepository.stopBookListener()
        })
    }


    override fun onItemClick(position: Int) {
        val database = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(database)
        bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
            if (isBookDataReady) {
                val libraryBookList = ArrayList<LibraryBookRecyclerData>()
                val bookCount = bookRepository.Library.size
                if (bookCount > 0) {
                    for (books in bookRepository.Library) {
                        libraryBookList.add(
                            LibraryBookRecyclerData(
                                books.id,
                                books.title,
                                books.authorNames,
                                books.publicationDate,
                                books.starRating,
                                books.publisher,
                                books.description,
                                books.prevReadCount,
                                books.thumbnail,
                                books.journalEntry,
                                books.userFinished,
                                books.isFav,
                                books.purchasedFrom,
                                books.mainCharacters,
                                books.genres,
                                books.tags,
                                books.lastReadDate,
                                books.lastReadTime,
                                books.prevReadCount,
                                books.startDate,
                                books.endDate,
                            )
                        )
                    }
                }
                bookRepository.stopBookListener()

                if (libraryBookList.isNotEmpty()) {
                    val intent = Intent(this, BookPageActivity::class.java)
                    intent.putExtra("title", libraryBookList[position].bookTitle)
                    intent.putExtra("author", libraryBookList[position].bookAuthor)
                    intent.putExtra("publisher", libraryBookList[position].bookPublisher)
                    intent.putExtra("publicationDate", libraryBookList[position].bookPubDate)
                    intent.putExtra("rating", libraryBookList[position].bookRating)
                    intent.putExtra("tags", libraryBookList[position].bookTags)
                    intent.putExtra("genres", libraryBookList[position].bookGenres)
                    intent.putExtra("startDate", libraryBookList[position].bookStartDate)
                    intent.putExtra("finishedDate", libraryBookList[position].bookEndDate)
                    intent.putExtra("mainCharacters", libraryBookList[position].bookCharacters)
                    intent.putExtra("purchasedFrom", libraryBookList[position].bookPurchasedFrom)
                    intent.putExtra("userReview", libraryBookList[position].bookReview)
                    intent.putExtra("summary", libraryBookList[position].bookSummary)
                    startActivity(intent)
                }
            }
        })
    }
}

