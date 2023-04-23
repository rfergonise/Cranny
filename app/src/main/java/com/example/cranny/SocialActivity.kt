package com.example.cranny

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class SocialActivity : AppCompatActivity() {

    // UI elements needed
    private lateinit var ivBackToMain: ImageView
    private lateinit var ivShowProfile: ImageView

    // Used to store what will displayed in the social feed
    private val friendSocialFeed = ArrayList<SocialFeed>()


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        // link the ui elements
        ivBackToMain = findViewById(R.id.ivBackToMain)
        ivShowProfile = findViewById(R.id.ivProfileVisibility)

        // load book data from database into friendSocialFeed then populate the recycler view adapter
        setUpSocialFeed()

        // Back Menu Button On Click Event
        ivBackToMain.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

       // Back Menu Button On Click Event
       ivShowProfile.setOnClickListener {
           val i = Intent(this, UserProfileActivity::class.java)
           startActivity(i)
       }

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

    private fun setUpSocialFeed()
    {
        // todo change it from the user's recents to the user's friend's recents
        val database = FirebaseDatabase.getInstance()
        val recentRepository = RecentRepository(database)
        recentRepository.fetchRecentData()
        recentRepository.isRecentDataReady.observe(this) { isRecentDataReady ->
            if (isRecentDataReady) {
                // clear the list before adding items
                friendSocialFeed.clear()
                // grab each social feed from recentRepository.SocialFeeds
                for (feed in recentRepository.SocialFeeds) {
                    val bookId = feed.id
                    val bookAuthors = feed.bookAuthor
                    val bookTitle = feed.bookTitle
                    // format the title to fit in the recycle view
                    var setTitle: String = formatBookTitle(bookTitle)
                    val isBookComplete = feed.isBookComplete
                    val status = feed.status
                    val bookCoverURL = feed.bookCoverURL
                    val dateRead = feed.lastReadDate
                    val timeRead = feed.lastReadTime
                    val username = feed.username
                    val socialFeed = SocialFeed(bookId, setTitle, bookAuthors, isBookComplete,
                        status, bookCoverURL, dateRead, timeRead, username)
                    // check if the item is already in the list before adding it
                    if (!friendSocialFeed.contains(socialFeed)) {
                        friendSocialFeed.add(socialFeed)
                    }
                }
                // Check if friendSocialFeed is not empty before setting the adapter
                if (friendSocialFeed.isNotEmpty()) {
                    recentRepository.stopRecentListener()
                    // Set up the adapter
                    val rvSocial: RecyclerView = findViewById(R.id.rvSocial)
                    val adapter = SocialFeedRecyclerViewAdapter(this, friendSocialFeed)
                    rvSocial.layoutManager = LinearLayoutManager(this)
                    rvSocial.adapter = adapter
                    adapter.notifyDataSetChanged() // Notify the adapter that the data set has changed
                } else {
                    Toast.makeText(this, "Friend's social feed is empty.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}