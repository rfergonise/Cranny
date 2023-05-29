package com.example.cranny

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LibraryBookAdapter(
    private val libraryDataToDisplay: ArrayList<LibraryBookRecyclerData>,
    private val listener: onBookClickListener
    ): RecyclerView.Adapter<LibraryBookAdapter.ItemViewHolder>() {

    interface onBookClickListener {
        fun onItemClick(position: Int)
    }

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

        holder.libBookTitle.text = libraryDataToDisplay[position].bookTitle
        holder.libAuthors.text = libraryDataToDisplay[position].bookAuthor
        // Need to fix when we figure out images
        //holder.libBookImage.imageview = libraryDataToDisplay[position].libraryBookImage
        //added by will for thumbnail
        Glide.with(holder.itemView.context)
            .load(libraryDataToDisplay[position].bookImage)  // Assuming bookCoverURL is the name of the field containing the URL of the book cover image
            .error(R.drawable.logo_mid_green)  // A placeholder image in case of any error while loading the actual image
            .into(holder.libBookImage)
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            val libBookTitle: TextView = itemView.findViewById(R.id.tvrvBookTitle)
            val libAuthors: TextView = itemView.findViewById(R.id.tvrvAuthorName)
            val libBookImage: ImageView = itemView.findViewById(R.id.rvBookImagePreview)



            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }
    }


