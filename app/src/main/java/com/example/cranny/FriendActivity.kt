package com.example.cranny

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    lateinit var tvNoRecent: TextView

    // Used to store what will displayed in the user feed
    private val friendSocialFeed = ArrayList<SocialFeed>()

    lateinit var username: String
    lateinit var friendId: String



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend2)

        friendId = intent.getStringExtra("friendId")!!
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
        tvNoRecent = findViewById(R.id.tvNoRecent)

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

    }

    private fun setUpSocialProfile(friendId: String)
    {
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, friendId)
        profileRepo.profileData.observe(this) { userProfile ->
            // get the info
            username = userProfile.username
            setUpUserRecents()
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
        val database = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(database, Friend(friendId, username, false))
        bookRepository.fetchBookData()
        bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
            if(isBookDataReady)
            {
                // clear the list before adding items
                friendSocialFeed.clear()
                // grab each social feed from recentRepository.SocialFeeds
                for (book in bookRepository.Library) {
                    val bookId = book.id
                    val bookAuthors = book.authorNames
                    val bookTitle = book.title
                    // format the title to fit in the recycle view
                    var setTitle: String = formatBookTitle(bookTitle)
                    val isBookComplete = book.userFinished
                    // create the page status screen
                    var setStatus: String = "@" + username
                    if(book.userFinished)
                    {
                        setStatus += "\nFinished reading!"
                    }
                    else
                    {
                        setStatus += "\nRead "
                        setStatus += book.prevReadCount.toString()
                        if(book.prevReadCount != 1) setStatus += " pages."
                        else setStatus += " page."
                    }
                    val status = setStatus
                    val bookCoverURL = book.thumbnail
                    val dateRead = book.lastReadDate
                    val timeRead = book.lastReadTime
                    val socialFeed = SocialFeed(bookId, setTitle, bookAuthors!!, isBookComplete,
                        status, bookCoverURL!!, dateRead!!, timeRead!!, username,
                    book.mainCharacters!!, book.journalEntry!!, book.purchasedFrom!!, book.genres!!, book.tags!!, book.starRating!!)
                    // check if the item is already in the list before adding it
                    if (!friendSocialFeed.contains(socialFeed)) {
                        friendSocialFeed.add(socialFeed)
                    }
                }
                val rvSocial: RecyclerView = findViewById(R.id.rvSocial)
                // Check if friendSocialFeed is not empty before setting the adapter
                if (friendSocialFeed.isNotEmpty()) {
                    tvNoRecent.visibility = View.INVISIBLE
                    rvSocial.visibility = View.VISIBLE
                    val sortedFeeds: MutableList<SocialFeed> = friendSocialFeed.sortedByDescending { it.lastReadTime }.toMutableList()
                    bookRepository.stopBookListener()
                    // Set up the adapter
                    val adapter = SocialFeedRecyclerViewAdapter(this,this, sortedFeeds)
                    rvSocial.layoutManager = LinearLayoutManager(this)
                    rvSocial.adapter = adapter
                    adapter.notifyDataSetChanged() // Notify the adapter that the data set has changed
                } else {
                    tvNoRecent.visibility = View.VISIBLE
                    rvSocial.visibility = View.INVISIBLE
                }
            }
        })
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