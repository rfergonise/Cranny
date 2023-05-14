package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView


class BookPageActivity : AppCompatActivity() {

    val abTitle : TextView = findViewById(R.id.tvBookTitle)
    val title : TextView = findViewById(R.id.tvbpBookTitle)
    val author : TextView = findViewById(R.id.tvbpAuthors)
    val genres : TextView = findViewById(R.id.tvbpGenres)
    val tags : TextView = findViewById(R.id.tvbpTags)
    val rating : RatingBar = findViewById(R.id.ratingBar2)
    val startDate : TextView = findViewById(R.id.tvbpDateStarted)
    val finishedDate : TextView = findViewById(R.id.tvbpDateFinished)
    var publisher : TextView = findViewById(R.id.tvbpPublisher)
    var publicationDate : TextView = findViewById(R.id.tvbpPublicationDate)
    var summary : TextView = findViewById(R.id.tvbpSummary)
    var mainCharacters : TextView = findViewById(R.id.tvbpMainCharacters)
    var userReview : TextView = findViewById(R.id.tvbpReview)
    var purchasedFrom : TextView= findViewById(R.id.tvbpPurchasedFrom)
    var lastPage : TextView = findViewById(R.id.tvbpUserPageRead)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_page)

        //val abTitle : TextView = findViewById(R.id.tvBookTitle)
        //val title : TextView = findViewById(R.id.tvbpBookTitle)
        //val author : TextView = findViewById(R.id.tvbpAuthors)
        //val genres : TextView = findViewById(R.id.tvbpGenres)
        //val tags : TextView = findViewById(R.id.tvbpTags)
        //val rating : RatingBar = findViewById(R.id.ratingBar2)
        //val startDate : TextView = findViewById(R.id.tvbpDateStarted)
        //val finishedDate : TextView = findViewById(R.id.tvbpDateFinished)
        //val publisher : TextView = findViewById(R.id.tvbpPublisher)
        //val publicationDate : TextView = findViewById(R.id.tvbpPublicationDate)
        //val summary : TextView = findViewById(R.id.tvbpSummary)
        //val mainCharacters : TextView = findViewById(R.id.tvbpMainCharacters)
        //val userReview : TextView = findViewById(R.id.tvbpReview)
        //val purchasedFrom : TextView= findViewById(R.id.tvbpPurchasedFrom)

        nullCheck()

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
        val exLastPageRead = bundle!!.getString("lastPageRead")


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
        lastPage.text = exLastPageRead


        val backBTN = findViewById<ImageButton>(R.id.bpBackButton)
        backBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

    }

    private fun nullCheck() {
        if (publisher.text.toString() == " ") {
            publisher.text = "n/a"
        }
        if (publicationDate.text.toString() == " ") {
            publicationDate.text = "n/a"
        }
        if (summary.text.toString() == " ") {
            summary.text = "n/a"
        }
        if (mainCharacters.text.toString() == " ") {
            mainCharacters.text = "n/a"
        }
        if (userReview.text.toString() == " ") {
            userReview.text = "n/a"
        }
        if (purchasedFrom.text.toString() == " ") {
            purchasedFrom.text = "n/a"
        }
    }

    fun bookFavorite(book: Book, btnbpNotFavorite: ImageView, btnbpIsFavorite: ImageView) {
        if (book.isFav == true) {
            btnbpIsFavorite.visibility = View.VISIBLE
            btnbpNotFavorite.visibility = View.INVISIBLE

            btnbpIsFavorite.setOnClickListener {
            }
        }
    }
}