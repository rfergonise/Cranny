package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LibraryActivity : AppCompatActivity(), LibraryBookAdapter.onBookClickListener {

    private lateinit var libraryRecycler: RecyclerView
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    var username : String = "admin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        libraryRecycler = findViewById(R.id.rvLibraryBookList)
        getLibraryRecyclerData()

        // Go back to Main Activity until menu is created
        val menuBTN = findViewById<Button>(R.id.btLibraryActivity)
        menuBTN.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
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
        val profileRepo = ProfileRepository(database, currentUser!!.uid)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            username = userProfile.username
            val bookRepository = BookRepository(database, Friend(currentUser!!.uid, username, false))
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
                                    books.pageCount,
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
                                    books.totalPagesRead,
                                    username,
                                )
                            )}
                            libraryRecycler.adapter =
                                LibraryBookAdapter(libraryRecyclerBookList, this@LibraryActivity)
                            libraryRecycler.layoutManager = LinearLayoutManager(this)
                            libraryRecycler.setHasFixedSize(true)
                    }
                }
                bookRepository.stopBookListener()
            })
        })
        profileRepo.stopProfileListener()
    }


    override fun onItemClick(position: Int) {

        if (username != "admin") {

            val database = FirebaseDatabase.getInstance()
            val bookRepository =
                BookRepository(database, Friend(currentUser!!.uid, username, false))
            bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
                if (isBookDataReady) {
                    val libraryBookList = ArrayList<Book>()
                    val bookCount = bookRepository.Library.size
                    if (bookCount > 0) {
                        for (books in bookRepository.Library) {
                            libraryBookList.add(
                                Book(
                                    books.id,
                                    books.title,
                                    books.authorNames,
                                    books.publicationDate,
                                    books.starRating,
                                    books.publisher,
                                    books.description,
                                    books.pageCount,
                                    books.thumbnail,
                                    books.journalEntry,
                                    books.userProgress,
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
                                    books.totalPageCount,
                                    books.totalPagesRead,
                            ))
                        }
                    }
                    bookRepository.stopBookListener()

                    if (libraryBookList.isNotEmpty()) {
                        val intent = Intent(this, BookPageActivity::class.java)
                        intent.putExtra("title", libraryBookList[position].title)
                        intent.putExtra("author", libraryBookList[position].authorNames)
                        intent.putExtra("publisher", libraryBookList[position].publisher)
                        intent.putExtra("publicationDate", libraryBookList[position].publicationDate)
                        intent.putExtra("rating", libraryBookList[position].starRating)
                        intent.putExtra("tags", libraryBookList[position].tags)
                        intent.putExtra("genres", libraryBookList[position].genres)
                        intent.putExtra("startDate", libraryBookList[position].startDate)
                        intent.putExtra("userFinished", libraryBookList[position].userFinished)
                        intent.putExtra("finishedDate", libraryBookList[position].endDate)
                        intent.putExtra("mainCharacters", libraryBookList[position].mainCharacters)
                        intent.putExtra("purchasedFrom", libraryBookList[position].purchasedFrom)
                        intent.putExtra("userReview", libraryBookList[position].journalEntry)
                        intent.putExtra("summary", libraryBookList[position].description)
                        //for some reason page count works but not total pages read, will leave as is for now
                        intent.putExtra("lastPageRead", libraryBookList[position].pageCount)
                        intent.putExtra("id", libraryBookList[position].id)
                        intent.putExtra("isFav", libraryBookList[position].isFav)
                        startActivity(intent)
                    }
                }
            })
        }
    }
}
