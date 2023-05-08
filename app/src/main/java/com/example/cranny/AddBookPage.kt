package com.example.cranny

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddBookPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book_page)

        val titleInput = findViewById<EditText>(R.id.etTitleInput)
        val dataStartedInput = findViewById<TextView>(R.id.tdStarted)
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

                val newBook = Book(
                    id = UUID.randomUUID().toString(),
                    title = titleInput.text.toString(),
                    authorNames = authorInput.text.toString(),
                    publicationDate = publicationDateInput.text.toString(),
                    starRating = ratingsInput.rating.toString().toFloat().toInt(),
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
            else {
                Toast.makeText(applicationContext, "Fill out required text fields", Toast.LENGTH_SHORT).show()
            }
        }

        saveTopBTN.setOnClickListener {
            if (authorInput.text.isNotEmpty() && titleInput.text.isNotEmpty()) {
                val editText = genresInput.editText
                val genres = editText?.text.toString()

                val editText2 = tagsInput.editText
                val tags = editText2?.text.toString()

                val newBook = Book(
                    id = UUID.randomUUID().toString(),
                    title = titleInput.text.toString(),
                    authorNames = authorInput.text.toString(),
                    publicationDate = publicationDateInput.text.toString(),
                    starRating = ratingsInput.rating.toString().toFloat().toInt(),
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
            else {
                Toast.makeText(applicationContext, "Fill out required text fields", Toast.LENGTH_SHORT).show()
            }
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
}