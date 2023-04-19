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
import com.bumptech.glide.Glide

class SocialActivity : AppCompatActivity() {

    // UI elements needed
    private lateinit var tvUsername: TextView
    private lateinit var tvFriendsCount: TextView
    private lateinit var tvBooksCount: TextView
    private lateinit var ivProfilePicture: ImageView
    private lateinit var ivBackToMain: ImageView

    // Used to store what will displayed in the social feed
    private val friendSocialFeed = ArrayList<SocialFeed>()


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        // link the ui elements
        tvUsername = findViewById(R.id.tvTitleUsername)
        tvFriendsCount = findViewById(R.id.tvTotalFriends)
        tvBooksCount = findViewById(R.id.tvTotalBooks)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        ivBackToMain = findViewById(R.id.ivBackToMain)

        // load user data from database into profile text views / image view
        setUpSocialProfile()

        // load book data from database into friendSocialFeed then populate the recycler view adapter
        setUpSocialFeed()

        // Back Menu Button On Click Event
        ivBackToMain.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

    }

    private fun loadImageFromUrl(url: String, imageView: ImageView, context: Context) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }


    private fun setUpSocialProfile()
    {
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database)
        profileRepo.profileData.observe(this) { userProfile ->
            // get the info
            val username = userProfile.username
            val name = userProfile.name
            val friendCount = userProfile.friendCount
            val bookCount = userProfile.bookCount
            val pfpURL = userProfile.profile
            val bio = userProfile.bio
            val id = userProfile.userId

            // Change the display username and friend/book count to the values stored in the database
            tvUsername.text = "@" + username
            tvBooksCount.text = bookCount.toString()
            tvFriendsCount.text = friendCount.toString()

            // Load the profile picture from the url stored in the database
            loadImageFromUrl(pfpURL, ivProfilePicture, this)
            profileRepo.stopProfileListener()
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