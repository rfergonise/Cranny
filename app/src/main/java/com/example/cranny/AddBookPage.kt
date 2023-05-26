package com.example.cranny

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase

import androidx.lifecycle.Observer
import com.example.cranny.databinding.ActivityAddBookPageBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import java.util.*
import com.example.cranny.SearchFragment

class AddBookPage : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    lateinit var titleInput: EditText
    lateinit var dataStartedInput: TextView
    lateinit var authorInput: EditText
    lateinit var summaryInput: EditText
    lateinit var purchasedFromInput: EditText
    lateinit var reviewInput: EditText
    lateinit var publisherInput: EditText
    lateinit var publicationDateInput: EditText
    lateinit var mainCharactersInput: EditText
    lateinit var genresInput: TextInputLayout
    lateinit var tagsInput: TextInputLayout
    lateinit var ratingsInput: RatingBar
    lateinit var menuBTN: ImageButton
    lateinit var cancelBottomBTN: Button
    lateinit var saveBottomBTN: Button
    lateinit var cancelTopBTN: ImageButton
    lateinit var saveTopBTN: ImageButton
    lateinit var finishedCB: CheckBox
    lateinit var dateFinishedTextView: TextView
    lateinit var dateFinishedInput: EditText
    lateinit var lastPageReadTextView: TextView
    lateinit var lastPageReadInput: EditText
    lateinit var searchButton: ImageButton


    // Used for view binding
    //lateinit var activityAddBookPageBinding: ActivityAddBookPageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book_page)


        titleInput = findViewById(R.id.etTitleInput)
        dataStartedInput = findViewById(R.id.tdStarted)
        authorInput = findViewById(R.id.etAuthorInput)
        summaryInput = findViewById(R.id.etSummaryInput)
        purchasedFromInput = findViewById(R.id.etPurchasedFrom)
        reviewInput = findViewById(R.id.etReview)
        publisherInput = findViewById(R.id.etPublisherInput)
        publicationDateInput = findViewById(R.id.etnPublicationDateInput)
        mainCharactersInput = findViewById(R.id.etMainCharactersInput)
        genresInput = findViewById(R.id.tiGenres)
        tagsInput = findViewById(R.id.tiTags)
        ratingsInput = findViewById(R.id.ratingBar)
        menuBTN = findViewById(R.id.ibCancelButton)
        cancelBottomBTN = findViewById(R.id.btnCancel)
        saveBottomBTN = findViewById(R.id.btnSave)
        saveTopBTN = findViewById(R.id.ibSaveButton)
        finishedCB = findViewById(R.id.cbFinished)
        dateFinishedTextView = findViewById(R.id.tvDateFinished)
        dateFinishedInput = findViewById(R.id.etDateFinished)
        lastPageReadTextView = findViewById(R.id.tvPageRead)
        lastPageReadInput = findViewById(R.id.tnPageNumber)
        searchButton = findViewById(R.id.imageButtonSearch)


        // binding activity to menu
