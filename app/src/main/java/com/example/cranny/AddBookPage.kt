package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.database.FirebaseDatabase

class AddBookPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book_page)

        val titleInput = findViewById<EditText>(R.id.etTitleInput)
        val dataStartedInput = findViewById<TextView>(R.id.tvDateStarted)
        val authorInput = findViewById<EditText>(R.id.etAuthorInput)
        val summaryInput = findViewById<EditText>(R.id.etSummaryInput)
        val purchasedFromInput = findViewById<EditText>(R.id.etPurchasedFrom)
        val reviewInput = findViewById<EditText>(R.id.etReview)
        val publisherInput = findViewById<EditText>(R.id.etPublisherInput)
        val publicationDateInput = findViewById<EditText>(R.id.etnPublicationDateInput)
        val mainCharactersInput = findViewById<EditText>(R.id.etMainCharactersInput)
        val genresInput = findViewById<EditText>(R.id.tiGenres)
        val tagsInput = findViewById<EditText>(R.id.tiTags)
        val ratingsInput = findViewById<RatingBar>(R.id.ratingBar)
        val cancelBottomBTN = findViewById<Button>(R.id.btnCancel)
        val saveBottomBTN = findViewById<Button>(R.id.btnSave)
        val cancelTopBTN = findViewById<ImageButton>(R.id.ibCancelButton)
        val saveTopBTN = findViewById<ImageButton>(R.id.ibSaveButton)

        val finishedCB = findViewById<CheckBox>(R.id.cbFinished)
        val dateFinishedTextView = findViewById<TextView>(R.id.tvDateFinished)
        val dateFinishedInput = findViewById<EditText>(R.id.etDateFinished)
        val lastPageReadTextView = findViewById<TextView>(R.id.tvPageRead)
        val lastPageReadInput = findViewById<EditText>(R.id.tnPageNumber)

        // Hide Data Finished or Last Page Read based on finished checkbox
        finishedCB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (finishedCB.isChecked) {
                dateFinishedTextView.visibility = View.VISIBLE
                dateFinishedInput.visibility = View.VISIBLE
                lastPageReadTextView.visibility = View.INVISIBLE
                lastPageReadInput.visibility = View.INVISIBLE
            } else {
                dateFinishedTextView.visibility = View.INVISIBLE
                dateFinishedInput.visibility = View.INVISIBLE
                lastPageReadTextView.visibility = View.VISIBLE
                lastPageReadInput.visibility = View.VISIBLE
            }
        }

        saveBottomBTN.setOnClickListener {
            val newBook = Book(
                // need to create new id with each book
                id = " ",
                title = titleInput.toString(),
                authorNames = authorInput.toString(),
                publicationDate = publicationDateInput.toString(),
                starRating = ratingsInput.toString().toInt(),
                publisher = publisherInput.toString(),
                description = summaryInput.toString(),
                pageCount = lastPageReadInput.toString().toInt(),
                // will need to work with Ethan about how to scrape book image from Google API
                thumbnail = " ",
                journalEntry = reviewInput.toString(),
                userProgress = 0,
                userFinished = finishedCB.isChecked,
                startDate = dataStartedInput.toString(),
                endDate = dateFinishedInput.toString(),
                prevReadCount = 0,
                purchasedFrom = purchasedFromInput.toString(),
                mainCharacters = mainCharactersInput.toString(),
                genres = genresInput.toString(),
                tags = tagsInput.toString(),
                lastReadDate = " ",
                lastReadTime = " ",
                isFav = false
            )

            // need to work with Ethan about how to add books to database
            val database = FirebaseDatabase.getInstance()
            val bookRepository = BookRepository(database)
            bookRepository.addBook(newBook)

            Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LibraryData::class.java)
            startActivity(intent)
        }

        saveTopBTN.setOnClickListener {
            val newBook = Book(
                // need to create new id with each book
                id = " ",
                title = titleInput.toString(),
                authorNames = authorInput.toString(),
                publicationDate = publicationDateInput.toString(),
                starRating = ratingsInput.toString().toInt(),
                publisher = publisherInput.toString(),
                description = summaryInput.toString(),
                pageCount = lastPageReadInput.toString().toInt(),
                // will need to work out how to scrape book image from Google API
                thumbnail = " ",
                journalEntry = reviewInput.toString(),
                userProgress = 0,
                userFinished = finishedCB.isChecked,
                startDate = dataStartedInput.toString(),
                endDate = dateFinishedInput.toString(),
                prevReadCount = 0,
                purchasedFrom = purchasedFromInput.toString(),
                mainCharacters = mainCharactersInput.toString(),
                genres = genresInput.toString(),
                tags = tagsInput.toString(),
                lastReadDate = " ",
                lastReadTime = " ",
                isFav = false
            )

            // need to work with Ethan about how to add books to database
            val database = FirebaseDatabase.getInstance()
            val bookRepository = BookRepository(database)
            bookRepository.addBook(newBook)

            Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LibraryData::class.java)
            startActivity(intent)
        }

        cancelBottomBTN.setOnClickListener {
            // need to clear all fields and return to library screen
            val intent = Intent(this, LibraryData::class.java)
            startActivity(intent)
        }

        cancelTopBTN.setOnClickListener {
            val intent = Intent(this, LibraryData::class.java)
            startActivity(intent)
        }

    }
}