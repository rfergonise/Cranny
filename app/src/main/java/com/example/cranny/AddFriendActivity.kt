package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

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

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        noUserFound = findViewById(R.id.mcvNoUserFound)
        userCard = findViewById(R.id.mcvUserBox)
        tvUsername = findViewById(R.id.tvFriendName)
        ivAddFriend = findViewById(R.id.ivRemoveFriend)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)

        noUserFound.visibility = View.INVISIBLE
        userCard.visibility = View.INVISIBLE

        val ivMainMenuButton: ImageView = findViewById(R.id.ivBackToMain)
        ivMainMenuButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        val ivFriendButton: ImageView = findViewById(R.id.ivFriendButton)
        ivFriendButton.setOnClickListener {
            val i = Intent(this, FriendSearchActivity::class.java)
            startActivity(i)
        }

        val database = FirebaseDatabase.getInstance()
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val profileRepo = ProfileRepository(database, currentUser?.uid!!)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            user = User(userProfile.userId, userProfile.name, userProfile.username, userProfile.friendCount,userProfile.bookCount, userProfile.bio)
            getFriendList()
        })
        profileRepo.stopProfileListener() // free the listener to stop memory leaks

    }

    fun getUserList()
    {
        val database = FirebaseDatabase.getInstance()
        val ServerRepository = ServerRepository(database)
        ServerRepository.isUserListReady.observe(this, Observer { isUserListReady ->
            if(isUserListReady)
            {
                val userCount = ServerRepository.Users.size
                if(userCount > 0)
                {
                    for(publicUser in ServerRepository.Users)
                    {
                        if(publicUser.username != user.username && !FriendList.contains(publicUser)) // user cant add itself and a friend already added
                            UserList.add(publicUser)
                    }
                    searchForUser()
                    ServerRepository.stopUserListener()
                }
                else
                {
                    noUserFound.visibility = View.VISIBLE
                    val searchBar: MaterialCardView = findViewById(R.id.mcvSearchBox)
                    searchBar.visibility = View.INVISIBLE
                    userCard.visibility = View.INVISIBLE
                }
            }
        })
    }

    fun searchForUser()
    {
        val searchEditText = findViewById<EditText>(R.id.etSearchBar)
        val buttonFind: MaterialCardView = findViewById(R.id.mcvFindButton)
        val database = FirebaseDatabase.getInstance()
        buttonFind.setOnClickListener {
            val filteredList = UserList.filter { findUser -> findUser.username.equals(searchEditText.text.toString(), ignoreCase = true)}
            if(user?.username != null)
            {
                if(filteredList.size == 0)
                {
                    noUserFound.visibility = View.VISIBLE
                    userCard.visibility = View.INVISIBLE
                }
                else
                {
                    tvUsername.text = filteredList[0].username
                    ivAddFriend.setOnClickListener {
                        UserList.removeIf { userToRemove ->
                            userToRemove == filteredList[0]
                        }
                        userCard.visibility = View.INVISIBLE
                        noUserFound.visibility = View.INVISIBLE

                        val requestRepo = FriendRequestRepository(database, filteredList[0].id)
                        requestRepo.addFriendRequest(Friend(user.userId, user.username, false))
                        Toast.makeText(this, "Friend Request Sent!", Toast.LENGTH_SHORT).show()
                    }
                    val profilePictureRepository = ProfilePictureRepository(database, filteredList[0].id)
                    profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture)
                    noUserFound.visibility = View.INVISIBLE
                    userCard.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getFriendList()
    {
        val database = FirebaseDatabase.getInstance()
        val friendRepo = FriendRepository(database, user.username,user.userId, this)
        friendRepo.fetchFriends()
        friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if(isFriendsReady)
            {
                val friendCount = friendRepo.FriendIds.size
                if(friendCount > 0)
                {
                    for(friend in friendRepo.FriendIds)
                    {
                        FriendList.add(friend)
                    }
                }
                getUserList()
            }
        })
        friendRepo.stopFriendListener() // free the listener to stop memory leaks
    }
}