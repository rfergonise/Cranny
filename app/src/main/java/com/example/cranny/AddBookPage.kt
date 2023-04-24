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

    }
}