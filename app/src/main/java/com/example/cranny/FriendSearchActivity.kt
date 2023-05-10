package com.example.cranny

/**
FriendSearchActivity is an activity in the Android application that allows users to search for friends.
It displays a list of friends and provides features such as adding friends, viewing friend requests,
and searching for specific friends. The activity communicates with Firebase Realtime Database to fetch
and update the friend data. The user interface includes a search bar, a list of friends, and options
to navigate back to the main screen or the user profile. The FriendSearchActivity class also utilizes
the SearchAdapter class, which is a RecyclerView adapter responsible for binding friend data to the UI
and handling user interactions.
 */

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class FriendSearchActivity : AppCompatActivity()
{

    private var friendList = mutableListOf<Friend>()
    private lateinit var rvFriends: RecyclerView
    lateinit var user: User
    lateinit var tvNoFriend: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_search)

        tvNoFriend = findViewById(R.id.tvNoFriendFound) // Retrieves the reference to the "tvNoFriendFound" TextView

        val ivBackToMainButton: ImageView = findViewById(R.id.ivBackToMain)
        ivBackToMainButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        val ivBackToProfileButton: ImageView = findViewById(R.id.ivProfileButton)
        ivBackToProfileButton.setOnClickListener {
            val i = Intent(this, UserProfileActivity::class.java)
            startActivity(i)
        }

        val buttonAddFriend: MaterialCardView = findViewById(R.id.mcvAddButton)
        buttonAddFriend.setOnClickListener {
            val i = Intent(this, AddFriendActivity::class.java)
            startActivity(i)
        }

        val buttonFriendRequest: MaterialCardView = findViewById(R.id.mcvRequestButton)
        buttonFriendRequest.setOnClickListener {
            val i = Intent(this, FriendRequestActivity::class.java)
            startActivity(i)
        }

        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser // Retrieves the current user from FirebaseAuth

        val profileRepo = ProfileRepository(database, currentUser?.uid!!)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            user = User(
                userProfile.userId,
                userProfile.name,
                userProfile.username,
                userProfile.friendCount,
                userProfile.bookCount,
                userProfile.bio
            )
            rvFriends = findViewById(R.id.rvFriendSearch) // Retrieves the reference to the RecyclerView

            getFriendList(user) // Retrieves and populates the friend list for the user
        })

        profileRepo.stopProfileListener() // Stops listening for updates on the user's profile to prevent memory leaks
    }


    /**
     * Retrieves and populates the friend list for the given user.
     * This function fetches the user's friends from the database,
     * observes the readiness of the friend list, and updates the UI accordingly.
     * It also stops the friend listener to prevent memory leaks.
     *
     * @param user The User object representing the current user.
     */
    private fun getFriendList(user: User) {
        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase

        val friendRepo = FriendRepository(database, user.username, user.userId, this)
        friendRepo.fetchFriends() // Fetches the friends of the user from the repository

        friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if (isFriendsReady) {
                val friendCount = friendRepo.FriendIds.size
                if (friendCount > 0) {
                    for (friend in friendRepo.FriendIds) {
                        friendList.add(friend) // Populates the friend list with the retrieved friends
                    }
                    rvFriends.layoutManager = LinearLayoutManager(this) // Sets the layout manager for the RecyclerView
                    rvFriends.setHasFixedSize(true) // Optimizes performance by indicating that the item size in the RecyclerView is fixed
                    rvFriends.adapter = SearchAdapter(
                        this@FriendSearchActivity,
                        this@FriendSearchActivity,
                        friendList,
                        user,
                        tvNoFriend
                    ) // Sets the adapter for the RecyclerView to display the friend list
                } else {
                    tvNoFriend.visibility = View.VISIBLE
                    rvFriends.visibility = View.INVISIBLE
                    val searchBar: MaterialCardView = findViewById(R.id.mcvSearchBox)
                    searchBar.visibility = View.INVISIBLE
                }
            }
        })

        friendRepo.stopFriendListener() // Stops listening for updates on the friend list to prevent memory leaks
    }

    /**
     * Called when the activity is resumed.
     * This function sets up a text changed listener for the search bar,
     * filters the friend list based on the entered text, and updates the UI accordingly.
     */
    override fun onResume() {
        super.onResume()

        val searchEditText = findViewById<EditText>(R.id.etSearchBar)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = friendList.filter { friend ->
                    friend.username.startsWith(s.toString(), ignoreCase = true)
                }
                if (user?.username != null) {
                    if (filteredList.size == 0)
                        tvNoFriend.visibility = View.VISIBLE
                    else
                        tvNoFriend.visibility = View.INVISIBLE
                    rvFriends.adapter = SearchAdapter(
                        this@FriendSearchActivity,
                        this@FriendSearchActivity,
                        filteredList.toMutableList(),
                        user,
                        tvNoFriend
                    ) // Sets the adapter for the RecyclerView with the filtered friend list
                }
            }
        })
    }
}

class SearchAdapter(private val owner: LifecycleOwner, val context: Context, private val usernames: MutableList<Friend>, private var user: User, private var tvNoFriend: TextView): RecyclerView.Adapter<SearchAdapter.MyViewHolder>()
{

