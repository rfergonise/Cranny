package com.example.cranny

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide



class EditBook : AppCompatActivity() {

    // EditText fields for book information
    private lateinit var editTextTitle: EditText
    private lateinit var editTextAuthor: EditText
    private lateinit var editTextPublishers: EditText
    private lateinit var editTextPublicationDate: EditText
    private lateinit var editTextGenres: EditText
    private lateinit var editTextTags: EditText
    private lateinit var editTextPurchasedFrom: EditText
    private lateinit var editTextSummary: EditText
    private lateinit var editTextMainCharacters: EditText
    private lateinit var editTextMyReview: EditText

    private lateinit var saveButton: Button
//    val bookRepository = BookRepository(database, Friend(userId, username, false))

    //Firebase Access
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private lateinit var username: String

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

//        bookRepository.updateBookData(updatedBook)

        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, currentUser!!.uid)






        //back to Book Page Activity button
        val declineBTN = findViewById<ImageButton>(R.id.bpDeclineButton)
        declineBTN.setOnClickListener {
            val intent = Intent(this, BookPageActivity::class.java)
            startActivity(intent)

            // Retrieve references to other EditText fields
            editTextTitle = findViewById(R.id.tvbpBookTitle)
            editTextAuthor = findViewById(R.id.tvbpAuthors)
            editTextGenres = findViewById(R.id.tvbpGenres)
            editTextPublishers = findViewById(R.id.tvbpPublisher)
            editTextPublicationDate = findViewById(R.id.tvbpPublicationDate)
            editTextTags = findViewById(R.id.tvbpTags)
            editTextPurchasedFrom = findViewById(R.id.tvbpPurchasedFrom)
            editTextSummary = findViewById(R.id.tvbpSummary)
            editTextMainCharacters = findViewById(R.id.tvbpMainCharacters)
            editTextMyReview = findViewById(R.id.tvbpReview)

            //save button click
            saveButton = findViewById(R.id.btnSave)

            saveButton.setOnClickListener {
                // Get text from EditText fields
                val title = editTextTitle.text.toString()
                val author = editTextAuthor.text.toString()
                val genre = editTextGenres.text.toString()
                val publisher = editTextPublishers.toString()
                val publicationDate = editTextPublicationDate.toString()
                val tags = editTextTags.toString()
                val purchaseFrom = editTextPurchasedFrom.toString()
                val summary = editTextSummary.toString()
                val mainCharacters = editTextMainCharacters.toString()
                val myReview = editTextMyReview.toString()
            }
        }
    }
}