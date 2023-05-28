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



class EditBook : AppCompatActivity() {

    // EditText fields for book information
    private lateinit var editTextTitle: EditText
    private lateinit var editTextAuthor: EditText
    private lateinit var editTextPublishers: EditText
    private lateinit var editTextPublicationDate: EditText
    private lateinit var editTextGenres: EditText
    private lateinit var editTextTags: EditText
    private lateinit var editTextPurchasedFrom: EditText
    private lateinit var editTextSummary: EditText
    private lateinit var editTextMainCharacters: EditText
    private lateinit var rbReview: RatingBar

    private lateinit var saveButton: Button

    //Firebase Access
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    // original book
    private lateinit var bookOriginal: Book

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        bookOriginal = (intent.getSerializableExtra("book") as? Book)!! // grab the passed book

        // Retrieve references to other EditText fields
        editTextTitle = findViewById(R.id.tvbpBookTitle)
        editTextAuthor = findViewById(R.id.tvbpAuthors)
        editTextGenres = findViewById(R.id.tvbpGenres)
        editTextPublishers = findViewById(R.id.tvbpPublisher)
        editTextPublicationDate = findViewById(R.id.tvbpPublicationDate)
        editTextTags = findViewById(R.id.tvbpTags)
        editTextPurchasedFrom = findViewById(R.id.tvbpPurchasedFrom)
        editTextSummary = findViewById(R.id.tvbpSummary)
        editTextMainCharacters = findViewById(R.id.tvbpMainCharacters)
        rbReview = findViewById(R.id.tvbpReview)

        // set the edit text fields to the current saved text

        if (bookOriginal.title!!.isNotEmpty()) editTextTitle.setText(bookOriginal.title)
        else editTextTitle.setText("Title Unknown")

        if (bookOriginal.authorNames!!.isNotEmpty()) editTextAuthor.setText(bookOriginal.authorNames)
        else editTextAuthor.setText("Author Unknown")

        if (bookOriginal.genres!!.isNotEmpty()) editTextGenres.setText(bookOriginal.genres)
        else editTextGenres.setText("Genre Unknown")

        if (bookOriginal.publisher!!.isNotEmpty()) editTextPublishers.setText(bookOriginal.publisher)
        else editTextPublishers.setText("Publisher Unknown")

        if (bookOriginal.publicationDate!!.isNotEmpty()) editTextPublicationDate.setText(bookOriginal.publicationDate)
        else editTextPublicationDate.setText("Publication Date Unknown")

        if (bookOriginal.tags!!.isNotEmpty()) editTextTags.setText(bookOriginal.tags)
        else editTextTags.setText("No Tags")

        if (bookOriginal.purchasedFrom!!.isNotEmpty()) editTextPurchasedFrom.setText(bookOriginal.purchasedFrom)
        else editTextPurchasedFrom.setText("Purchase Location Unknown")

        if (bookOriginal.description!!.isNotEmpty()) editTextSummary.setText(bookOriginal.description)
        else editTextSummary.setText("No Summary")

        if (bookOriginal.mainCharacters!!.isNotEmpty()) editTextMainCharacters.setText(bookOriginal.mainCharacters)
        else editTextMainCharacters.setText("No Main Characters")

        // set the original rating
        rbReview.rating = bookOriginal.starRating!!

