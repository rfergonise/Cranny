package com.example.cranny.model

import com.bumptech.glide.Glide
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.cranny.Book
import com.example.cranny.BookSuggestion
import com.example.cranny.R
import com.example.cranny.network.googlebooks.RetrofitInstance
import com.example.cranny.network.googlebooks.apiKey
import java.util.*

class BookSuggestionAdapter(context: Context, private val suggestions: List<BookSuggestion>, private val itemClickListener: OnItemClickListener)
    : ArrayAdapter<BookSuggestion>(context, R.layout.item_book_suggestion, suggestions) {

    var selectedBook: BookSuggestion? = null

    interface OnItemClickListener {
        fun onItemClick(book: BookSuggestion)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_book_suggestion, parent, false)

        val bookSuggestion = suggestions[position]

        val bookCoverImage = view.findViewById<ImageView>(R.id.bookCoverImage)
        val bookTitle = view.findViewById<TextView>(R.id.bookTitle)

        Glide.with(context)
            .load(bookSuggestion.imageUrl)
            .into(bookCoverImage)

        bookTitle.text = bookSuggestion.title

        // When a book item is clicked, set selectedBook to the clicked book and call the listener
        view.setOnClickListener {
            selectedBook = bookSuggestion
            itemClickListener.onItemClick(bookSuggestion)
        }

        return view
    }

}

