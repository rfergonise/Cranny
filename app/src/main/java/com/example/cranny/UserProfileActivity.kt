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

    private fun setUpSocialProfile()
    {
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
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
            setUpUserRecents()
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
                    var favoriteCount: Int = 0
                    for (i in 0 until friendCount) {

                        if(friendRepo.FriendIds[i].isFavorite)
                        {
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
                            favoriteCount++
                        }
                    }
                    if(favoriteCount == 0)
                    {
                        val showText: TextView = findViewById(R.id.tvNoFavorites)
                        showText.visibility = View.VISIBLE
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
        val bookRepository = BookRepository(database, Friend(userId, username, false))
        bookRepository.fetchBookData()
        bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
            if(isBookDataReady)
            {
                // clear the list before adding items
                userSocialFeed.clear()
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
                    if (!userSocialFeed.contains(socialFeed)) {
                        userSocialFeed.add(socialFeed)
                    }
                }
                val rvSocial: RecyclerView = findViewById(R.id.rvSocial)
                // Check if friendSocialFeed is not empty before setting the adapter
                if (userSocialFeed.isNotEmpty()) {
                    tvNoRecent.visibility = View.INVISIBLE
                    rvSocial.visibility = View.VISIBLE
                    val sortedFeeds: MutableList<SocialFeed> = userSocialFeed.sortedByDescending { it.lastReadTime }.toMutableList()
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