//        activityAddBookPageBinding = ActivityAddBookPageBinding.inflate(layoutInflater)
//        setContentView(activityAddBookPageBinding.root)

        // Go back to Dashboard Activity
        val btAddBookPage: ImageView = menuBTN
        btAddBookPage.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
        }

        // Random number generator for book IDs
        val randNum: String = UUID.randomUUID()
            .toString() //creates a new universal unique identifier for the books ID. this way there is no chance of 2 books getting the same ID

        // Hide Data Finished or Last Page Read based on finished checkbox
        finishedCB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (finishedCB.isChecked) {
                dateFinishedTextView.visibility = View.VISIBLE
                dateFinishedInput.visibility = View.VISIBLE
                lastPageReadTextView.visibility = View.INVISIBLE
                lastPageReadInput.visibility = View.INVISIBLE
            } else {
                dateFinishedTextView.visibility =
                    View.GONE //changed from view.invisible to view.gone which makes any unused areas disappear altogether rather than go invisible, which is better performance
                dateFinishedInput.visibility = View.GONE
                lastPageReadTextView.visibility = View.VISIBLE
                lastPageReadInput.visibility = View.VISIBLE
            }
        }

        //bookSuggestionAdaptor is used to display the list of book suggestions
        val bookSuggestionAdapter = bookSuggestionAdapter(
            this,
            mutableListOf(),
            AdapterView.OnItemClickListener { parent, view, position, id ->
                //boodSuggestionAdapter Body
            })

        saveTopBTN.setOnClickListener {
            val title = titleInput.text.toString()
            val author = authorInput.text.toString()

            //trying to check if already in library before saving
            checkIfInLibrary(title, author) { bookExists ->
                // Use the bookExists value here
                if (bookExists) {
                    // Book exists in the library
                } else {
                    // Book doesn't exist in the library
                }
            }
        }

        saveBottomBTN.setOnClickListener {
            val title = titleInput.text.toString()
            val author = authorInput.text.toString()

            //trying to check if already in library before saving
            checkIfInLibrary(title, author) { bookExists ->
                // Use the bookExists value here
                if (bookExists) {
                    // Book exists in the library
                } else {
                    // Book doesn't exist in the library
                }
            }
        }

        menuBTN.setOnClickListener {
         val intent = Intent(this, LibraryActivity::class.java)
         startActivity(intent)
         }

        cancelBottomBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        //Start of the search Fragment
        searchButton.setOnClickListener {
            val searchFragment = SearchFragment()
            searchFragment.show(supportFragmentManager, "SearchFragment")

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
            val bookSuggestionAdapter = bookSuggestionAdapter(
                this,
                mutableListOf(),
                AdapterView.OnItemClickListener { parent, view, position, id ->
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
                    val selectedBook: Book? =
                        selectedBookSuggestion?.let { bookSuggestionToBook(it, user) }
                    withContext(Dispatchers.Main) {

                        val totalPageCount: Int = 400 // todo change to total page count from api

                        //this didn't seem to be working
//                        var lastPageRead: Int
//                        try {
//                            lastPageRead = setPageRead(lastPageReadInput.toString().toInt())
//                        } catch (e: NumberFormatException) {
//                            // Handle the exception here (e.g., show an error message, provide a default value)
//                            lastPageRead = 0 // Default value if conversion fails
//                        }

                        val inputText = lastPageReadInput.text.toString().trim()
                        val totalPagesReadInput = if (inputText.isNotBlank()) {
                            val parsedValue = inputText.toIntOrNull()
                            parsedValue?.let {
                                setPageRead(it)
                            } ?: 0
                        } else {
                            0
                        }

                        val newBook = Book(
                            id = UUID.randomUUID().toString(),
                            title = capitalizeEachWord(titleInput.text.toString()),
                            authorNames = capitalizeEachWord(authorInput.text.toString()),
                            publicationDate = setPublicationDate(publicationDateInput.text.toString()),
                            starRating = ratingsInput.rating.toString().toFloat(),
                            publisher = capitalizeEachWord(publisherInput.text.toString()),
                            description = summaryInput.text.toString(),
                            pageCount = totalPagesReadInput,
                            thumbnail = " ",
                            journalEntry = reviewInput.text.toString(),
                            userProgress = 0,
                            userFinished = finishedCB.isChecked,
                            startDate = setStartDate(dataStartedInput.text.toString()),
                            endDate = setFinishedDate(dateFinishedInput.text.toString()),
                            prevReadCount = 0,
                            purchasedFrom = capitalizeEachWord(purchasedFromInput.text.toString()),
                            mainCharacters = mainCharactersInput.text.toString(),
                            genres = genres,
                            tags = tags,
                            lastReadDate = currentMillis,
                            lastReadTime = currentMillis,
                            isFav = false,
                            totalPageCount = totalPageCount,
                            totalPagesRead = totalPagesReadInput
                        )
                        val bookRepository =
                            BookRepository(database, Friend(currentUser!!.uid, username, false))
                        bookRepository.addBook(newBook, this@AddBookPage)
                        val friendRepo = FriendRepository(
                            database,
                            username,
                            currentUser!!.uid,
                            this@AddBookPage
                        )
                        friendRepo.fetchFriends()
                        friendRepo.isFriendsReady.observe(
                            this@AddBookPage,
                            Observer { isFriendsReady ->
                                if (isFriendsReady) {
                                    val friendCount = friendRepo.FriendIds.size
                                    if (friendCount > 0) {
                                        val recentRepository = RecentRepository(
                                            database,
                                            username,
                                            friendRepo.FriendIds
                                        )
                                        val status = if (newBook.userFinished) {
                                            "@$username Finished Reading!"
                                        } else if (totalPagesReadInput > 1) {
                                            //changed lastPageRead to totalPagesRead to try to get accurate page count
                                            "@$username Read $totalPagesReadInput pages."
                                        } else {
                                            "@$username Read $totalPagesReadInput page."
                                        }
                                        recentRepository.addRecent(
                                            SocialFeed(
                                                newBook.id,
                                                newBook.title,
                                                newBook.authorNames!!,
                                                newBook.userFinished,
                                                status,
                                                newBook.thumbnail!!,
                                                newBook.lastReadDate!!,
                                                newBook.lastReadTime!!,
                                                username,
                                                newBook.mainCharacters!!,
                                                newBook.journalEntry!!,
                                                newBook.purchasedFrom!!,
                                                newBook.genres!!,
                                                newBook.tags!!,
                                                newBook.starRating!!,
                                                newBook.totalPageCount!!,
                                                newBook.totalPagesRead!!
                                            )
                                        )
                                        Toast.makeText(
                                            applicationContext,
                                            "Book saved",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent =
                                            Intent(this@AddBookPage, LibraryActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            })
                        friendRepo.stopFriendListener()
                    }
                    profileRepo.stopProfileListener()

                }
            })

        } else {
            Toast.makeText(applicationContext, "Fill out required text fields", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //Goes through string and capitalizes the first letter of each word
    fun capitalizeEachWord(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        } }
        return capitalizedWords.joinToString(" ")
    }

    // If user doesn't input a date, it will autofill with the current date
    private fun setStartDate(startDate: String): String {
        if (startDate.isNullOrEmpty()) {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            return formatter.format(time)
        } else {
            val formatter = SimpleDateFormat("MMddyyyy")
            val dateInput = formatter.parse(startDate)
            val formattedDate = SimpleDateFormat("MM/dd/yyyy").format(dateInput)
            return formattedDate
        }
    }

    // If user doesn't input a date, it will autofill with the current date
    private fun setFinishedDate(finishedDate: String): String {
        val finishedCB = findViewById<CheckBox>(R.id.cbFinished)
        val userFinished = finishedCB.isChecked

        if (userFinished) {
            if (finishedDate.isNullOrEmpty()) {
                val time = Calendar.getInstance().time
                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                val formattedDate = formatter.format(time)
                return formattedDate
            } else {
                val formatter = SimpleDateFormat("MMddyyyy")
                val dateInput = formatter.parse(dateFinishedInput.text.toString())
                val formattedDate = SimpleDateFormat("MM/dd/yyyy").format(dateInput)
                return formattedDate
            }
        }

        // Return an empty string if userFinished is false
        return ""
    }

    // Format publication date or return blank
    private fun setPublicationDate(publicationDate: String): String {
        if (publicationDate.isNotEmpty()) {
            val formatter = SimpleDateFormat("MMddyyyy")
            val dateInput = formatter.parse(publicationDateInput.text.toString())
            val formattedDate = SimpleDateFormat("MM/dd/yyyy").format(dateInput)
            return formattedDate
        } else {
            return ""
        }
    }

    // If user doesn't input last page read, it will default to 0
    private fun setPageRead(pageRead: Int): Int {
        if (pageRead > 0) {
            return pageRead
        } else {
            //pageRead.toInt()
            //return pageRead
            return 0
        }
    }

    //function to create a book
    private fun createBook(
        title: String, author: String, publicationDate: String, starRating: Float,
        publisher: String, description: String, pageCount: Int, thumbnail: String,
        journalEntry: String, finished: Boolean, startDate: String, endDate: String,
        purchasedFrom: String, mainCharacters: String, genres: String, tags: String
    ): Book {
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
            isFav = false,
            totalPageCount = 0,
            totalPagesRead = 0
        )
    }

    //function that allows us to take the book suggestion we click on and turn it into a book so we can populate the required fields.
    private suspend fun bookSuggestionToBook(suggestion: BookSuggestion, user: Friend): Book {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(firebaseDatabase, user)
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
                isFav = false,
                totalPageCount = 0,
                totalPagesRead = 0
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
            isFav = false,
            totalPageCount = 0,
            totalPagesRead = 0
        )
    }

    //    Check if Title and Author match another book before saving
    private fun checkIfInLibrary(titleCheck: String, authorCheck: String, callback: (Boolean) -> Unit) {
        var bookExists = false  // Flag variable to track if the book exists

        val libraryBookList = ArrayList<Book>()
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, currentUser!!.uid)

        profileRepo.profileData.observe(this, Observer { userProfile ->
            val username = userProfile.username
            val bookRepository =
                BookRepository(database, Friend(currentUser!!.uid, username, false))

            bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
                if (isBookDataReady) {
                    val bookCount = bookRepository.Library.size

                    if (bookCount > 0) {
                        for (books in bookRepository.Library) {
                            libraryBookList.add(
                                Book(
                                    books.id,
                                    books.title,
                                    books.authorNames,
                                    books.publicationDate,
                                    books.starRating,
                                    books.publisher,
                                    books.description,
                                    books.pageCount,
                                    books.thumbnail,
                                    books.journalEntry,
                                    books.userProgress,
                                    books.userFinished,
                                    books.isFav,
                                    books.purchasedFrom,
                                    books.mainCharacters,
                                    books.genres,
                                    books.tags,
                                    books.lastReadDate,
                                    books.lastReadTime,
                                    books.prevReadCount,
                                    books.startDate,
                                    books.endDate,
                                    books.totalPageCount,
                                    books.totalPagesRead,
                                )
                            )

                            if (titleCheck.lowercase() == books.title.lowercase() && authorCheck.lowercase() == books.authorNames.lowercase()) {
                                bookExists = true
                                break
                            }
                        }
                    }
                }

                bookRepository.stopBookListener()

                if (bookExists) {
                    // Book already exists in the library
                    Toast.makeText(
                        applicationContext,
                        "Book already in library",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Book doesn't exist in the library
                    saveNewBook()
                }

                // Invoke the callback with the result
                callback.invoke(bookExists)
            })
            profileRepo.stopProfileListener()
        })
    }
}