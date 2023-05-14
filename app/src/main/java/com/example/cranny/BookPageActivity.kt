package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase


class BookPageActivity : AppCompatActivity() {

    private lateinit var abTitle: TextView
    private lateinit var title: TextView
    private lateinit var author: TextView
    private lateinit var genres: TextView
    private lateinit var tags: TextView
    private lateinit var rating: RatingBar
    private lateinit var startDate: TextView
    private lateinit var finishedDate: TextView
    private lateinit var publisher: TextView
    private lateinit var publicationDate: TextView
    private lateinit var summary: TextView
    private lateinit var mainCharacters: TextView
    private lateinit var userReview: TextView
    private lateinit var purchasedFrom: TextView
    private lateinit var lastPage: TextView
    private lateinit var isFavorite: ImageButton
    private lateinit var notFavorite: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_page)

        abTitle = findViewById(R.id.tvBookTitle)
        title = findViewById(R.id.tvbpBookTitle)
        author = findViewById(R.id.tvbpAuthors)
        genres = findViewById(R.id.tvbpGenres)
        tags = findViewById(R.id.tvbpTags)
        rating = findViewById(R.id.ratingBar2)
        startDate = findViewById(R.id.tvbpDateStarted)
        finishedDate = findViewById(R.id.tvbpDateFinished)
        publisher = findViewById(R.id.tvbpPublisher)
        publicationDate = findViewById(R.id.tvbpPublicationDate)
        summary = findViewById(R.id.tvbpSummary)
        mainCharacters = findViewById(R.id.tvbpMainCharacters)
        userReview = findViewById(R.id.tvbpReview)
        purchasedFrom = findViewById(R.id.tvbpPurchasedFrom)
        lastPage = findViewById(R.id.tvbpLastPageRead)
        isFavorite = findViewById(R.id.btnbpIsFavorite)
        notFavorite = findViewById(R.id.btnbpNotFavorite)


        nullCheck()

        val bundle: Bundle? = intent.extras
        val exTitle = bundle!!.getString("title")
        val exAuthor = bundle.getString("author")
        val exGenres = bundle.getString("genres")
        val exTags = bundle.getString("tags")
        val exRating = bundle.getInt("rating")
        val exStartDate = bundle.getString("startDate")
        val exFinishedDate = bundle.getString("finishedDate")
        val exPublisher = bundle.getString("publisher")
        val exPublicationDate = bundle.getString("publicationDate")
        val exMainCharacters = bundle.getString("mainCharacters")
        val exSummary = bundle.getString("summary")
        val exReview = bundle.getString("userReview")
        val exPurchasedFrom = bundle.getString("purchasedFrom")
        val exLastPageRead = bundle.getString("lastPageRead")
        val exBookID = bundle.getString("id")
        val exIsFav = bundle.getString("isFav")

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

        nullCheck()

        val backBTN = findViewById<ImageButton>(R.id.bpBackButton)
        backBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        val currentBook = Book(
            exBookID.toString(),
            title.toString(),
            author.toString(),
            publicationDate.toString(),
            rating.rating.toFloat(),
            publisher.toString(),
            summary.toString(),
            0,
            " ",
            userReview.toString(),
            0,
            false,
            exIsFav.toBoolean(),
            purchasedFrom.toString(),
            mainCharacters.toString(),
            genres.toString(),
            tags.toString(),
            0,
            0,
            0,
            startDate.toString(),
            "",
            0,
            0
        )

        bookFavorite(currentBook, notFavorite, isFavorite)

    }


    private fun nullCheck() {
        if (publisher.text.toString().isBlank()) {
            publisher.text = "Not Available"
        }
        if (publicationDate.text.toString().isBlank()) {
            publicationDate.text = "Not Available"
        }
        if (summary.text.toString().isBlank()) {
            summary.text = "Not Available"
        }
        if (mainCharacters.text.toString().isBlank()) {
            mainCharacters.text = "Not Available"
        }
        if (userReview.text.toString().isBlank()) {
            userReview.text = "Not Available"
        }
        if (purchasedFrom.text.toString().isBlank()) {
            purchasedFrom.text = "Not Available"
        }
    }

    //Creating a "Friend" when the favorite button is clicked and updating the isFav variable for it. Need to fix
    fun bookFavorite(book: Book, btnbpNotFavorite: ImageButton, btnbpIsFavorite: ImageButton) {
        if (book.isFav == false) {
            btnbpIsFavorite.visibility = View.INVISIBLE
            btnbpNotFavorite.visibility = View.VISIBLE

            btnbpNotFavorite.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val bookRepo = BookRepository(database, Friend(book.id, book.title, book.isFav!!))

                book.isFav = true
                bookRepo.updateFavoriteStatus(book)

                btnbpIsFavorite.visibility = View.VISIBLE
                btnbpNotFavorite.visibility = View.INVISIBLE

                btnbpIsFavorite.setOnClickListener {
                    bookFavorite(book, btnbpNotFavorite, btnbpIsFavorite)
                }
            }
        }
        else {
            btnbpIsFavorite.visibility = View.VISIBLE
            btnbpNotFavorite.visibility = View.INVISIBLE

            btnbpIsFavorite.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val bookRepo = BookRepository(database, Friend(book.id, book.title, book.isFav!!))

                book.isFav = false
                bookRepo.updateFavoriteStatus(book)

                btnbpIsFavorite.visibility = View.INVISIBLE
                btnbpNotFavorite.visibility = View.VISIBLE

                btnbpNotFavorite.setOnClickListener {
                    bookFavorite(book, btnbpNotFavorite, btnbpIsFavorite)
                }
            }
        }
    }
}