package com.example.cranny.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.example.cranny.Book
import com.example.cranny.BookDetails
import com.example.cranny.R

class BookAdapter(private val bookList: List<Book>, private val context: Context) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.idTVBookTitle)
        val publisherTV: TextView = view.findViewById(R.id.idTVpublisher)
        val pageCountTV: TextView = view.findViewById(R.id.idTVPageCount)
        val dateTV: TextView = view.findViewById(R.id.idTVDate)
        val bookIV: ImageView = view.findViewById(R.id.idIVbook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_rv_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = bookList[position]
        holder.nameTV.text = currentBook.title
        holder.publisherTV.text = currentBook.publisher
        holder.pageCountTV.text = "No of Pages : ${currentBook.pageCount}"
        holder.dateTV.text = currentBook.publicationDate

        if (!currentBook.thumbnail.isNullOrEmpty()) {
            Glide.with(context)
                .load(currentBook.thumbnail)
                .into(holder.bookIV)
        } else {
            // Set a placeholder or error image when there's no thumbnail.
            holder.bookIV.setImageResource(R.drawable.baseline_star_24)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetails::class.java).apply {
                putExtra("book", currentBook)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = bookList.size
}