package com.example.cranny

/**
FriendActivity is an activity class that represents a friend's profile in the Cranny app.
It displays the friend's profile information, such as their username, display name, bio,
friend count, and book count. It also shows the friend's recent social feeds, including the
books they have read, with details like book title, authors, status, and cover image.
The activity retrieves data from Firebase Realtime Database and uses RecyclerView to display
the social feeds in a scrollable list. Users can navigate back to the main screen or hide
the friend's profile using the provided buttons.
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    /**
     * Sets up the social profile for a given friend ID.
     *
     * @param friendId The ID of the friend whose social profile is being set up.
     */
    private fun setUpSocialProfile(friendId: String) {
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, friendId)

        profileRepo.profileData.observe(this) { userProfile ->
            // Get the information from the user profile
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

            // Load the profile picture from the database into the image view
            val profilePictureRepository = ProfilePictureRepository(database, id)
            profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture)

            profileRepo.stopProfileListener()
        }
    }

    /**
     * Sets up the user's recent social feeds.
     */
    private fun setUpUserRecents() {
        val database = FirebaseDatabase.getInstance()
        val bookRepository = BookRepository(database, Friend(friendId, username, false))

        bookRepository.fetchBookData()
        bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
            if (isBookDataReady) {
                // Clear the list before adding items
                friendSocialFeed.clear()

                // Retrieve each social feed from the book repository's library
                for (book in bookRepository.Library) {
                    val bookId = book.id
                    val bookAuthors = book.authorNames
                    val bookTitle = book.title

                    // Format the title to fit in the RecyclerView
                    var setTitle: String = formatBookTitle(bookTitle, 17)

                    val isBookComplete = book.userFinished

                    // Create the book status message
                    var setStatus: String = "@" + username
                    if (book.userFinished) {
                        setStatus += "\nFinished reading!"
                    } else {
                        setStatus += "\nRead "
                        setStatus += book.prevReadCount.toString()
                        if (book.prevReadCount != 1) setStatus += " pages."
                        else setStatus += " page."
                    }
                    val status = setStatus

                    val bookCoverURL = book.thumbnail
                    val dateRead = book.lastReadDate
                    val timeRead = book.lastReadTime

                    val socialFeed = SocialFeed(
                        bookId, setTitle, bookAuthors!!, isBookComplete,
                        status, bookCoverURL!!, dateRead!!, timeRead!!, username,
                        book.mainCharacters!!, book.journalEntry!!, book.purchasedFrom!!,
                        book.genres!!, book.tags!!, book.starRating!!, book.totalPageCount!!, book.totalPagesRead!!
                    )

                    // Check if the item is already in the list before adding it
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
                    val adapter = SocialFeedRecyclerViewAdapter(this, this, sortedFeeds)
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

    /**
     * Formats the book title to fit within a specified length and adds a line break if necessary.
     *
     * @param title The book title to format.
     * @param formatLength The desired length to format the title.
     * @return The formatted book title.
     */
    private fun formatBookTitle(title: String, formatLength: Int): String {
        if (title.length > formatLength) {
            // Find the last space before or at the specified length
            var replaceThisSpace: Int = title.substring(0, formatLength).lastIndexOf(' ')

            if (replaceThisSpace <= 0) {
                // No space found before the specified length, replace at the specified length - 1
                replaceThisSpace = formatLength - 1
            }

            // Add a line break at the space position
            return title.substring(0, replaceThisSpace) + "\n" + title.substring(replaceThisSpace + 1)
        } else {
            return title // Return the original title if it doesn't need formatting
        }
    }
}