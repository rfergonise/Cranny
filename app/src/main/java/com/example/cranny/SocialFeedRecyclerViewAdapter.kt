package com.example.cranny


import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import com.squareup.picasso.Picasso


class SocialFeedRecyclerViewAdapter(private val context: Context, private val friendSocialFeed: MutableList<SocialFeed>)
    : RecyclerView.Adapter<SocialFeedRecyclerViewAdapter.MyViewHolder>()
{

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialFeedRecyclerViewAdapter.MyViewHolder
    {
        // This is where you inflate the layout (Giving a look to the rows)
        var inflater: LayoutInflater = LayoutInflater.from(context)
        var view: View = inflater.inflate(R.layout.recycler_view_social_row, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // assigning values to the views created in the recycler_view_row layout file
        // based on the position of the recycler view
        holder.tvBookTitle.text = formatBookTitle(friendSocialFeed[position].bookTitle)
        holder.tvBookAuthors.text = friendSocialFeed[position].bookAuthor
        holder.tvPageStatus.text = friendSocialFeed[position].status
        // todo load book covers
    }
    private fun formatBookTitle(title: String): String
    {
        if (title.length > 17)
        {
            // find the last space before or at 17 characters
            var replaceThisSpace: Int = title.substring(0, 17).lastIndexOf(' ')
            if (replaceThisSpace <= 0)
            {
                // no space found before 17 characters, replace at 17
                replaceThisSpace = 16
            }
            return title.substring(0, replaceThisSpace) + "\n" + title.substring(replaceThisSpace+1)
        }
        else return title
    }


    override fun getItemCount(): Int {
        // the recycler view just wants to know the number of items needed to display
        return friendSocialFeed.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        // grabbing the views from our recycler_view_row layout file
        var ivBookCover: ImageView = itemView.findViewById(R.id.ivSocialBookCover)
        var tvBookTitle: TextView = itemView.findViewById(R.id.tvSocialBookTitle)
        var tvBookAuthors: TextView = itemView.findViewById(R.id.tvSocialBookAuthor)
        var tvPageStatus: TextView = itemView.findViewById(R.id.tvSocialPageStatus)


    }



}