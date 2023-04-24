package com.example.cranny

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView

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
        val genresInput = findViewById<EditText>(R.id.tiGenres)
        val tagsInput = findViewById<EditText>(R.id.tiTags)
        val ratingsInput = findViewById<RatingBar>(R.id.ratingBar)
        val cancelBTN = findViewById<Button>(R.id.btnCancel)
        val saveBTN = findViewById<Button>(R.id.btnSave)
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

        saveBTN.setOnClickListener {
            val newBook = Book(
                // need to create new id with each book
                id = " ",
                title = titleInput.toString(),
                authorNames = authorInput.toString(),
                //need to add publication date
                publicationDate = " ",
                starRating = ratingsInput.toString().toInt(),
                // need to add publisher
                publisher = " ",
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
                // need to add main characters
                mainCharacters = " ",
                genres = genresInput.toString(),
                tags = tagsInput.toString(),
                lastReadDate = " ",
                lastReadTime = " ",
                isFav = false
            )

            // need to work with Ethan about how to add books to database
            // addBookToDatabase(newBook)
        }

        cancelBTN.setOnClickListener {
            // need to clear all fields and return to library screen
        }

    }
}