package com.example.cranny

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LibraryBookAdapter(
    private val libraryDataToDisplay: ArrayList<LibraryBookRecyclerData>): RecyclerView.Adapter<LibraryBookAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            LibraryBookAdapter.ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.book_cardview, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return libraryDataToDisplay.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val libraryBook: LibraryBookRecyclerData = libraryDataToDisplay[position]

        holder.libBookTitle.text = libraryDataToDisplay[position].libraryBookTitle
        holder.libAuthors.text = libraryDataToDisplay[position].libraryAuthorsName
        // Need to fix when we figure out images
        //holder.libBookImage.imageview = libraryDataToDisplay[position].libraryBookImage
    }

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val libBookTitle: TextView = itemView.findViewById(R.id.tvrvBookTitle)
        val libAuthors: TextView = itemView.findViewById(R.id.tvrvAuthorName)
        val libBookImage: ImageView = itemView.findViewById(R.id.rvBookImagePreview)
    }

}