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
    private lateinit var userPageRead: TextView
    private lateinit var ibFavorite: ImageButton
    private lateinit var tvFinishedReading: TextView
    private var isBookFavorite: Boolean = false
    private var userFinished: Boolean = true
    private lateinit var exThumbnail: ImageView  //added by will to show thumbnail in library book info


    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private lateinit var username: String


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_page)

        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, currentUser!!.uid)


        profileRepo.profileData.observe(this, Observer { userProfile ->
            username = userProfile.username
            val bookRepository = BookRepository(database, Friend(currentUser!!.uid, username, false))
            bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
                if (isBookDataReady) {
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
                    userPageRead = findViewById(R.id.tvbpUserPageRead)
                    ibFavorite = findViewById(R.id.bpibFavorite)
                    exThumbnail = findViewById(R.id.imageView)  //added by will to show thumbnail in library book info

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
                    //last page not importing properly

                    val exUserPageRead = bundle.getInt("lastPageRead")
                    val exTotalPageCount = bundle.getInt("totalPageCount")

                    val exBookID = bundle.getString("id")
                    val exIsFav = bundle.getBoolean("isFav")
                    val exUserFinished = bundle.getBoolean("userFinished")
                    val exTime = bundle.getLong("time")
                    //username = bundle.getString("username")!!
                    val exThumbnailUrl = intent.getStringExtra("thumbnail") //added by will to show thumbnail in library book info

                    //added by will to show thumbnail in library book info
                    println("Thumbnail URL: $exThumbnailUrl")
                    Glide.with(this)
                        .load(exThumbnailUrl)
                        .error(R.drawable.logo_mid_green)
                        .into(exThumbnail)


                    abTitle.text = exTitle
                    title.text = exTitle
                    author.text = exAuthor
                    genres.text = exGenres
                    tags.text = exTags
                    rating.rating = exRating
                    startDate.text = exStartDate
                    finishedDate.text = exFinishedDate
                    publisher.text = exPublisher
                    publicationDate.text = exPublicationDate
                    summary.text = exSummary
                    mainCharacters.text = exMainCharacters
                    userReview.text = exReview
                    purchasedFrom.text = exPurchasedFrom
                    userPageRead.text = exUserPageRead.toString()
                    userFinished = exUserFinished

                    nullCheck()


                    //last page read returning 0
                    val currentBook = Book(
                        exBookID.toString(), //@PrimaryKey val id: String,
                        exTitle!!, //var title: String,
                        exAuthor!!, //var authorNames: String?,
                        exPublicationDate, //var publicationDate: String?,
                        exRating, //var starRating: Float?,
                        exPublisher, //var publisher: String?,
                        exSummary, //var description: String?,
                        exTotalPageCount, // "PageCount"
                        exThumbnailUrl, //var thumbnail: String?,
                        exReview,  //var journalEntry: String?,
                        exUserPageRead, // "UserProgress"
                        exUserFinished, //var userFinished: Boolean,
                        isBookFavorite, //var isFav: Boolean?,
                        exPurchasedFrom, //var purchasedFrom: String?,
                        exMainCharacters, //var mainCharacters: String?,
                        exGenres, //var genres: String?,
                        exTags, //var tags: String?,
                        exTime, //var lastReadDate: Long?,
                        exTime, //var lastReadTime: Long?,
                        0, //var prevReadCount: Int?,
                        exStartDate!!, //var startDate: String,
                        "", //var endDate: String,
                        exTotalPageCount, // "TotalPageCount"
                        exUserPageRead, // "TotalPageRead"
                        exThumbnailUrl, //var thumbnail: String?,  //added by will to show thumbnail in library book info

                    )


                    isBookFavorite = exIsFav
                    if (isBookFavorite) {
                        ibFavorite.setImageResource(R.drawable.favorite_yes)
                        ibFavorite.setColorFilter(resources.getColor(R.color.cranny_gold))
                    } else {
                        ibFavorite.setImageResource(R.drawable.favorite_yes)
                        ibFavorite.setColorFilter(resources.getColor(R.color.cranny_mid_green))
                    }

                    ibFavorite.setOnClickListener {
                        bookFavorite(currentBook, isBookFavorite)
                    }

                    //finished reading returning null
                    tvFinishedReading = findViewById(R.id.tvbpFinishedReading)

                    if (exUserFinished) {
                        tvFinishedReading.visibility = View.VISIBLE
                        finishedDate.visibility = View.VISIBLE
                        lastPage.visibility = View.INVISIBLE
                        userPageRead.visibility = View.INVISIBLE
                    } else {
                        tvFinishedReading.visibility = View.INVISIBLE
                        finishedDate.visibility = View.INVISIBLE
                        lastPage.visibility = View.VISIBLE
                        userPageRead.visibility = View.VISIBLE
                    }


                    val backBTN = findViewById<ImageButton>(R.id.bpBackButton)
                    backBTN.setOnClickListener {
                        val intent = Intent(this, LibraryActivity::class.java)
                        intent.putExtra("thumbnail", currentBook.thumbnail)
                        startActivity(intent)
                    }

                    //edit activity button
                    val editBTN = findViewById<Button>(R.id.btnbpEditBook)
                    editBTN.setOnClickListener {
                        val intent = Intent(this, EditBook::class.java)
                        intent.putExtra("book", currentBook) // pass the original book
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
                                        BookRepository(
                                            database,
                                            Friend(currentUser!!.uid, username, false)
                                        )
                                    bookRepository.removeBook(currentBook, this@BookPageActivity)

                                    bookRepository.stopBookListener()
                                })
                                profileRepo.stopProfileListener()
                                val intent = Intent(this, LibraryActivity::class.java)
                                intent.putExtra("thumbnail", currentBook.thumbnail)
                                startActivity(intent)
                                Toast.makeText(
                                    applicationContext,
                                    "Book deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            .setNegativeButton("No") { dialog, id ->
                                // Dismiss the dialog
                                dialog.dismiss()
                            }
                        val alert = alertBox.create()
                        alert.show()
                    }
                }
                bookRepository.stopBookListener()
            })
        })
        profileRepo.stopProfileListener()
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

        private fun bookFavorite(currentBook: Book, isBookFav: Boolean)
        {
            isBookFavorite = !isBookFav
            if(!isBookFav) {
                ibFavorite.setImageResource(R.drawable.favorite_yes) // we are making it a favorite so show the fav button
                ibFavorite.setColorFilter(resources.getColor(R.color.cranny_gold))
            }
            else {
                ibFavorite.setImageResource(R.drawable.favorite_yes) // we are not favorite it so show the not fav button
                ibFavorite.setColorFilter(resources.getColor(R.color.cranny_mid_green))
            }

            val database = FirebaseDatabase.getInstance()
            // was crashing before because username wasn't getting set
            // so I passed it in with the book data
            val bookRepository = BookRepository(database, Friend(currentUser!!.uid, username, false))
            currentBook.isFav = !isBookFav
            bookRepository.updateFavoriteStatus(currentBook)
        }
    }
