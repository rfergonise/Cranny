package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.persistableBundleOf

class BookPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_page)

        val abTitle : TextView = findViewById(R.id.tvBookTitle)
        val title : TextView = findViewById(R.id.tvbpBookTitle)
        val author : TextView = findViewById(R.id.tvbpAuthors)
        val genres : TextView = findViewById(R.id.tvbpGenres)
        val tags : TextView = findViewById(R.id.tvbpTags)
        val rating : RatingBar = findViewById(R.id.ratingBar2)
        val startDate : TextView = findViewById(R.id.tvbpDateStarted)
        val finishedDate : TextView = findViewById(R.id.tvbpDateFinished)
        val publisher : TextView = findViewById(R.id.tvbpPublisher)
        val publicationDate : TextView = findViewById(R.id.tvbpPublicationDate)
        val summary : TextView = findViewById(R.id.tvbpSummary)
        val mainCharacters : TextView = findViewById(R.id.tvbpMainCharacters)
        val userReview : TextView = findViewById(R.id.tvbpReview)
        val purchasedFrom : TextView= findViewById(R.id.tvbpPurchasedFrom)


        val bundle : Bundle? = intent.extras
        val exTitle = bundle!!.getString("title")
        val exAuthor = bundle!!.getString("author")
        val exGenres = bundle!!.getString("genres")
        val exTags = bundle!!.getString("tags")
        val exRating = bundle!!.getInt("rating")
        val exStartDate = bundle!!.getString("startDate")
        val exFinishedDate = bundle!!.getString("finishedDate")
        val exPublisher = bundle!!.getString("publisher")
        val exPublicationDate = bundle!!.getString("publicationDate")
        val exMainCharacters = bundle!!.getString("mainCharacters")
        val exSummary = bundle!!.getString("summary")
        val exReview = bundle!!.getString("userReview")
        val exPurchasedFrom = bundle!!.getString("purchasedFrom")


        abTitle.text = exTitle
        title.text = exTitle
        author.text = exAuthor
        genres.text = exGenres
        tags.text = exTags
        rating.rating = exRating.toFloat()
        startDate.text = exStartDate
        finishedDate.text = exFinishedDate
        publisher.text = exPublisher
        publicationDate.text = exPublicationDate
        summary.text = exSummary
        mainCharacters.text = exMainCharacters
        userReview.text = exReview
        purchasedFrom.text = exPurchasedFrom


        val backBTN = findViewById<ImageButton>(R.id.bpBackButton)
        backBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        val favoriteBTN = findViewById<ImageButton>(R.id.bpFavoriteButton)
        favoriteBTN.setOnClickListener() {


        }

    }
}