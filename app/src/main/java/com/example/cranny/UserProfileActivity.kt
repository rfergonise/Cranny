package com.example.cranny

/**
This Kotlin file represents the UserProfileActivity, which is responsible for displaying the user's profile information,
including username, display name, bio, friend count, book count, and profile picture. It also sets up the user's social
profile by fetching data from the database and updating the UI components accordingly. The activity allows the user to
navigate to different sections such as friends, books, and the main menu. It uses various repositories and adapters to
fetch and display the necessary data, such as friend lists, favorite friends, and recent book activity. The UserProfileActivity
class extends AppCompatActivity and overrides the onCreate method to set up the activity's layout and initialize UI objects.
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
    lateinit var tvNoRecent: TextView
    lateinit var ivMainMenu: ImageView

    // Used to store what will displayed in the user feed
    private val userSocialFeed = ArrayList<SocialFeed>()

    lateinit var username: String
    lateinit var userId: String


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
        tvNoRecent = findViewById(R.id.tvNoRecent)
        ivMainMenu = findViewById(R.id.ivBackToMain)

        // load user data from database into profile text views / image view
        setUpSocialProfile()

        ivMainMenu.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

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
            val i = Intent(this, LibraryActivity::class.java)
            startActivity(i)
        }

    }

    /**
     * Sets up the social profile by fetching user data from the database and updating the UI components.
     */
    private fun setUpSocialProfile() {
        // Get the current user ID
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        // Get the instance of the Firebase database
        val database = FirebaseDatabase.getInstance()

        // Create a profile repository instance
        val profileRepo = ProfileRepository(database, userId!!)

        // Observe the profile data from the repository
        profileRepo.profileData.observe(this) { userProfile ->
            // Extract the user information from the profile data
            username = userProfile.username
            val name = userProfile.name
            val friendCount = userProfile.friendCount
            val bookCount = userProfile.bookCount
            val bio = userProfile.bio
            val id = userProfile.userId

            // Update the UI with the retrieved user information
            tvUsername.text = "@" + username
            tvBooksCount.text = bookCount.toString()
            tvFriendsCount.text = friendCount.toString()
            tvDisplayName.text = name.toString()
            tvBio.text = bio.toString()

            // Load the profile picture from the database into the image view
            val profilePictureRepository = ProfilePictureRepository(database, id)
            profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture)

            // Set up the favorite friend horizontal layout
            setUpFavoriteFriendHorizontalLayout(username, id)

            // Set up the user recents
            setUpUserRecents()

            // Stop listening to profile updates to prevent memory leaks
            profileRepo.stopProfileListener()
        }
    }
    /**
     * Sets up the horizontal layout to display favorite friends.
     *
     * @param username The username of the current user.
     * @param id The ID of the current user.
     */
    private fun setUpFavoriteFriendHorizontalLayout(username: String, id: String) {
        // Get the horizontal layout from the XML layout file
        val horizontalLayout = findViewById<LinearLayout>(R.id.horizontal_layout)

        // Initialize friend count and get the Firebase database instance
        var friendCount = 0
        val database = FirebaseDatabase.getInstance()

        // Create a friend repository instance
        val friendRepo = FriendRepository(database, username, id, this)

        // Fetch the friends from the repository
        friendRepo.fetchFriends()

        // Observe the friends ready status
        friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if (isFriendsReady) {
                // Get the total friend count
                friendCount = friendRepo.FriendIds.size

                if (friendCount > 0) {
                    var favoriteCount: Int = 0

                    // Iterate through the friend list
                    for (i in 0 until friendCount) {
                        if (friendRepo.FriendIds[i].isFavorite) {
                            // Create a MaterialCardView for the friend
                            val cardView = MaterialCardView(this)
                            val params = LinearLayout.LayoutParams(200, 200)
                            params.setMargins(16, 8, 16, 8)
                            cardView.layoutParams = params
                            cardView.radius = 130F
                            cardView.strokeWidth = 10
                            cardView.strokeColor = ContextCompat.getColor(this, R.color.cranny_blue_light)
                            cardView.cardElevation = 0F

                            // Create an ImageView for the friend's profile picture
                            val imageView = ImageView(this)
                            imageView.layoutParams = ViewGroup.LayoutParams(-1, -1)
                            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                            // Set an onClick listener for the friend's profile picture
                            imageView.setOnClickListener {
                                val friendIntent = Intent(this, FriendActivity::class.java)
                                friendIntent.putExtra("friendId", friendRepo.FriendIds[i].id)
                                startActivity(friendIntent)
                            }

                            // Load the friend's profile picture from the database into the ImageView
                            val profilePictureRepository = ProfilePictureRepository(database, friendRepo.FriendIds[i].id)
                            profilePictureRepository.loadProfilePictureIntoImageView(imageView)

                            // Add the ImageView to the MaterialCardView
                            cardView.addView(imageView)

                            // Add the MaterialCardView to the horizontal layout
                            horizontalLayout.addView(cardView)

                            // Increment the favorite count
                            favoriteCount++
                        }
                    }

                    if (favoriteCount == 0) {
                        // Show the "No Favorites" text view if no favorites are available
                        val showText: TextView = findViewById(R.id.tvNoFavorites)
                        showText.visibility = View.VISIBLE
                    }
                } else {
                    // Show the "No Favorites" text view if no friends are available
                    val showText: TextView = findViewById(R.id.tvNoFavorites)
                    showText.visibility = View.VISIBLE
                }
            }
        })

        // Stop listening to friend updates to prevent memory leaks
        friendRepo.stopFriendListener()
    }
    /**
     * Sets up the user's recent book activity.
     */
    private fun setUpUserRecents() {
        // Get the Firebase database instance
        val database = FirebaseDatabase.getInstance()

        // Create a book repository instance
        val bookRepository = BookRepository(database, Friend(userId, username, false))

        // Fetch the book data from the repository
        bookRepository.fetchBookData()

        // Observe the book data ready status
        bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
            if (isBookDataReady) {
                // Clear the list before adding items
                userSocialFeed.clear()

                // Iterate through each book in the book repository
                for (book in bookRepository.Library) {
                    val bookId = book.id
                    val bookAuthors = book.authorNames
                    val bookTitle = book.title

                    // Format the book title to fit in the RecyclerView
                    val setTitle: String = formatBookTitle(bookTitle, 17)

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

                    // Create a social feed object
                    val socialFeed = SocialFeed(
                        bookId,
                        setTitle,
                        bookAuthors!!,
                        isBookComplete,
                        status,
                        bookCoverURL!!,
                        dateRead!!,
                        timeRead!!,
                        username,
                        book.mainCharacters!!,
                        book.journalEntry!!,
                        book.purchasedFrom!!,
                        book.genres!!,
                        book.tags!!,
                        book.starRating!!,
                        book.totalPageCount!!,
                        book.totalPagesRead!!
                    )

                    // Check if the social feed is already in the list before adding it
                    if (!userSocialFeed.contains(socialFeed)) {
                        userSocialFeed.add(socialFeed)
                    }
                }

                val rvSocial: RecyclerView = findViewById(R.id.rvSocial)

                if (userSocialFeed.isNotEmpty()) {
                    // Show the RecyclerView if the user's social feed is not empty
                    tvNoRecent.visibility = View.INVISIBLE
                    rvSocial.visibility = View.VISIBLE

                    // Sort the social feeds by descending last read time
                    val sortedFeeds: MutableList<SocialFeed> =
                        userSocialFeed.sortedByDescending { it.lastReadTime }.toMutableList()

                    // Stop listening to book updates to prevent memory leaks
                    bookRepository.stopBookListener()

                    // Set up the adapter for the RecyclerView
                    val adapter = SocialFeedRecyclerViewAdapter(this, this, sortedFeeds)
                    rvSocial.layoutManager = LinearLayoutManager(this)
                    rvSocial.adapter = adapter
                    adapter.notifyDataSetChanged() // Notify the adapter that the data set has changed
                } else {
                    // Show the "No Recent Activity" text view if the user's social feed is empty
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