        val database = FirebaseDatabase.getInstance()
        // get profile details
        val profileRepo = ProfileRepository(database, currentUser!!.uid)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            // once we have profile details, create library firebase access
            val bookRepository = BookRepository(database, Friend(userProfile.userId, userProfile.username, false))
            // when we have the book repo and profile details, allow the logic for buttons
            AllowButtonAccess(bookRepository)
        })
        profileRepo.stopProfileListener()



    }

    private fun AllowButtonAccess(bookRepository: BookRepository)
    {
        // Cancel Edit Book On Click Listener
        val declineBTN = findViewById<ImageButton>(R.id.bpDeclineButton)
        declineBTN.setOnClickListener {
            // they don't want to edit it so go back with the original book
            val intent = Intent(this, BookPageActivity::class.java)
            intent.putExtra("title", bookOriginal.title)
            intent.putExtra("author", bookOriginal.authorNames)
            intent.putExtra("publisher", bookOriginal.publisher)
            intent.putExtra("publicationDate", bookOriginal.publicationDate)
            intent.putExtra("rating", bookOriginal.starRating)
            intent.putExtra("tags", bookOriginal.tags)
            intent.putExtra("genres", bookOriginal.genres)
            intent.putExtra("startDate", bookOriginal.startDate)
            intent.putExtra("userFinished", bookOriginal.userFinished)
            intent.putExtra("finishedDate", bookOriginal.endDate)
            intent.putExtra("mainCharacters", bookOriginal.mainCharacters)
            intent.putExtra("purchasedFrom", bookOriginal.purchasedFrom)
            intent.putExtra("userReview", bookOriginal.journalEntry)
            intent.putExtra("summary", bookOriginal.description)
            intent.putExtra("lastPageRead", bookOriginal.pageCount)
            intent.putExtra("id", bookOriginal.id)
            intent.putExtra("isFav", bookOriginal.isFav)
            intent.putExtra("thumbnail", bookOriginal.thumbnail)
            startActivity(intent)
        }

        // Save Edit Book On Click Listener
        saveButton = findViewById(R.id.btnSave)
        saveButton.setOnClickListener {
            // They want to save so let's see what's new
            // we should check if the text is empty but for time sake we can shove that to the side
            var bookTitle: String = if(bookOriginal.title != editTextTitle.text.toString()) editTextTitle.text.toString()
            else bookOriginal.title

            var bookAuthors: String = if(bookOriginal.authorNames != editTextAuthor.text.toString()) editTextAuthor.text.toString()
            else bookOriginal.authorNames

            var bookGenres: String = if(bookOriginal.genres != editTextGenres.text.toString()) editTextGenres.text.toString()
            else bookOriginal.genres!!

            var bookPublisher: String = if(bookOriginal.publisher != editTextPublishers.text.toString()) editTextPublishers.text.toString()
            else bookOriginal.publisher!!

            var bookPublicationDate: String = if(bookOriginal.publicationDate != editTextPublicationDate.text.toString()) editTextPublicationDate.text.toString()
            else bookOriginal.publicationDate!!

            var bookTags: String = if(bookOriginal.tags != editTextTags.text.toString()) editTextTags.text.toString()
            else bookOriginal.tags!!

            var bookPurchasedFrom: String = if(bookOriginal.purchasedFrom != editTextPurchasedFrom.text.toString()) editTextPurchasedFrom.text.toString()
            else bookOriginal.purchasedFrom!!

            var bookSummary: String = if(bookOriginal.description != editTextSummary.text.toString()) editTextSummary.text.toString()
            else bookOriginal.description!!

            var bookMainCharacters: String = if(bookOriginal.mainCharacters != editTextMainCharacters.text.toString()) editTextMainCharacters.text.toString()
            else bookOriginal.mainCharacters!!

            var bookStarRating: Float = if(bookOriginal.starRating != rbReview.rating) rbReview.rating
            else bookOriginal.starRating!!

            val bookLastTimeRead = System.currentTimeMillis() // they updated the info for the book so log it

            /* todo
                add:
                written review (save inside journalEntry variable)
                page they are on (save inside totalPagesRead variable)
                did they finish it (save inside userFinished variable)
             */

            val updatedBook = Book(
                bookOriginal.id,
                bookTitle,
                bookAuthors,
                bookPublicationDate,
                bookStarRating,
                bookPublisher,
                bookSummary,
                bookOriginal.pageCount,
                bookOriginal.thumbnail,
                bookOriginal.journalEntry,
                bookOriginal.userProgress,
                bookOriginal.userFinished,
                bookOriginal.isFav,
                bookPurchasedFrom,
                bookMainCharacters,
                bookGenres,
                bookTags,
                bookLastTimeRead,
                bookLastTimeRead,
                bookOriginal.prevReadCount,
                bookOriginal.startDate,
                bookOriginal.endDate,
                bookOriginal.totalPageCount,
                bookOriginal.totalPagesRead,
                bookOriginal.isbn
            )
            // now that we have the update book, let's update it on the database
            bookRepository.updateBookData(updatedBook)
            // now let's go back and pass it the new book information
            val intent = Intent(this, BookPageActivity::class.java)
            intent.putExtra("title", updatedBook.title)
            intent.putExtra("author", updatedBook.authorNames)
            intent.putExtra("publisher", updatedBook.publisher)
            intent.putExtra("publicationDate", updatedBook.publicationDate)
            intent.putExtra("rating", updatedBook.starRating)
            intent.putExtra("tags", updatedBook.tags)
            intent.putExtra("genres", updatedBook.genres)
            intent.putExtra("startDate", updatedBook.startDate)
            intent.putExtra("userFinished", updatedBook.userFinished)
            intent.putExtra("finishedDate", updatedBook.endDate)
            intent.putExtra("mainCharacters", updatedBook.mainCharacters)
            intent.putExtra("purchasedFrom", updatedBook.purchasedFrom)
            intent.putExtra("userReview", updatedBook.journalEntry)
            intent.putExtra("summary", updatedBook.description)
            intent.putExtra("lastPageRead", updatedBook.pageCount)
            intent.putExtra("id", updatedBook.id)
            intent.putExtra("isFav", updatedBook.isFav)
            intent.putExtra("thumbnail", updatedBook.thumbnail)
            startActivity(intent)

        }
    }

}