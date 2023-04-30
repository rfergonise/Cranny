package com.example.cranny

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase

class FriendActivity : AppCompatActivity()
{

    // Declare UI Objects
    lateinit var ivHideFriendProfile: ImageView
    lateinit var ivBackToMain: ImageView
    lateinit var ivProfilePicture: ImageView
    lateinit var tvUsername: TextView
    lateinit var tvDisplayName: TextView
    lateinit var tvBio: TextView
    lateinit var tvFriendsCount: TextView
    lateinit var tvBooksCount: TextView

    // Used to store what will displayed in the user feed
    private val friendSocialFeed = ArrayList<SocialFeed>()

    lateinit var username: String



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend2)

        val friendId = intent.getStringExtra("friendId")
        // friendId = null

        // Initialize UI Objects
        ivHideFriendProfile = findViewById(R.id.ivReturnToProfile)
        ivBackToMain = findViewById(R.id.ivBackToMain)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        tvUsername = findViewById(R.id.tvProfileUsername)
        tvDisplayName = findViewById(R.id.tvProfileDisplayName)
        tvBio = findViewById(R.id.tvProfileBio)
        tvFriendsCount = findViewById(R.id.tvTotalFriends)
        tvBooksCount = findViewById(R.id.tvTotalBooks)

        // load user data from database into profile text views / image view
        setUpSocialProfile(friendId!!)

        // Hide Profile On Click Listener
        ivHideFriendProfile.setOnClickListener {
            val i = Intent(this, UserProfileActivity::class.java)
            startActivity(i)
        }

        // Hide Profile On Click Listener
        ivBackToMain.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        // load the user's most recent reads into the recycler view
        setUpUserRecents()
    }

    private fun setUpSocialProfile(friendId: String)
    {
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, friendId)
        profileRepo.profileData.observe(this) { userProfile ->
            // get the info
            username = userProfile.username
            val name = userProfile.name
            val friendCount = userProfile.friendCount
            val bookCount = userProfile.bookCount
            val bio = userProfile.bio
            val id = userProfile.userId

            // Change the display username and friend/book count to the values stored in the database
            tvUsername.text = "@" + username
            tvBooksCount.text = bookCount.toString()
            tvFriendsCount.text = friendCount.toString()
            tvDisplayName.text = name.toString()
            tvBio.text = bio.toString()

            // Load the profile picture from database into image view
            val profilePictureRepository = ProfilePictureRepository(database, id)
            profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture)


            profileRepo.stopProfileListener()
        }
    }

    private fun setUpUserRecents()
    {
        // todo change it from the user's recents to their friend's recents
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
                    var _username: String = ""
                    if(username != null) _username = username
                    val socialFeed = SocialFeed(bookId, setTitle, bookAuthors, isBookComplete,
                        status, bookCoverURL, dateRead, timeRead, _username)
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
}