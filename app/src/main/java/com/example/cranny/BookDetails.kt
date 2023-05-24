package com.example.cranny

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import com.example.cranny.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class BookDetails : AppCompatActivity() {

    private lateinit var titleTV: TextView
    private lateinit var authorTV: TextView
    private lateinit var publisherTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var pageCountTV: TextView
    private lateinit var publishDateTV: TextView
    private lateinit var addBtn: Button
    private lateinit var bookIV: ImageView
    private lateinit var backButton: Button

    private lateinit var profileRepo: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book_details)
        titleTV = findViewById(R.id.idTVTitle)
        authorTV = findViewById(R.id.idTVSubTitle)
        publisherTV = findViewById(R.id.idTVpublisher)
        descriptionTV = findViewById(R.id.idTVDescription)
        pageCountTV = findViewById(R.id.idTVNoOfPages)
        publishDateTV = findViewById(R.id.idTVPublishDate)
        addBtn = findViewById(R.id.idBtnAdd)
        bookIV = findViewById(R.id.idIVbook)
        backButton = findViewById(R.id.idBtnBack)

        val book: Book? = intent.getSerializableExtra("book") as? Book
        if(book != null){
            titleTV.text = book.title
            authorTV.text = book.authorNames
            publisherTV.text = book.publisher
            descriptionTV.text = book.description
            pageCountTV.text = book.pageCount.toString()
            publishDateTV.text = book.publicationDate


            Picasso.get().load(book.thumbnail).into(bookIV)

        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // handle this case if needed
        } else {
            val userId = currentUser.uid
            val database = FirebaseDatabase.getInstance()
            profileRepo = ProfileRepository(database, userId)

            // Initialize addBtn here before setting the observer
            addBtn.setOnClickListener { view: View ->
                val book = intent.getSerializableExtra("book") as Book?
                if (book != null) {
                    book.totalPagesRead = 0
                    book.starRating = 0f
                    book.journalEntry = ""
                    book.userFinished = false
                    book.userProgress = 0
                    book.isFav = false
                    book.tags = ""
                    book.lastReadTime = 0
                    book.lastReadDate = 0
                    book.startDate = ""
                    book.purchasedFrom = ""
                    book.totalPageCount = 0


                    //Saves the created book and places it in the users library
                    val username: String? = profileRepo.profileData.value?.username
                    val user: Friend = Friend(userId, username as String, false)
                    val bookRepository = BookRepository(database, user)
                    bookRepository.addBook(book, this)
                    Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show()

                    //take user back to their library
                    val intent = Intent(this, LibraryActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No book to add", Toast.LENGTH_SHORT).show()
                }
            }

            profileRepo.profileData.observe(this@BookDetails, Observer { userProfile ->
                // Do something with userProfile if needed
            })
        }
        backButton.setOnClickListener {
            finish()
        }
    }

}
