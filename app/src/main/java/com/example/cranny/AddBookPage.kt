package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.cranny.databinding.ActivityAddBookPageBinding
import com.example.cranny.databinding.ActivityLibraryBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class AddBookPage : AppCompatActivity() {

    // Used for view binding
    lateinit var activityAddBookPageBinding: ActivityAddBookPageBinding


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
        val saveTopBTN = findViewById<ImageButton>(R.id.ibSaveButton)
        val finishedCB = findViewById<CheckBox>(R.id.cbFinished)
        val dateFinishedTextView = findViewById<TextView>(R.id.tvDateFinished)
        val dateFinishedInput = findViewById<EditText>(R.id.etDateFinished)
        val lastPageReadTextView = findViewById<TextView>(R.id.tvPageRead)
        val lastPageReadInput = findViewById<EditText>(R.id.tnPageNumber)

        // binding activity to menu
        activityAddBookPageBinding = ActivityAddBookPageBinding.inflate(layoutInflater)
        setContentView(activityAddBookPageBinding.root)

        // Go back to Dashboard Activity
        val btAddBookPage : Button = findViewById(R.id.btAddBookPage)
        btAddBookPage.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
        }

        // Random number generator for book IDs
        val randNum: Int = Random.nextInt(1000)

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



                val newBook = Book(
                    // need to create new id with each book
                    id = randNum.toString(),
                    title = titleInput.text.toString(),
                    authorNames = authorInput.text.toString(),
                    publicationDate = publicationDateInput.text.toString(),
                    starRating = ratingsInput.rating.toString().toFloat().toInt(),
                    publisher = publisherInput.text.toString(),
                    description = summaryInput.text.toString(),
                    pageCount = lastPageRead,
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

                // need to work with Ethan about how to add books to database
                val database = FirebaseDatabase.getInstance()
                val bookRepository = BookRepository(database)
                bookRepository.addBook(newBook)

                Toast.makeText(applicationContext, "Book saved", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LibraryActivity::class.java)
                startActivity(intent)
            }
            else {
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
                starRating = ratingsInput.rating.toString().toFloat().toInt(),
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
    }
}