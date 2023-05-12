package com.example.cranny

/**
FriendRequestActivity is an activity in the Cranny app that allows users to manage friend requests.
It retrieves and displays friend requests for the current user.
Users can accept or decline friend requests from other users.
The activity uses RecyclerView and adapters to display the friend requests in a list format.
It also communicates with Firebase for fetching and updating friend requests and user profiles.
 */

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class FriendRequestActivity : AppCompatActivity()
{
    private var requestList = mutableListOf<Friend>()
    private lateinit var rvRequests: RecyclerView
    lateinit var user: User
    lateinit var tvNoFriendRequest: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request) // Sets the content view for the activity to the corresponding layout

        val ivMainMenuButton: ImageView = findViewById(R.id.ivBackToMain)
        ivMainMenuButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i) // Navigates to the MainActivity when the main menu button is clicked
        }

        val ivFriendButton: ImageView = findViewById(R.id.ivFriendButton)
        ivFriendButton.setOnClickListener {
            val i = Intent(this, FriendSearchActivity::class.java)
            startActivity(i) // Navigates to the FriendSearchActivity when the friend button is clicked
        }

        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser // Retrieves the current user from FirebaseAuth

        val profileRepo = ProfileRepository(database, currentUser?.uid!!) // Creates an instance of the ProfileRepository

        profileRepo.profileData.observe(this, Observer { userProfile ->
            user = User(
                userProfile.userId,
                userProfile.name,
                userProfile.username,
                userProfile.friendCount,
                userProfile.bookCount,
                userProfile.bio
            ) // Creates a User object from the retrieved profile data

            tvNoFriendRequest = findViewById(R.id.tvNoRequestFound) // Retrieves the "tvNoRequestFound" TextView UI element
            rvRequests = findViewById(R.id.rvFriendRequest) // Retrieves the "rvFriendRequest" RecyclerView UI element

            getFriendRequests(user) // Calls the getFriendRequests() function to retrieve and populate the friend requests

        })

        profileRepo.stopProfileListener() // Stops listening for updates on the profile data to prevent memory leaks
    }

    /**
     * Retrieves and populates the friend requests for the specified user.
     * This function fetches the friend requests from the FriendRequestRepository and populates the requestList accordingly.
     * It also sets up the RecyclerView to display the friend requests using the FriendRequestAdapter.
     * The function handles the visibility of UI elements when there are no friend requests.
     * It stops the friend request listener to prevent memory leaks.
     *
     * @param user The user for whom the friend requests are retrieved.
     */
    fun getFriendRequests(user: User) {
        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase
        val requestRepo = FriendRequestRepository(database, user.userId) // Creates an instance of the FriendRequestRepository

        requestRepo.fetchFriendRequests() // Fetches the friend requests from the repository

        requestRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if (isFriendsReady) { // Checks if the friend requests are ready
                val friendCount = requestRepo.RequestedUsers.size // Retrieves the number of friend requests

                if (friendCount > 0) { // Checks if there are friend requests
                    for (friend in requestRepo.RequestedUsers) { // Iterates over each friend request in the repository
                        requestList.add(friend) // Adds the friend request to the requestList
                    }

                    rvRequests.layoutManager = LinearLayoutManager(this) // Sets up the layout manager for the RecyclerView
                    rvRequests.setHasFixedSize(true) // Sets the RecyclerView to have a fixed size
                    rvRequests.adapter = FriendRequestAdapter(this, this, requestList, user, tvNoFriendRequest) // Sets the adapter for the RecyclerView

                } else { // Executes when there are no friend requests
                    tvNoFriendRequest.visibility = View.VISIBLE // Sets the visibility of the "tvNoFriendRequest" TextView UI element to visible
                    rvRequests.visibility = View.INVISIBLE // Sets the visibility of the RecyclerView UI element to invisible
                }
            }
        })

        requestRepo.stopFriendRequestListener() // Stops listening for updates on the friend requests to prevent memory leaks
    }


}

class FriendRequestAdapter(private val owner: LifecycleOwner, val context: Context, private val usernames: MutableList<Friend>, private var user: User, private var tvNoFriend: TextView): RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder>()
{

    /**
     * Creates and returns a ViewHolder object for the RecyclerView.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the item.
     * @return The created ViewHolder object.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_request_view, parent, false)
        return MyViewHolder(itemView)
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The item count.
     */
    override fun getItemCount(): Int {
        return usernames.size
    }

    /**
     * Binds the data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val username = usernames[position].username
        holder.tvUsername.text = "@${username}" // Sets the text of "tvUsername" TextView to the username

        val id = usernames[position].id
        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase

        val profilePictureRepository = ProfilePictureRepository(database, id) // Creates an instance of the ProfilePictureRepository
        profilePictureRepository.loadProfilePictureIntoImageView(holder.ivProfilePicture) // Loads the profile picture into the "ivProfilePicture" ImageView

        holder.ivAcceptFriend.setOnClickListener {
            val friendRepo = FriendRepository(database, user.username, user.userId, owner)
            friendRepo.addFriend(Friend(id, username, false)) // Adds the friend to the FriendRepository

            val requestRepo = FriendRequestRepository(database, user.userId)
            requestRepo.removeFriendRequest(Friend(id, username, false)) // Removes the friend request from the FriendRequestRepository

            usernames.removeAt(position) // Removes the friend request from the usernames list
            notifyItemRemoved(position) // Notifies the adapter about the removed item at the specified position

            if (usernames.size == 0) {
                tvNoFriend.visibility = View.VISIBLE // Sets the visibility of the "tvNoFriend" TextView to visible when there are no friend requests
            }

            Toast.makeText(context, "Friend Request Accepted", Toast.LENGTH_SHORT).show() // Shows a toast indicating the friend request was accepted
        }

        holder.ivDeclineFriend.setOnClickListener {
            val requestRepo = FriendRequestRepository(database, user.userId)
            requestRepo.removeFriendRequest(Friend(id, username, false)) // Removes the friend request from the FriendRequestRepository

            usernames.removeAt(position) // Removes the friend request from the usernames list
            notifyItemRemoved(position) // Notifies the adapter about the removed item at the specified position

            if (usernames.size == 0) {
                tvNoFriend.visibility = View.VISIBLE // Sets the visibility of the "tvNoFriend" TextView to visible when there are no friend requests
            }

            Toast.makeText(context, "Friend Request Declined", Toast.LENGTH_SHORT).show() // Shows a toast indicating the friend request was declined
        }
    }

    /**
     * ViewHolder class that represents an item view in the RecyclerView.
     *
     * @param itemView The item view.
     */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername) // Reference to the "tvUsername" TextView
        val ivAcceptFriend: ImageView = itemView.findViewById(R.id.ivAcceptFriend) // Reference to the "ivAcceptFriend" ImageView
        val ivDeclineFriend: ImageView = itemView.findViewById(R.id.ivDeclineFriend) // Reference to the "ivDeclineFriend" ImageView
        val ivProfilePicture: ImageView = itemView.findViewById(R.id.ivProfilePicture) // Reference to the "ivProfile
    }
}