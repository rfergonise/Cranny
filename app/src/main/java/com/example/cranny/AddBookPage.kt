package com.example.cranny

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.random.Random
import com.example.cranny.bookSuggestionAdapter
import com.example.cranny.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBookPage : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    lateinit var titleInput : EditText
    lateinit var dataStartedInput : TextView
    lateinit var authorInput : EditText
    lateinit var summaryInput : EditText
    lateinit var purchasedFromInput : EditText
    lateinit var reviewInput : EditText
    lateinit var publisherInput : EditText
    lateinit var publicationDateInput : EditText
    lateinit var mainCharactersInput : EditText
    lateinit var genresInput : TextInputLayout
    lateinit var tagsInput : TextInputLayout
    lateinit var ratingsInput : RatingBar
    lateinit var cancelBottomBTN : Button
    lateinit var saveBottomBTN : Button
    lateinit var cancelTopBTN : ImageButton
    lateinit var saveTopBTN : ImageButton
    lateinit var finishedCB : CheckBox
    lateinit var dateFinishedTextView : TextView
    lateinit var dateFinishedInput : EditText
    lateinit var lastPageReadTextView : TextView
    lateinit var lastPageReadInput : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book_page)

        titleInput = findViewById<EditText>(R.id.etTitleInput)
        dataStartedInput = findViewById<TextView>(R.id.tdStarted)
        authorInput = findViewById<EditText>(R.id.etAuthorInput)
        summaryInput = findViewById<EditText>(R.id.etSummaryInput)
        purchasedFromInput = findViewById<EditText>(R.id.etPurchasedFrom)
        reviewInput = findViewById<EditText>(R.id.etReview)
        publisherInput = findViewById<EditText>(R.id.etPublisherInput)
        publicationDateInput = findViewById<EditText>(R.id.etnPublicationDateInput)
        mainCharactersInput = findViewById<EditText>(R.id.etMainCharactersInput)
        genresInput = findViewById<TextInputLayout>(R.id.tiGenres)
        tagsInput = findViewById<TextInputLayout>(R.id.tiTags)
        ratingsInput = findViewById<RatingBar>(R.id.ratingBar)
        cancelBottomBTN = findViewById<Button>(R.id.btnCancel)
        saveBottomBTN = findViewById<Button>(R.id.btnSave)
        cancelTopBTN = findViewById<ImageButton>(R.id.ibCancelButton)
        saveTopBTN = findViewById<ImageButton>(R.id.ibSaveButton)
        finishedCB = findViewById<CheckBox>(R.id.cbFinished)
        dateFinishedTextView = findViewById<TextView>(R.id.tvDateFinished)
        dateFinishedInput = findViewById<EditText>(R.id.etDateFinished)
        lastPageReadTextView = findViewById<TextView>(R.id.tvPageRead)
        lastPageReadInput = findViewById<EditText>(R.id.tnPageNumber)


        // Random number generator for book IDs
        val randNum: String = UUID.randomUUID().toString() //creates a new universal unique identifier for the books ID. this way there is no chance of 2 books getting the same ID

        // Hide Data Finished or Last Page Read based on finished checkbox
        finishedCB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (finishedCB.isChecked) {
                dateFinishedTextView.visibility = View.VISIBLE
                dateFinishedInput.visibility = View.VISIBLE
                lastPageReadTextView.visibility = View.INVISIBLE
                lastPageReadInput.visibility = View.INVISIBLE
            } else {
                dateFinishedTextView.visibility = View.GONE //changed from view.invisible to view.gone which makes any unused areas disappear altogether rather than go invisible, which is better performance
                dateFinishedInput.visibility = View.GONE
                lastPageReadTextView.visibility = View.GONE
                lastPageReadInput.visibility = View.GONE
            }
        }

        //bookSuggestionAdaptor is used to display the list of book suggestions
        val bookSuggestionAdapter = bookSuggestionAdapter(this, mutableListOf(), AdapterView.OnItemClickListener { parent, view, position, id ->
            //boodSuggestionAdapter Body
        })
        saveBottomBTN.setOnClickListener {

//            if (authorInput.text.isNotEmpty() && titleInput.text.isNotEmpty()) {
//                val editText = genresInput.editText
//                val genres = editText?.text.toString()
//
//                val editText2 = tagsInput.editText
//                val tags = editText2?.text.toString()
//
//                val lastPageRead = if (lastPageReadInput.text.isNotEmpty()) {
//                    lastPageReadInput.text.toString().toInt()
//                } else {
//                    0 // or any other default value you want to use
//                }
//
//                //created CoroutineScope to access suspended functions
//                val selectedBookSuggestion: BookSuggestion? = bookSuggestionAdapter?.selectedBook
//                    CoroutineScope(Dispatchers.IO).launch {
//                    val selectedBook: Book? = selectedBookSuggestion?.let { bookSuggestionToBook(it) }
//
//                    withContext(Dispatchers.Main) {
//                        // Use CreateBook Here so we can use bookSuggestionAdapter
//                        //use selectedBook in this coroutineScope
//                        //used Create book function
//                        val newBook = createBook(
//                            title = selectedBook?.title ?: titleInput.text.toString(),
//                            author = selectedBook?.authorNames ?: authorInput.text.toString(),
//                            publicationDate = selectedBook?.publicationDate ?: publicationDateInput.text.toString(),
//                            starRating = ratingsInput.rating,
//                            publisher = selectedBook?.publisher ?: publisherInput.text.toString(),
//                            description = selectedBook?.description ?: summaryInput.text.toString(),
//                            pageCount = lastPageRead,
//                            thumbnail = selectedBook?.thumbnail ?: "",
//                            journalEntry = reviewInput.text.toString(),
//                            finished = finishedCB.isChecked,
//                            startDate = dataStartedInput.text.toString(),
//                            endDate = dateFinishedInput.text.toString(),
//                            purchasedFrom = purchasedFromInput.text.toString(),
//                            mainCharacters = mainCharactersInput.text.toString(),
//                            genres = selectedBook?.genres ?: genres,
//                            tags = tags
//                        )
//
//                        // need to work with Ethan about how to add books to database
//                        val database = FirebaseDatabase.getInstance()
//                        val bookRepository = BookRepository(database)
//                        bookRepository.addBook(newBook)
//
//                        Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()
//
//                        val intent = Intent(this@AddBookPage, LibraryActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//            } else {
//                Toast.makeText(applicationContext, "Fill out required text fields", Toast.LENGTH_SHORT).show()
//            }
            saveNewBook()
        }

        saveTopBTN.setOnClickListener {
//            val editText = genresInput.editText
//            val genres = editText?.text.toString()
//
//            val editText2 = tagsInput.editText
//            val tags = editText2?.text.toString()
//
//            val newBook = Book(
//                // need to create new id with each book
//                id = randNum.toString(),
//                title = titleInput.text.toString(),
//                authorNames = authorInput.text.toString(),
//                publicationDate = publicationDateInput.text.toString(),
//                starRating = 0,
//                publisher = publisherInput.text.toString(),
//                description = summaryInput.text.toString(),
//                pageCount = lastPageReadInput.text.toString().toInt(),
//                // will need to work with Ethan about how to scrape book image from Google API
//                thumbnail = " ",
//                journalEntry = reviewInput.text.toString(),
//                userProgress = 0,
//                userFinished = finishedCB.isChecked,
//                startDate = dataStartedInput.text.toString(),
//                endDate = dateFinishedInput.text.toString(),
//                prevReadCount = 0,
//                purchasedFrom = purchasedFromInput.text.toString(),
//                mainCharacters = mainCharactersInput.text.toString(),
//                genres = genres,
//                tags = tags,
//                lastReadDate = 0,
//                lastReadTime = 0,
//                isFav = false
//            )
//
//
//            val database = FirebaseDatabase.getInstance()
//            val bookRepository = BookRepository(database)
//            bookRepository.addBook(newBook)
//
//            Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()
//
//            val intent = Intent(this, LibraryActivity::class.java)
//            startActivity(intent)

            saveNewBook()
        }

        saveTopBTN.setOnClickListener {
            saveNewBook()
        }


        cancelBottomBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        cancelTopBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

    }

    private fun saveNewBook() {

        if (authorInput.text.isNotEmpty() && titleInput.text.isNotEmpty()) {
            val editText = genresInput.editText
            val genres = editText?.text.toString()

            val editText2 = tagsInput.editText
            val tags = editText2?.text.toString()



            val lastPageRead = if (lastPageReadInput.text.isNotEmpty()) {
                lastPageReadInput.text.toString().toInt()
            } else {
                0 // or any other default value you want to use
            }

            val bookSuggestionAdapter = bookSuggestionAdapter(this, mutableListOf(), AdapterView.OnItemClickListener { parent, view, position, id ->
                //boodSuggestionAdapter Body
            })

            val currentMillis = System.currentTimeMillis() // get the current time in milliseconds
            Toast.makeText(applicationContext, "Saving book...", Toast.LENGTH_SHORT).show()
            val database = FirebaseDatabase.getInstance()
            val profileRepo = ProfileRepository(database, currentUser!!.uid)
            profileRepo.profileData.observe(this@AddBookPage, Observer { userProfile ->
                val username: String = userProfile.username
                val user: Friend = Friend(currentUser!!.uid, username, false)
                val selectedBookSuggestion: BookSuggestion? = bookSuggestionAdapter?.selectedBook
                CoroutineScope(Dispatchers.IO).launch {
                    val selectedBook: Book? = selectedBookSuggestion?.let { bookSuggestionToBook(it,user) }
                    withContext(Dispatchers.Main) {


                        val newBook = Book(
                            id = UUID.randomUUID().toString(),
                            title = titleInput.text.toString(),
                            authorNames = authorInput.text.toString(),
                            publicationDate = publicationDateInput.text.toString(),
                            starRating = ratingsInput.rating.toString().toFloat(),
                            publisher = publisherInput.text.toString(),
                            description = summaryInput.text.toString(),
                            pageCount = setPageRead(lastPageReadInput.text.toString()),
                            thumbnail = " ",
                            journalEntry = reviewInput.text.toString(),
                            userProgress = 0,
                            userFinished = finishedCB.isChecked,
                            startDate = setStartDate(dataStartedInput.text.toString()),
                            endDate = setFinishedDate(dateFinishedInput.text.toString()),
                            prevReadCount = 0,
                            purchasedFrom = purchasedFromInput.text.toString(),
                            mainCharacters = mainCharactersInput.text.toString(),
                            genres = genres,
                            tags = tags,
                            lastReadDate = currentMillis,
                            lastReadTime = currentMillis,
                            isFav = false
                        )
                        val bookRepository = BookRepository(database, Friend(currentUser!!.uid, username, false))
                        bookRepository.addBook(newBook, this@AddBookPage)
                        val friendRepo = FriendRepository(database, username, currentUser!!.uid, this@AddBookPage)
                        friendRepo.fetchFriends()
                        friendRepo.isFriendsReady.observe(this@AddBookPage, Observer { isFriendsReady ->
                            if(isFriendsReady)
                            {
                                val friendCount = friendRepo.FriendIds.size
                                if(friendCount > 0) {
                                    val recentRepository = RecentRepository(database, username, friendRepo.FriendIds)
                                    val status = if (newBook.userFinished) {
                                        "@$username Finished Reading!"
                                    }
                                    else if(lastPageRead > 1)
                                    {
                                        "@$username Read $lastPageRead pages."
                                    }
                                    else
                                    {
                                        "@$username Read $lastPageRead page."
                                    }
                                    recentRepository.addRecent(SocialFeed(newBook.id, newBook.title, newBook.authorNames!!, newBook.userFinished,
                                        status, newBook.thumbnail!!, newBook.lastReadDate!!, newBook.lastReadTime!!, username, newBook.mainCharacters!!,
                                        newBook.journalEntry!!, newBook.purchasedFrom!!, newBook.genres!!, newBook.tags!!, newBook.starRating!!))
                                    Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@AddBookPage, LibraryActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        })
                        friendRepo.stopFriendListener()
                    }
                    profileRepo.stopProfileListener()

                }
            })

        }
        else {
            Toast.makeText(applicationContext, "Fill out required text fields", Toast.LENGTH_SHORT).show()
        }
    }

    // If user doesn't input a date, it will autofill with the current date
    private fun setStartDate(startDate: String): String {
        if (startDate.isNullOrEmpty()) {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            return formatter.format(time)
        } else {
            val formatter = SimpleDateFormat("MM//dd/yyyy")
            val dateInput = formatter.parse(startDate)
            return formatter.format(dateInput)
        }
    }

    private fun setFinishedDate(finishedDate: String): String {
        val finishedCB = findViewById<CheckBox>(R.id.cbFinished)
        val userFinished = finishedCB.isChecked

        if (userFinished){
            if (finishedDate.isNullOrEmpty()) {
                val time = 0
                val formatter = SimpleDateFormat("MM/dd/yyyy")
                return formatter.format(time)
            } else {
                val formatter = SimpleDateFormat("MM/dd/yyyy")
                val dateInput = formatter.parse(finishedDate)
                return formatter.format(dateInput)
            }
        } else
            return ""
    }

    // If user doesn't input last page read, it will default to 0
    private fun setPageRead(pageRead: String): Int {
        return if (pageRead.isNullOrEmpty()) {
            0
        } else {
            pageRead.toInt()
        }
    }

    //function to create a book
    private fun createBook(title: String, author: String, publicationDate: String, starRating: Float,
                           publisher: String, description: String, pageCount: Int, thumbnail: String,
                           journalEntry: String, finished: Boolean, startDate: String, endDate: String,
                           purchasedFrom: String, mainCharacters: String, genres: String, tags: String): Book {
        // need to create new id with each book
        val id = UUID.randomUUID().toString()
        val currentMillis = System.currentTimeMillis() // get the current time in milliseconds
        return Book(
            id = id,
            title = title,
            authorNames = author,
            publicationDate = publicationDate,
            starRating = starRating,
            publisher = publisher,
            description = description,
            pageCount = pageCount,
            thumbnail = thumbnail,
            journalEntry = journalEntry,
            userProgress = 0,
            userFinished = finished,
            startDate = startDate,
            endDate = endDate,
            prevReadCount = 0,
            purchasedFrom = purchasedFrom,
            mainCharacters = mainCharacters,
            genres = genres,
            tags = tags,
            lastReadDate = currentMillis,
            lastReadTime = currentMillis,
            isFav = false
        )
    }

    //function that allows us to take the book suggestion we click on and turn it into a book so we can populate the required fields.
    private suspend fun bookSuggestionToBook(suggestion: BookSuggestion, user: Friend): Book {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(firebaseDatabase, user )
        // Get the book details from the Google Books API
        val bookDetails = withContext(Dispatchers.IO) {
            bookRepository.getBookDetails(suggestion.Id)
        }   // If bookDetails is null, return a default Book
        if (bookDetails == null) {
            return Book(
                id = "",
                title = "Unknown",
                authorNames = "Unknown",
                publicationDate = "Unknown",
                starRating = 0f,
                publisher = "Unknown",
                description = "Unknown",
                pageCount = 0,
                thumbnail = "",
                journalEntry = "",
                userProgress = 0,
                userFinished = false,
                startDate = "",
                endDate = "",
                prevReadCount = 0,
                purchasedFrom = "",
                mainCharacters = "",
                genres = "Unknown",
                tags = "",
                lastReadDate = 0,
                lastReadTime = 0,
                isFav = false
            )
        }


        // otherwise Return a new Book with the details filled in
        return Book(
            id = UUID.randomUUID().toString(),
            title = bookDetails.title,
            authorNames = bookDetails.authorNames,
            publicationDate = bookDetails.publicationDate,
            starRating = 0f,
            publisher = bookDetails.publisher,
            description = bookDetails.description,
            pageCount = bookDetails.pageCount,
            thumbnail = bookDetails.thumbnail,
            journalEntry = "",
            userProgress = 0,
            userFinished = false,
            startDate = "",
            endDate = "",
            prevReadCount = 0,
            purchasedFrom = "",
            mainCharacters = "",
            genres = " ",
            tags = "",
            lastReadDate = 0,
            lastReadTime = 0,
            isFav = false
        )
    }
}