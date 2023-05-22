package com.example.cranny

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.Observer


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
    private lateinit var ibFavorite: ImageButton
    private var isBookFavorite: Boolean = false


    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private lateinit var username: String


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
        ibFavorite = findViewById(R.id.ibFavorite)

        //nullCheck()

        val bundle: Bundle? = intent.extras
        val exTitle = bundle!!.getString("title")
        val exAuthor = bundle.getString("author")
        val exGenres = bundle.getString("genres")
        val exTags = bundle.getString("tags")
        val exRating = bundle.getFloat("rating")
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
        username = bundle.getString("username")!!

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

        isBookFavorite = exIsFav.toBoolean()
        if(isBookFavorite) ibFavorite.setImageResource(R.drawable.favorite_yes)
        else ibFavorite.setImageResource(R.drawable.favorite_no)

        val currentBook = Book(
            exBookID.toString(),
            title.toString(),
            author.toString(),
            publicationDate.toString(),
            rating.rating,
            publisher.toString(),
            summary.toString(),
            0,
            " ",
            userReview.toString(),
            0,
            false,
            isBookFavorite,
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
            0,
            isbn = "isbn"
        )

        ibFavorite.setOnClickListener {
            bookFavorite(currentBook, isBookFavorite)
        }

        val backBTN = findViewById<ImageButton>(R.id.bpBackButton)
        backBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        val deleteBTN = findViewById<Button>(R.id.btnbpDelete)
        deleteBTN.setOnClickListener {
            val alertBox = AlertDialog.Builder(this@BookPageActivity)
                .setTitle("Remove Book")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Yes") { dialog, id ->
                    val database = FirebaseDatabase.getInstance()
                    val profileRepo = ProfileRepository(database, currentUser!!.uid)
                    profileRepo.profileData.observe(this, Observer { userProfile ->
                        username = userProfile.username
                        val bookRepository =
                            BookRepository(database, Friend(currentUser!!.uid, username, false))
                        bookRepository.removeBook(currentBook, this@BookPageActivity)

                        bookRepository.stopBookListener()
                    })
                    profileRepo.stopProfileListener()
                    val intent = Intent(this, LibraryActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "Book deleted", Toast.LENGTH_SHORT).show()
                }

                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = alertBox.create()
            alert.show()
        }
    }

        fun nullCheck() {
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

        fun bookFavorite(currentBook: Book, isBookFav: Boolean)
        {
            isBookFavorite = !isBookFav
            if(!isBookFav) ibFavorite.setImageResource(R.drawable.favorite_yes) // we are making it a favorite so show the fav button
            else ibFavorite.setImageResource(R.drawable.favorite_no) // we are not favorite it so show the not fav button

            val database = FirebaseDatabase.getInstance()
            // was crashing before because username wasn't getting set
            // so I passed it in with the book data
            val bookRepository = BookRepository(database, Friend(currentUser!!.uid, username, false))
            currentBook.isFav = !isBookFav
            bookRepository.updateFavoriteStatus(currentBook)
        }
    }
