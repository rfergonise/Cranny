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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // attach to the ui elements
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

        // get the book they clicked on
        val book: Book? = intent.getSerializableExtra("book") as? Book

        if (book != null) {
            // if the pass was successful
            // load the book's info into the ui
            titleTV.text = book.title
            authorTV.text = book.authorNames
            publisherTV.text = book.publisher
            descriptionTV.text = book.description
            pageCountTV.text = book.pageCount.toString()
            publishDateTV.text = book.publicationDate
            Picasso.get().load(book.thumbnail).into(bookIV)
            // all them to click add or back after loading info
            AllowButtonLogic(book)
        }
        else finish()
    }

    private fun AllowButtonLogic(book: Book)
    {
        // Add Button On Click Listener
        addBtn.setOnClickListener { view: View ->
            Toast.makeText(this, "Adding book...", Toast.LENGTH_SHORT).show()
            // declare and initialize needed firebase variables
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser!!.uid
            val database = FirebaseDatabase.getInstance()
            // get the user's username
            val profileRepo = ProfileRepository(database, userId)
            profileRepo.profileData.observe(this, Observer { userProfile ->
                val username: String = userProfile.username
                // create a user object
                val user = Friend(userId, username, false)
                // get access to the user's book data
                val bookRepository = BookRepository(database, user)
                // add the new book
                bookRepository.addBook(book, this)
                Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show()
                //take user back to their library
                val intent = Intent(this, LibraryActivity::class.java)
                startActivity(intent)
            })
            profileRepo.stopProfileListener()
        }

        // Back Button On Click Listener
        backButton.setOnClickListener {
            finish()
        }
    }
}
