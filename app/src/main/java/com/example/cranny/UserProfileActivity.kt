package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text


class UserProfileActivity : AppCompatActivity()
{

    // Declare UI Objects
    lateinit var ivHideProfile: ImageView
    lateinit var ivProfilePicture: ImageView
    lateinit var tvUsername: TextView
    lateinit var tvDisplayName: TextView
    lateinit var tvBio: TextView
    lateinit var tvFriendsCount: TextView
    lateinit var tvBooksCount: TextView
    lateinit var cvBookButton: MaterialCardView
    lateinit var cvFriendButton: MaterialCardView

    // Used to store what will displayed in the user feed
    private val userSocialFeed = ArrayList<SocialFeed>()

    lateinit var username: String


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize UI Objects
        ivHideProfile = findViewById(R.id.ivProfileVisibility)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        tvUsername = findViewById(R.id.tvProfileUsername)
        tvDisplayName = findViewById(R.id.tvProfileDisplayName)
        tvBio = findViewById(R.id.tvProfileBio)
        tvFriendsCount = findViewById(R.id.tvTotalFriends)
        tvBooksCount = findViewById(R.id.tvTotalBooks)
        cvBookButton = findViewById(R.id.cvBookCount)
        cvFriendButton = findViewById(R.id.cvFriendCount)

        // load user data from database into profile text views / image view
        setUpSocialProfile()

        // Hide Profile On Click Listener
        ivHideProfile.setOnClickListener {
            val i = Intent(this, SocialActivity::class.java)
            startActivity(i)
        }

        // Friend On Click Listener
        cvFriendButton.setOnClickListener {
            val i = Intent(this, FriendSearchActivity::class.java)
            startActivity(i)
        }

        // Book On Click Listener
        cvBookButton.setOnClickListener {
            // todo open user library activity
        }

        // load the user's most recent reads into the recycler view
        setUpUserRecents()
    }

    private fun setUpSocialProfile()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, userId!!)
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


            setUpFavoriteFriendHorizontalLayout(username, id)

            profileRepo.stopProfileListener()
        }
    }
    private fun setUpFavoriteFriendHorizontalLayout(username: String, id: String)
    {
        // todo set it up to load favorite friends instead of just every friend
        val horizontalLayout = findViewById<LinearLayout>(R.id.horizontal_layout)
        var friendCount = 0
        val database = FirebaseDatabase.getInstance()
        val friendRepo = FriendRepository(database, username, id, this)
        friendRepo.fetchFriends()
        friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if(isFriendsReady)
            {
                friendCount = friendRepo.FriendIds.size
                if(friendCount > 0)
                {
                    for (i in 0 until friendCount) {
                        val cardView = MaterialCardView(this)
                        val params = LinearLayout.LayoutParams(200, 200)
                        params.setMargins(16,8,16,8)
                        cardView.layoutParams = params
                        cardView.radius = 130F
                        cardView.strokeWidth = 10
                        cardView.strokeColor = ContextCompat.getColor(this, R.color.cranny_blue_light)
                        cardView.cardElevation = 0F

                        val imageView = ImageView(this)
                        imageView.layoutParams = ViewGroup.LayoutParams(-1, -1)
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                        // Set onClick listener
                        imageView.setOnClickListener {
                            val friendIntent = Intent(this, FriendActivity::class.java)
                            friendIntent.putExtra("friendId", friendRepo.FriendIds[i].id)
                            // friendRepo.FriendIds[i] != null
                            startActivity(friendIntent)
                        }

                        val profilePictureRepository = ProfilePictureRepository(database, friendRepo.FriendIds[i].id)
                        profilePictureRepository.loadProfilePictureIntoImageView(imageView)

                        cardView.addView(imageView)
                        horizontalLayout.addView(cardView)
                    }
                }
                else
                {
                    val showText: TextView = findViewById(R.id.tvNoFavorites)
                    showText.visibility = View.VISIBLE
                }
            }
        })
        friendRepo.stopFriendListener() // free the listener to stop memory leaks

    }
    private fun setUpUserRecents()
    {
        val database = FirebaseDatabase.getInstance()
        val recentRepository = RecentRepository(database)
        recentRepository.fetchRecentData()
        recentRepository.isRecentDataReady.observe(this) { isRecentDataReady ->
            if (isRecentDataReady) {
                // clear the list before adding items
                userSocialFeed.clear()
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
                    if (!userSocialFeed.contains(socialFeed)) {
                        userSocialFeed.add(socialFeed)
                    }
                }
                // Check if friendSocialFeed is not empty before setting the adapter
                if (userSocialFeed.isNotEmpty()) {
                    recentRepository.stopRecentListener()
                    // Set up the adapter
                    val rvSocial: RecyclerView = findViewById(R.id.rvSocial)
                    val adapter = SocialFeedRecyclerViewAdapter(this, userSocialFeed)
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