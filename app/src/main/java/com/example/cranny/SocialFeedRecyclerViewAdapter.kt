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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso


class SocialFeedRecyclerViewAdapter(private val activity: AppCompatActivity, private val context: Context, private val friendSocialFeed: MutableList<SocialFeed>)
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
        friendSocialFeed[position].bookTitle = formatBookTitle(friendSocialFeed[position].bookTitle, 17)
        friendSocialFeed[position].mainCharacters = formatBookTitle(friendSocialFeed[position].mainCharacters, 45)
        friendSocialFeed[position].journalEntry = formatBookTitle(friendSocialFeed[position].journalEntry, 45)
        friendSocialFeed[position].purchasedFrom = formatBookTitle(friendSocialFeed[position].purchasedFrom, 45)
        friendSocialFeed[position].genres = formatBookTitle(friendSocialFeed[position].genres, 45)
        friendSocialFeed[position].tags = formatBookTitle(friendSocialFeed[position].tags, 45)
        holder.tvBookTitle.text = friendSocialFeed[position].bookTitle
        holder.tvBookAuthors.text = friendSocialFeed[position].bookAuthor
        holder.tvPageStatus.text = friendSocialFeed[position].status

        // when they click on the cover, show book info
        holder.mcvCoverBorder.setOnClickListener {
            val showBookPopUp = BookInfoFeedFragment.newInstance(friendSocialFeed[position])
            showBookPopUp.show(activity.supportFragmentManager, "showPopUp")
        }
    }
    private fun formatBookTitle(title: String, formatLength: Int): String
    {
        if (title.length > formatLength)
        {
            // find the last space before or at 17 characters
            var replaceThisSpace: Int = title.substring(0, formatLength).lastIndexOf(' ')
            if (replaceThisSpace <= 0)
            {
                // no space found before x characters, replace at x
                replaceThisSpace = formatLength - 1
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
        var mcvCoverBorder: MaterialCardView = itemView.findViewById(R.id.mcvCoverBorder)


    }



}