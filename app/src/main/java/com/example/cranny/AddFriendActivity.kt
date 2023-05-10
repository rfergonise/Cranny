package com.example.cranny

/**
The AddFriendActivity class is responsible for managing the functionality of adding friends in the Cranny app.
Users can search for other users, view their profiles, and send friend requests.
It retrieves user and friend data from the Firebase Realtime Database and updates the UI accordingly.
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AddFriendActivity : AppCompatActivity()
{

    var UserList: MutableList<Friend> = mutableListOf()
    var FriendList: MutableList<Friend> = mutableListOf()
    lateinit var user: User

    lateinit var noUserFound: MaterialCardView
    lateinit var userCard: MaterialCardView
    lateinit var tvUsername: TextView
    lateinit var ivAddFriend: ImageView
    lateinit var ivProfilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        // Initialize UI elements
        noUserFound = findViewById(R.id.mcvNoUserFound)
        userCard = findViewById(R.id.mcvUserBox)
        tvUsername = findViewById(R.id.tvFriendName)
        ivAddFriend = findViewById(R.id.ivRemoveFriend)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)

        // Set initial visibility of UI elements
        noUserFound.visibility = View.INVISIBLE
        userCard.visibility = View.INVISIBLE

        // Set up click listener for the main menu button
        val ivMainMenuButton: ImageView = findViewById(R.id.ivBackToMain)
        ivMainMenuButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        // Set up click listener for the friend button
        val ivFriendButton: ImageView = findViewById(R.id.ivFriendButton)
        ivFriendButton.setOnClickListener {
            val i = Intent(this, FriendSearchActivity::class.java)
            startActivity(i)
        }

        // Get the current user's profile data from the database
        val database = FirebaseDatabase.getInstance()
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
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
            getFriendList()
        })
        profileRepo.stopProfileListener() // Free the listener to stop memory leaks
    }


    /**
     * Retrieves the list of users from the server and populates the user interface accordingly.
     */
    fun getUserList() {
        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase
        val serverRepository = ServerRepository(database) // Creates an instance of the ServerRepository using the FirebaseDatabase instance

        serverRepository.isUserListReady.observe(this, Observer { isUserListReady ->
            if (isUserListReady) { // Checks if the user list is ready
                val userCount = serverRepository.Users.size // Retrieves the number of users in the repository

                if (userCount > 0) { // Checks if there are users in the repository
                    for (publicUser in serverRepository.Users) { // Iterates over each public user in the repository
                        if (publicUser.username != user.username && !FriendList.contains(publicUser)) { // Checks if the public user is not the current user and not already a friend
                            UserList.add(publicUser) // Adds the public user to the UserList
                        }
                    }

                    searchForUser() // Calls a function to search for users (not provided in the code)
                    serverRepository.stopUserListener() // Stops listening for updates on the user list
                } else { // Executes when the user list is empty
                    noUserFound.visibility = View.VISIBLE // Sets the visibility of the "noUserFound" UI element to visible
                    val searchBar: MaterialCardView = findViewById(R.id.mcvSearchBox)
                    searchBar.visibility = View.INVISIBLE // Sets the visibility of the search bar UI element to invisible
                    userCard.visibility = View.INVISIBLE // Sets the visibility of the user card UI element to invisible
                }
            }
        })
    }

    /**
     * Searches for a user based on the text entered in the search bar and performs relevant actions.
     */
    fun searchForUser() {
        val searchEditText = findViewById<EditText>(R.id.etSearchBar) // Retrieves the search EditText UI element
        val buttonFind: MaterialCardView = findViewById(R.id.mcvFindButton) // Retrieves the find button UI element
        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase

        buttonFind.setOnClickListener {
            val filteredList = UserList.filter { findUser -> findUser.username.equals(searchEditText.text.toString(), ignoreCase = true) } // Filters the UserList based on the entered username

            if (user?.username != null) { // Checks if the current user's username is not null
                if (filteredList.size == 0) { // Checks if no users were found in the filtered list
                    noUserFound.visibility = View.VISIBLE // Sets the visibility of the "noUserFound" UI element to visible
                    userCard.visibility = View.INVISIBLE // Sets the visibility of the user card UI element to invisible
                } else { // Executes when users were found in the filtered list
                    tvUsername.text = filteredList[0].username // Sets the username in the UI to the username of the first user in the filtered list

                    ivAddFriend.setOnClickListener {
                        UserList.removeIf { userToRemove ->
                            userToRemove == filteredList[0]
                        }
                        userCard.visibility = View.INVISIBLE // Sets the visibility of the user card UI element to invisible
                        noUserFound.visibility = View.INVISIBLE // Sets the visibility of the "noUserFound" UI element to invisible

                        val requestRepo = FriendRequestRepository(database, filteredList[0].id) // Creates an instance of the FriendRequestRepository
                        requestRepo.addFriendRequest(Friend(user.userId, user.username, false)) // Adds a friend request to the repository
                        Toast.makeText(this, "Friend Request Sent!", Toast.LENGTH_SHORT).show() // Displays a toast message indicating that a friend request was sent
                    }

                    val profilePictureRepository = ProfilePictureRepository(database, filteredList[0].id) // Creates an instance of the ProfilePictureRepository
                    profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture) // Loads the user's profile picture into the ImageView UI element

                    noUserFound.visibility = View.INVISIBLE // Sets the visibility of the "noUserFound" UI element to invisible
                    userCard.visibility = View.VISIBLE // Sets the visibility of the user card UI element to visible
                }
            }
        }
    }

    /**
     * Retrieves the friend list for the current user from the database and performs necessary actions.
     * This function fetches the friend list from the FriendRepository and populates the FriendList accordingly.
     * It also calls the getUserList() function to update the user list after retrieving the friend list.
     * The function stops the friend listener to prevent memory leaks.
     */
    private fun getFriendList() {
        val database = FirebaseDatabase.getInstance() // Retrieves the instance of the FirebaseDatabase
        val friendRepo = FriendRepository(database, user.username, user.userId, this) // Creates an instance of the FriendRepository

        friendRepo.fetchFriends() // Fetches the friend list from the repository

        friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if (isFriendsReady) { // Checks if the friend list is ready
                val friendCount = friendRepo.FriendIds.size // Retrieves the number of friends in the repository

                if (friendCount > 0) { // Checks if there are friends in the repository
                    for (friend in friendRepo.FriendIds) { // Iterates over each friend ID in the repository
                        FriendList.add(friend) // Adds the friend ID to the FriendList
                    }
                }

                getUserList() // Calls the getUserList() function to update the user list
            }
        })

        friendRepo.stopFriendListener() // Stops listening for updates on the friend list to prevent memory leaks
    }
}