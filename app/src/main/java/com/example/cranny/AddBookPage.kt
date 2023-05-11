package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
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
        val genresInput = findViewById<TextInputLayout>(R.id.tiGenres)
        val tagsInput = findViewById<TextInputLayout>(R.id.tiTags)
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

        //
        val bookSuggestionAdapter = bookSuggestionAdapter(this, mutableListOf(), AdapterView.OnItemClickListener { parent, view, position, id ->
            // Handle item click here
        })
        saveBottomBTN.setOnClickListener {
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


                val selectedBookSuggestion: BookSuggestion? = bookSuggestionAdapter?.selectedBook
                CoroutineScope(Dispatchers.IO).launch {
                    val selectedBook: Book? = selectedBookSuggestion?.let { bookSuggestionToBook(it) }

                    withContext(Dispatchers.Main) {
                        // Use CreateBook Here so we can use bookSuggestionAdapter
                        //use selectedBook in this coroutineScope

                        val newBook = createBook(
                            title = selectedBook?.title ?: titleInput.text.toString(),
                            author = selectedBook?.authorNames ?: authorInput.text.toString(),
                            publicationDate = selectedBook?.publicationDate ?: publicationDateInput.text.toString(),
                            starRating = ratingsInput.rating,
                            publisher = selectedBook?.publisher ?: publisherInput.text.toString(),
                            description = selectedBook?.description ?: summaryInput.text.toString(),
                            pageCount = lastPageRead,
                            thumbnail = selectedBook?.thumbnail ?: "",
                            journalEntry = reviewInput.text.toString(),
                            finished = finishedCB.isChecked,
                            startDate = dataStartedInput.text.toString(),
                            endDate = dateFinishedInput.text.toString(),
                            purchasedFrom = purchasedFromInput.text.toString(),
                            mainCharacters = mainCharactersInput.text.toString(),
                            genres = selectedBook?.genres ?: genres,
                            tags = tags
                        )

                        // need to work with Ethan about how to add books to database
                        val database = FirebaseDatabase.getInstance()
                        val bookRepository = BookRepository(database)
                        bookRepository.addBook(newBook)

                        Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@AddBookPage, LibraryActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Fill out required text fields", Toast.LENGTH_SHORT).show()
            }
        }

        saveTopBTN.setOnClickListener {
            val editText = genresInput.editText
            val genres = editText?.text.toString()

            val editText2 = tagsInput.editText
            val tags = editText2?.text.toString()

            val newBook = Book(
                // need to create new id with each book
                id = randNum.toString(),
                title = titleInput.text.toString(),
                authorNames = authorInput.text.toString(),
                publicationDate = publicationDateInput.text.toString(),
                starRating = 0,
                publisher = publisherInput.text.toString(),
                description = summaryInput.text.toString(),
                pageCount = lastPageReadInput.text.toString().toInt(),
                // will need to work with Ethan about how to scrape book image from Google API
                thumbnail = " ",
                journalEntry = reviewInput.text.toString(),
                userProgress = 0,
                userFinished = finishedCB.isChecked,
                startDate = dataStartedInput.text.toString(),
                endDate = dateFinishedInput.text.toString(),
                prevReadCount = 0,
                purchasedFrom = purchasedFromInput.text.toString(),
                mainCharacters = mainCharactersInput.text.toString(),
                genres = genres,
                tags = tags,
                lastReadDate = " ",
                lastReadTime = " ",
                isFav = false
            )


            val database = FirebaseDatabase.getInstance()
            val bookRepository = BookRepository(database)
            bookRepository.addBook(newBook)

            Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
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

    //function to reduce code duplication
    private fun createBook(title: String, author: String, publicationDate: String, starRating: Float,
                           publisher: String, description: String, pageCount: Int, thumbnail: String,
                           journalEntry: String, finished: Boolean, startDate: String, endDate: String,
                           purchasedFrom: String, mainCharacters: String, genres: String, tags: String): Book {
        // need to create new id with each book
        val id = UUID.randomUUID().toString()

        return Book(
            id = id,
            title = title,
            authorNames = author,
            publicationDate = publicationDate,
            starRating = starRating.toInt(),
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
            lastReadDate = " ",
            lastReadTime = " ",
            isFav = false
        )
    }
    private suspend fun bookSuggestionToBook(suggestion: BookSuggestion): Book {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(firebaseDatabase)
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
                starRating = 0,
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
                lastReadDate = "",
                lastReadTime = "",
                isFav = false
            )
        }


        // otherwise Return a new Book with the details filled in
        return Book(
            id = UUID.randomUUID().toString(),
            title = bookDetails.title,
            authorNames = bookDetails.authorNames,
            publicationDate = bookDetails.publicationDate,
            starRating = 0,
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
            lastReadDate = "",
            lastReadTime = "",
            isFav = false
        )
    }
}