    /**
     * Creates a new ViewHolder by inflating the layout for each item in the RecyclerView.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view.
     * @return The created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_friend_view, parent, false)

        // Create and return a new ViewHolder with the inflated layout
        return MyViewHolder(itemView)
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The item count.
     */
    override fun getItemCount(): Int {
        // Return the size of the usernames list
        return usernames.size
    }


    /**
     * Binds the data to the ViewHolder and sets up the interaction logic for each item in the RecyclerView.
     *
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item in the RecyclerView.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Get the username and ID of the current item
        val username = usernames[position].username
        val id = usernames[position].id

        // Display the username with an '@' symbol
        holder.tvUsername.text = "@${username}"

        // Set up favorite/unfavorite functionality for the friend
        FriendFavorite(usernames[position], holder.ivUnFavorite, holder.ivFavorite)

        // Set up click listener for removing a friend
        holder.ivRemoveFriend.setOnClickListener {
            // Create a Friend object to remove
            val removeFriend = Friend(id, username, false)

            // Get the Firebase database instance
            val database = FirebaseDatabase.getInstance()

            // Create a FriendRepository instance with relevant information
            val friendRepo = FriendRepository(database, user.username, user.userId, owner)

            // Remove the friend using the FriendRepository
            friendRepo.removeFriend(removeFriend)

            // Hide the remove friend button
            holder.ivRemoveFriend.visibility = View.INVISIBLE

            // Remove the friend from the list and update the RecyclerView
            usernames.removeAt(position)
            notifyItemRemoved(position)

            // Show a message if the list becomes empty
            if (usernames.size == 0) {
                tvNoFriend.visibility = View.VISIBLE
            }

            // Update the user's friend count
            user.friendCount--

            // Create a ProfileRepository instance with the user's ID
            val profileRepo = ProfileRepository(database, user.userId)

            // Update the user's profile data with the new friend count
            profileRepo.updateProfileData(
                user.username,
                user.name,
                user.userId,
                user.bio,
                user.friendCount,
                user.bookCount
            )
        }

        // Set up click listener for opening the friend's activity
        holder.tvUsername.setOnClickListener {
            // Create an Intent to open the FriendActivity
            val friendIntent = Intent(context, FriendActivity::class.java)

            // Pass the friend's ID as an extra to the Intent
            friendIntent.putExtra("friendId", id)

            // Start the FriendActivity
            context.startActivity(friendIntent)
        }
    }

    /**
     * Updates the favorite/unfavorite status and sets up the click listeners for the friend's favorite icon.
     *
     * @param friend The Friend object.
     * @param ivUnFavorite The ImageView for the unfavorite icon.
     * @param ivFavorite The ImageView for the favorite icon.
     */
    fun FriendFavorite(friend: Friend, ivUnFavorite: ImageView, ivFavorite: ImageView) {
        if (friend.isFavorite) {
            // Display the unfavorite icon and hide the favorite icon
            ivUnFavorite.visibility = View.VISIBLE
            ivFavorite.visibility = View.INVISIBLE

            // Set up click listener for unfavorite icon
            ivUnFavorite.setOnClickListener {
                // Get the Firebase database instance
                val database = FirebaseDatabase.getInstance()

                // Create a FriendRepository instance with relevant information
                val friendRepo = FriendRepository(database, user.username, user.userId, owner)

                // Update the favorite status to false
                friend.isFavorite = false
                friendRepo.updateFavoriteStatus(friend)

                // Hide the unfavorite icon and show the favorite icon
                ivUnFavorite.visibility = View.INVISIBLE
                ivFavorite.visibility = View.VISIBLE

                // Set up click listener for favorite icon
                ivFavorite.setOnClickListener {
                    // Recursively call FriendFavorite to handle favorite icon click
                    FriendFavorite(friend, ivUnFavorite, ivFavorite)
                }
            }
        } else {
            // Display the favorite icon and hide the unfavorite icon
            ivUnFavorite.visibility = View.INVISIBLE
            ivFavorite.visibility = View.VISIBLE

            // Set up click listener for favorite icon
            ivFavorite.setOnClickListener {
                // Get the Firebase database instance
                val database = FirebaseDatabase.getInstance()

                // Create a FriendRepository instance with relevant information
                val friendRepo = FriendRepository(database, user.username, user.userId, owner)

                // Update the favorite status to true
                friend.isFavorite = true
                friendRepo.updateFavoriteStatus(friend)

                // Hide the favorite icon and show the unfavorite icon
                ivUnFavorite.visibility = View.VISIBLE
                ivFavorite.visibility = View.INVISIBLE

                // Set up click listener for unfavorite icon
                ivUnFavorite.setOnClickListener {
                    // Recursively call FriendFavorite to handle unfavorite icon click
                    FriendFavorite(friend, ivUnFavorite, ivFavorite)
                }
            }
        }
    }

    /**
     * Represents a ViewHolder for the items in the RecyclerView.
     *
     * @param itemView The item view for the ViewHolder.
     */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize the views in the item view
        val tvUsername: TextView = itemView.findViewById(R.id.tvFriendName)
        val ivRemoveFriend: ImageView = itemView.findViewById(R.id.ivRemoveFriend)
        val ivUnFavorite: ImageView = itemView.findViewById(R.id.ivUnFavorite)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
    }

}
