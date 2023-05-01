package com.example.cranny

import android.content.Context
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
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text


class FriendSearchActivity : AppCompatActivity()
{

    private var friendList = mutableListOf<Friend>()
    private lateinit var rvFriends: RecyclerView
    lateinit var user: User
    lateinit var tvNoFriend: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_search)

        tvNoFriend = findViewById(R.id.tvNoFriendFound)

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

        val database = FirebaseDatabase.getInstance()
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val profileRepo = ProfileRepository(database, currentUser?.uid!!)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            user = User(userProfile.userId, userProfile.name, userProfile.username, userProfile.friendCount,
            userProfile.bookCount, userProfile.bio)
            rvFriends = findViewById(R.id.rvFriendSearch)
            getFriendList(user)

        })
        profileRepo.stopProfileListener() // free the listener to stop memory leaks


    }


    private fun getFriendList(user: User)
    {
        val database = FirebaseDatabase.getInstance()
        val friendRepo = FriendRepository(database, user.username, user.userId, this)
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
                    rvFriends.adapter = SearchAdapter(this@FriendSearchActivity,this@FriendSearchActivity,friendList, user, tvNoFriend)
                }
                else
                {
                    tvNoFriend.visibility = View.VISIBLE
                    rvFriends.visibility = View.INVISIBLE
                    val searchBar: MaterialCardView = findViewById(R.id.mcvSearchBox)
                    searchBar.visibility = View.INVISIBLE
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
                    friend.username.startsWith(s.toString(), ignoreCase = true)
                }
                if(user?.username != null)
                {
                    if(filteredList.size == 0) tvNoFriend.visibility = View.VISIBLE
                    else tvNoFriend.visibility = View.INVISIBLE
                    rvFriends.adapter = SearchAdapter(this@FriendSearchActivity,this@FriendSearchActivity, filteredList.toMutableList(), user, tvNoFriend)
                }
            }
        })
    }
}

class SearchAdapter(private val owner: LifecycleOwner, val context: Context, private val usernames: MutableList<Friend>, private var user: User, private var tvNoFriend: TextView): RecyclerView.Adapter<SearchAdapter.MyViewHolder>()
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
        holder.tvUsername.text = "@${username}"
        val id = usernames[position].id

        FriendFavorite(usernames[position], holder.ivUnFavorite, holder.ivFavorite)

        holder.ivRemoveFriend.setOnClickListener {
            val removeFriend = Friend(id, username, false)
            val database = FirebaseDatabase.getInstance()
            val friendRepo = FriendRepository(database, user.username, user.userId, owner)
            friendRepo.removeFriend(removeFriend)
            holder.ivRemoveFriend.visibility = View.INVISIBLE
            usernames.removeAt(position)
            notifyItemRemoved(position)
            if(usernames.size == 0) tvNoFriend.visibility = View.VISIBLE

            // Update the user's friend count
            user.friendCount--
            val profileRepo = ProfileRepository(database, user.userId)
            profileRepo.updateProfileData(user.username, user.name, user.userId, user.bio, user.friendCount, user.bookCount)
        }
        holder.tvUsername.setOnClickListener{
            val friendIntent = Intent(context, FriendActivity::class.java)
            friendIntent.putExtra("friendId", id)
            context.startActivity(friendIntent)
        }
    }

    fun FriendFavorite(friend:Friend, ivUnFavorite: ImageView, ivFavorite: ImageView)
    {
        if(friend.isFavorite)
        {
            ivUnFavorite.visibility = View.VISIBLE
            ivFavorite.visibility = View.INVISIBLE
            ivUnFavorite.setOnClickListener {
                // todo set the friend to not a favorite
                val database = FirebaseDatabase.getInstance()
                val FriendRepo = FriendRepository(database, user.username, user.userId, owner)
                friend.isFavorite = false
                FriendRepo.updateFavoriteStatus(friend)
                ivUnFavorite.visibility = View.INVISIBLE
                ivFavorite.visibility = View.VISIBLE
                ivFavorite.setOnClickListener {
                    FriendFavorite(friend, ivUnFavorite, ivFavorite)
                }
            }

        }
        else
        {
            ivUnFavorite.visibility = View.INVISIBLE
            ivFavorite.visibility = View.VISIBLE
            ivFavorite.setOnClickListener {
                // todo set the friend to a favorite
                val database = FirebaseDatabase.getInstance()
                val FriendRepo = FriendRepository(database, user.username, user.userId, owner)
                friend.isFavorite = true
                FriendRepo.updateFavoriteStatus(friend)
                ivUnFavorite.visibility = View.VISIBLE
                ivFavorite.visibility = View.INVISIBLE
                ivUnFavorite.setOnClickListener {
                    FriendFavorite(friend, ivUnFavorite, ivFavorite)
                }
            }
        }
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val tvUsername: TextView = itemView.findViewById(R.id.tvFriendName)
        val ivRemoveFriend: ImageView = itemView.findViewById(R.id.ivRemoveFriend)
        val ivUnFavorite: ImageView = itemView.findViewById(R.id.ivUnFavorite)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
    }
}
