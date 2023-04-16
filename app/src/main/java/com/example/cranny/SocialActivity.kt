package com.example.cranny

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide


class SocialActivity : AppCompatActivity() {

    private lateinit var tvPageTitle: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvFriendsTitle: TextView
    private lateinit var tvFriendsCount: TextView
    private lateinit var tvBooksTitle: TextView
    private lateinit var tvBooksCount: TextView
    private lateinit var ivProfilePicture: ImageView


    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val userDatabase = FirebaseDatabase.getInstance().getReference("UserData")

    private val friendSocialFeed = ArrayList<SocialFeed>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        tvPageTitle = findViewById(R.id.tvTitle)
        tvUsername = findViewById(R.id.tvTitleUsername)
        tvFriendsTitle = findViewById(R.id.tvTitleFriends)
        tvFriendsCount = findViewById(R.id.tvTotalFriends)
        tvBooksTitle = findViewById(R.id.tvTitleBooks)
        tvBooksCount = findViewById(R.id.tvTotalBooks)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)

        setUpSocialProfile()

        setUpSocialFeed()

    }

    fun loadImageFromUrl(url: String, imageView: ImageView, context: Context) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }


    private fun setUpSocialProfile()
    {
        userDatabase.child(userId).child("Profile").get().addOnSuccessListener {

            if(it.exists())
            {
                val username = it.child("Username").value as String
                val name = it.child("Name").value as String
                val friendCount = (it.child("FriendCount").value as Long).toInt()
                val bookCount = (it.child("BookCount").value as Long).toInt()
                val pfpURL = it.child("ProfilePictureURL").value as String

                // Change the display username and friend/book count to the values stored in the database
                tvUsername.text = "@" + username
                tvBooksCount.text = bookCount.toString()
                tvFriendsCount.text = bookCount.toString()

                // Load the profile picture from the url stored in the database
                loadImageFromUrl(pfpURL, ivProfilePicture, this)

            }
            else
            {
                Toast.makeText(this, "User does not exist.", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to read database.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setUpSocialFeed() {
        userDatabase.child(userId).child("Recents").get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (ds in dataSnapshot.children) {
                    val id = ds.child("Id").getValue(String::class.java) ?: ""
                    val bookTitle = ds.child("BookTitle").getValue(String::class.java) ?: ""
                    val bookAuthor = ds.child("BookAuthor").getValue(String::class.java) ?: ""
                    val isBookComplete = ds.child("IsBookComplete").getValue(Boolean::class.java) ?: false
                    val pagesRead = ds.child("PagesRead").getValue(Int::class.java) ?: 0
                    val bookCoverURL = ds.child("BookCoverURL").getValue(String::class.java) ?: ""
                    val dateRead = ds.child("DateRead").getValue(String::class.java) ?: ""
                    val timeRead = ds.child("TimeRead").getValue(String::class.java) ?: ""
                    val username = ds.child("Username").getValue(String::class.java) ?: ""


                    // format the title to fit in the recycle view
                    var setTitle: String = bookTitle
                    if (setTitle.length > 17) {
                        // find the last space before or at 17 characters
                        var replaceThisSpace: Int = setTitle.substring(0, 17).lastIndexOf(' ')
                        if (replaceThisSpace <= 0) {
                            // no space found before 17 characters, replace at 17
                            replaceThisSpace = 16
                        }
                        setTitle = setTitle.substring(0, replaceThisSpace) + "\n" + setTitle.substring(replaceThisSpace + 1)
                    }

                    // create the page status screen
                    var setStatus: String = username
                    if(isBookComplete)
                    {
                        setStatus += "\nFinished reading!"
                    }
                    else
                    {
                        setStatus += "\nRead "
                        setStatus += pagesRead.toString()
                        if(pagesRead != 1) setStatus += " pages."
                        else setStatus += " page."
                    }


                    val socialFeed = SocialFeed(
                        id,
                        setTitle,
                        bookAuthor,
                        isBookComplete,
                        setStatus,
                        bookCoverURL,
                        dateRead,
                        timeRead,
                        username
                    )
                    friendSocialFeed.add(socialFeed)
                }

                // Check if friendSocialFeed is not empty before setting the adapter
                if (friendSocialFeed.isNotEmpty()) {
                    // Set up the adapter
                    var rvSocial: RecyclerView = findViewById(R.id.rvSocial)
                    val adapter = SocialFeedRecyclerViewAdapter(this, friendSocialFeed)
                    rvSocial.layoutManager = LinearLayoutManager(this)
                    rvSocial.adapter = adapter
                    adapter.notifyDataSetChanged() // Add this line to notify the adapter that the data set has changed
                }
                else {
                    Toast.makeText(this, "Friend's social feed is empty.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User does not exist.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to read database.", Toast.LENGTH_SHORT).show()
        }
    }
}