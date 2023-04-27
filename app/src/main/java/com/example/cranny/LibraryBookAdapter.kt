package com.example.cranny

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LibraryBookAdapter(
    private val data: List<LibraryBookRecyclerData>
) : RecyclerView.Adapter<LibraryBookAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val libBookTitle: TextView = view.findViewById(R.id.tvrvBookTitle)
        val libAuthors: TextView = view.findViewById(R.id.tvrvAuthorName)
        val libBookImage: ImageView = view.findViewById(R.id.rvBookImagePreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_cardview, parent, false)

        return ItemViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val libraryBook: LibraryBookRecyclerData = data[position]

        holder.libBookTitle.text = libraryBook.libraryBookTitle
        holder.libAuthors.text = libraryBook.libraryAuthorsName
        holder.libBookImage.setImageResource(libraryBook.libraryBookImage)
    }
}