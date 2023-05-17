package com.example.cranny.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        Picasso.get().load(currentBook.thumbnail).into(holder.bookIV)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetails::class.java).apply {
                putExtra("id", currentBook.id)
                putExtra("title", currentBook.title)
                putExtra("authorNames", currentBook.authorNames)
                putExtra("publicationDate", currentBook.publicationDate)
                putExtra("starRating", currentBook.starRating)
                putExtra("publisher", currentBook.publisher)
                putExtra("description", currentBook.description)
                putExtra("pageCount", currentBook.pageCount)
                putExtra("thumbnail", currentBook.thumbnail)
                putExtra("journalEntry", currentBook.journalEntry)
                putExtra("userProgress", currentBook.userProgress)
                putExtra("userFinished", currentBook.userFinished)
                putExtra("isFav", currentBook.isFav)
                putExtra("purchasedFrom", currentBook.purchasedFrom)
                putExtra("mainCharacters", currentBook.mainCharacters)
                putExtra("genres", currentBook.genres)
                putExtra("tags", currentBook.tags)
                putExtra("lastReadDate", currentBook.lastReadDate)
                putExtra("lastReadTime", currentBook.lastReadTime)
                putExtra("prevReadCount", currentBook.prevReadCount)
                putExtra("startDate", currentBook.startDate)
                putExtra("endDate", currentBook.endDate)
                putExtra("totalPageCount", currentBook.totalPageCount)
                putExtra("totalPagesRead", currentBook.totalPagesRead)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = bookList.size

}