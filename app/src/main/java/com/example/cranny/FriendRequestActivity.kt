package com.example.cranny

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
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class FriendRequestActivity : AppCompatActivity()
{
    private var requestList = mutableListOf<Friend>()
    private lateinit var rvRequests: RecyclerView
    lateinit var user: User
    lateinit var tvNoFriendRequest: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

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
            user = User(userProfile.userId, userProfile.name, userProfile.username, userProfile.friendCount,
                userProfile.bookCount, userProfile.bio)
            tvNoFriendRequest = findViewById(R.id.tvNoRequestFound)
            rvRequests = findViewById(R.id.rvFriendRequest)
            getFriendRequests(user)

        })
        profileRepo.stopProfileListener()

    }

    fun getFriendRequests(user: User)
    {

        val database = FirebaseDatabase.getInstance()
        val requestRepo = FriendRequestRepository(database, user.userId)
        requestRepo.fetchFriendRequests()
        requestRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if(isFriendsReady)
            {
                val friendCount = requestRepo.RequestedUsers.size
                if(friendCount > 0)
                {
                    for(friend in requestRepo.RequestedUsers)
                    {
                        requestList.add(friend)
                    }
                    rvRequests.layoutManager = LinearLayoutManager(this)
                    rvRequests.setHasFixedSize(true)
                    rvRequests.adapter = FriendRequestAdapter(this, this, requestList, user, tvNoFriendRequest)
                }
                else
                {
                    tvNoFriendRequest.visibility = View.VISIBLE
                    rvRequests.visibility = View.INVISIBLE
                }
            }
        })
        requestRepo.stopFriendRequestListener()
    }


}

class FriendRequestAdapter(private val owner: LifecycleOwner, val context: Context, private val usernames: MutableList<Friend>, private var user: User, private var tvNoFriend: TextView): RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_request_view,
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
        holder.tvUsername.text = "@${username}"
        val id = usernames[position].id
        val database = FirebaseDatabase.getInstance()
        val profilePictureRepository = ProfilePictureRepository(database, id)
        profilePictureRepository.loadProfilePictureIntoImageView(holder.ivProfilePicture)

        holder.ivAcceptFriend.setOnClickListener {
            val friendRepo = FriendRepository(database, user.username, user.userId, owner)
            friendRepo.addFriend(Friend(id, username))
            // remove the friend from the request list and data
            val requestRepo = FriendRequestRepository(database, user.userId)
            requestRepo.removeFriendRequest(Friend(id, username))
            usernames.removeAt(position)
            notifyItemRemoved(position)
            if(usernames.size == 0) tvNoFriend.visibility = View.VISIBLE

            Toast.makeText(context, "Friend Request Accepted", Toast.LENGTH_SHORT).show()
        }

        holder.ivDeclineFriend.setOnClickListener {
            // remove the friend from the request list and data
            val requestRepo = FriendRequestRepository(database, user.userId)
            requestRepo.removeFriendRequest(Friend(id, username))
            usernames.removeAt(position)
            notifyItemRemoved(position)
            if(usernames.size == 0) tvNoFriend.visibility = View.VISIBLE

            Toast.makeText(context, "Friend Request Declined", Toast.LENGTH_SHORT).show()
        }

    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val ivAcceptFriend: ImageView = itemView.findViewById(R.id.ivAcceptFriend)
        val ivDeclineFriend: ImageView = itemView.findViewById(R.id.ivDeclineFriend)
        val ivProfilePicture: ImageView = itemView.findViewById(R.id.ivProfilePicture)
    }
}