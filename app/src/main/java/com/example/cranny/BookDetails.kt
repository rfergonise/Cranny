package com.example.cranny

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class BookDetails : AppCompatActivity() {

    private lateinit var titleTV: TextView
    private lateinit var authorTV: TextView
    private lateinit var publisherTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var pageCountTV: TextView
    private lateinit var publishDateTV: TextView
    private lateinit var previewBtn: Button
    private lateinit var addBtn: Button
    private lateinit var bookIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        titleTV = findViewById(R.id.idTVTitle)
        authorTV = findViewById(R.id.idTVSubTitle)
        publisherTV = findViewById(R.id.idTVpublisher)
        descriptionTV = findViewById(R.id.idTVDescription)
        pageCountTV = findViewById(R.id.idTVNoOfPages)
        publishDateTV = findViewById(R.id.idTVPublishDate)
        previewBtn = findViewById(R.id.idBtnPreview)
        addBtn = findViewById(R.id.idBtnAdd)
        bookIV = findViewById(R.id.idIVbook)

        val book: Book = intent.getSerializableExtra("book") as Book

        titleTV.text = book.title
        authorTV.text = book.authorNames
        publisherTV.text = book.publisher
        publishDateTV.text = "Published On : ${book.publicationDate}"
        descriptionTV.text = book.description
        pageCountTV.text = "No Of Pages : ${book.pageCount}"
        Picasso.get().load(book.thumbnail).into(bookIV)

        previewBtn.setOnClickListener { view: View ->

        }

        addBtn.setOnClickListener { view: View ->

        }
    }
}