package com.example.cranny

import android.content.Intent
import android.media.Image
import android.opengl.Visibility
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
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class FriendSearchActivity : AppCompatActivity()
{

    private var friendList = mutableListOf<String>()
    private lateinit var rvFriends: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_search)

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
            // todo start add friend activity
        }

        val buttonFriendRequest: MaterialCardView = findViewById(R.id.mcvRequestButton)
        buttonFriendRequest.setOnClickListener {
            // todo start friend request activity
        }

        val database = FirebaseDatabase.getInstance()
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val profileRepo = ProfileRepository(database, currentUser?.uid!!)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            val user = User(userProfile.userId, userProfile.name, userProfile.username, userProfile.friendCount,
            userProfile.bookCount, userProfile.bio)
            rvFriends = findViewById(R.id.rvFriendSearch)
            getFriendList(user)

        })
        profileRepo.stopProfileListener() // free the listener to stop memory leaks


    }


    private fun getFriendList(user: User)
    {
        var friendList = mutableListOf<Friend>()
        val database = FirebaseDatabase.getInstance()
        val friendRepo = FriendRepository(database)
        friendRepo.fetchFriends()
        friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if(isFriendsReady)
            {
                val friendCount = friendRepo.FriendIds.size
                if(friendCount > 0)
                {
                    for(friend in friendRepo.FriendIds)
                    {
                        friendList.add(friend)
                    }
                    rvFriends.layoutManager = LinearLayoutManager(this)
                    rvFriends.setHasFixedSize(true)
                    rvFriends.adapter = SearchAdapter(friendList, user)
                }
                else
                {
                    rvFriends.visibility = View.INVISIBLE
                    val searchBar: MaterialCardView = findViewById(R.id.mcvSearchBox)
                    searchBar.visibility = View.INVISIBLE
                    Toast.makeText(this, "No friends added.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        friendRepo.stopFriendListener() // free the listener to stop memory leaks
    }

    override fun onResume()
    {
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
                    friend.startsWith(s.toString(), ignoreCase = true)
                }
                //adapter.updateData(filteredList)
            }
        })
    }
}

class SearchAdapter(private val usernames: MutableList<Friend>, private var user: User): RecyclerView.Adapter<SearchAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_friend_view,
        parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int
    {
        return usernames.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val username = usernames[position].username
        val usernameSize = username.length
        var formateUsername = "@${username}"
        for(i in 0..15 - usernameSize)
        {
            formateUsername += " "
        }
        holder.tvUsername.text = formateUsername
        holder.ivRemoveFriend.setOnClickListener {
            val id = usernames[position].id
            val removeFriend = Friend(id, username)
            val database = FirebaseDatabase.getInstance()
            val friendRepo = FriendRepository(database)
            friendRepo.removeFriend(removeFriend)
            holder.ivRemoveFriend.visibility = View.INVISIBLE
            usernames.removeAt(position)
            notifyItemRemoved(position)

            // Update the user's friend count
            user.friendCount--
            val profileRepo = ProfileRepository(database, user.userId)
            profileRepo.updateProfileData(user.username, user.name, user.userId, user.bio, user.friendCount, user.bookCount)
        }
    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val tvUsername: TextView = itemView.findViewById(R.id.tvFriendName)
        val ivRemoveFriend: ImageView = itemView.findViewById(R.id.ivRemoveFriend)
    }
}
