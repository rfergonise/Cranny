package com.example.cranny

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class bookSuggestionAdapter(context: Context, private val suggestions: MutableList<BookSuggestion>, private val itemClickListener: AdapterView.OnItemClickListener) : ArrayAdapter<BookSuggestion>(context, R.layout.item_book_suggestion, suggestions) {
    var selectedBook: BookSuggestion? = null


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_book_suggestion, parent, false)
//
//        val bookSuggestion = suggestions[position]
//
//        val bookCoverImage = view.findViewById<ImageView>(R.id.bookCoverImage)
//        val bookTitle = view.findViewById<TextView>(R.id.bookTitle)
//
//        Glide.with(context)
//            .load(bookSuggestion.thumbnail)
//            .into(bookCoverImage)
//
//        bookTitle.text = bookSuggestion.title
//
//        view.setOnClickListener {
//            selectedBook = bookSuggestion
//            itemClickListener.onItemClick(null, it, position, 0L)
//        }

        return view
    }



    fun updateSuggestions(newSuggestions: List<BookSuggestion>) {
        suggestions.clear()
        suggestions.addAll(newSuggestions)
        notifyDataSetChanged()
    }


}