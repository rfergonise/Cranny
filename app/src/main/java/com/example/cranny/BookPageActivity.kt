package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView

class BookPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_page)

        val abTitle : TextView = findViewById(R.id.tvBookTitle)
        val title : TextView = findViewById(R.id.tvbpBookTitle)
        val author : TextView = findViewById(R.id.tvbpAuthors)
        val genres : TextView = findViewById(R.id.tvbpGenres)
        val tags : TextView = findViewById(R.id.tvbpTags)
        val rating : RatingBar = findViewById(R.id.ratingBar2)

        val bundle : Bundle? = intent.extras
        val exTitle = bundle!!.getString("title")
        val exAuthor = bundle!!.getString("author")
        val exGenres = bundle!!.getString("genres")
        val exTags = bundle!!.getString("tags")
        val exRating = bundle!!.getInt("rating")

        abTitle.text = exTitle
        title.text = exTitle
        author.text = exAuthor
        genres.text = exGenres
        tags.text = exTags
        rating.rating = exRating.toFloat()

        val backBTN = findViewById<ImageButton>(R.id.bpBackButton)
        backBTN.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        val favoriteBTN = findViewById<ImageButton>(R.id.bpFavoriteButton)
        favoriteBTN.setOnClickListener() {


        }

    }
}