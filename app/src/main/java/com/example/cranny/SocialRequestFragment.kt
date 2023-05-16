package com.example.cranny

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.CountDownLatch

class SocialRequestFragment : Fragment() {

    // Fragment View
    lateinit var fragmentView: View

    // UI
    lateinit var etSearchBar: EditText
    lateinit var rvFeed: RecyclerView
    lateinit var mcvSearchButton: MaterialCardView
    lateinit var mcvClearButton: MaterialCardView
    lateinit var tvNoFriendRequest: TextView
    lateinit var SearchPage: ConstraintLayout

    // List of users to be displayed in the feed
    lateinit var mlUsersToDisplay: MutableList<Friend>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_social_request, container, false)

        // setup UI
        etSearchBar = fragmentView.findViewById(R.id.etSearchBar)
        mcvSearchButton = fragmentView.findViewById(R.id.mcvSearchButton)
        mcvClearButton = fragmentView.findViewById(R.id.mcvClearButton)
        rvFeed = fragmentView.findViewById(R.id.rvUserSearchFeed)
        tvNoFriendRequest = fragmentView.findViewById(R.id.tvFeedIsEmpty)
        SearchPage = fragmentView.findViewById(R.id.SearchPage)

        SearchPage.visibility = View.INVISIBLE
        // Initial List
        mlUsersToDisplay = mutableListOf<Friend>()

        // setup lists
        val database = FirebaseDatabase.getInstance()
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = currentUser!!.uid
        StartFetching(database, userId)

        return fragmentView
    }

    private fun isStringMatchingUsername(username: String, friends: MutableList<Friend>): Boolean {
        for (friend in friends) {
            if (friend.username == username) {
                return true
            }
        }
        return false
    }
    private fun StartFetching(database: FirebaseDatabase, userId: String)
    {
        // fetch profile data
        val profileRepo = ProfileRepository(database, userId)
        val owner: LifecycleOwner = requireActivity()
        val context = context
        //Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        profileRepo.profileData.observe(owner, Observer { userProfile ->
            val username: String = userProfile.username
            //Toast.makeText(context, "Loaded Profile", Toast.LENGTH_SHORT).show()
            // fetch friend data
            val friendRepo = FriendRepository(database, username, userId, this)
            friendRepo.fetchFriends()
            friendRepo.isFriendsReady.observe(owner, Observer { isFriendsReady ->
                if(isFriendsReady)
                {
                    //Toast.makeText(context, "Loaded Friends", Toast.LENGTH_SHORT).show()
                    // fetch server data
                    val serverRepository = ServerRepository(database)
                    serverRepository.isUserListReady.observe(owner, Observer { isUsersReady->
                        if(isUsersReady)
                        {
                            //Toast.makeText(context, "Loaded Usernames", Toast.LENGTH_SHORT).show()
                            // fetch friend requests
                            val friendRequestRepository = FriendRequestRepository(database, userId)
                            friendRequestRepository.fetchFriendRequests()
                            friendRequestRepository.isFriendsReady.observe(owner, Observer{ isFriendRequestsReady ->
                                if(isFriendRequestsReady)
                                {
                                    //Toast.makeText(context, "Loaded Friend Requests", Toast.LENGTH_SHORT).show()

                                    val requestedUsersRepository = RequestedRepository(database, userId)
                                    requestedUsersRepository.fetchRequestedUsers()
                                    friendRequestRepository.isFriendsReady.observe(owner, Observer { isRequestedUsersReady->
                                        if(isRequestedUsersReady)
                                        {
                                            var mlAvailableUsersToAdd = mutableListOf<Friend>()
                                            for (publicUser in serverRepository.Users) { // Iterates over each public user in the repository
                                                val isFriend = isStringMatchingUsername(publicUser.username, friendRepo.FriendIds)
                                                val isAlreadyRequested = isStringMatchingUsername(publicUser.username, requestedUsersRepository.RequestedUsers)
                                                val isRequestingUs = isStringMatchingUsername(publicUser.username, friendRequestRepository.RequestedUsers)
                                                // We can only send friend request to users who ares 1) The same user 2) Already friended 3) Already sent a request to 4) They sent us a request
                                                if (publicUser.username != username && !isFriend && !isAlreadyRequested && !isRequestingUs) { // Checks if the public user is not the current user and not already a friend
                                                    mlAvailableUsersToAdd.add(Friend(publicUser.id, publicUser.username, false)) // Adds the public user to the UserList
                                                    // true means it's a friend request
                                                }
                                            }
                                            if(friendRequestRepository.RequestedUsers.size > 0)
                                            {
                                                // there is friend requests
                                                tvNoFriendRequest.visibility = View.INVISIBLE
                                                rvFeed.visibility = View.VISIBLE
                                                // add each requesting user to list of users to display
                                                for(requestingUser in friendRequestRepository.RequestedUsers)
                                                {
                                                    mlUsersToDisplay.add(Friend(requestingUser.id, requestingUser.username, true))
                                                    // false means it's a searched user
                                                }
                                                rvFeed.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // Sets the layout manager for the RecyclerView
                                                rvFeed.setHasFixedSize(true) // Optimizes performance by indicating that the item size in the RecyclerView is fixed
                                                rvFeed.adapter = RequestFragmentAdapter(owner, context!!, mlUsersToDisplay, userProfile, tvNoFriendRequest, rvFeed, database)
                                            }
                                            else
                                            {
                                                // there is no friend requests
                                                tvNoFriendRequest.visibility = View.VISIBLE
                                                rvFeed.visibility = View.INVISIBLE
                                            }
                                            AllowClear()
                                            AllowSearch(userProfile, owner, context!!, mlAvailableUsersToAdd, database)
                                            SearchPage.visibility = View.VISIBLE
                                        }
                                    })
                                }
                            })
                        }
                    })
                    serverRepository.stopUserListener()
                }
            })
            friendRepo.stopFriendListener()
        })
        profileRepo.stopProfileListener()
    }

    private fun AllowSearch(user: User, owner: LifecycleOwner, context: Context, mlAvailableUsersToAdd: MutableList<Friend>, database: FirebaseDatabase)
    {
        mcvSearchButton.setOnClickListener {
            val strUserTheySearchedFor = etSearchBar.text.toString()
            if (etSearchBar.text.isNotBlank()) {
                val filteredList = mlAvailableUsersToAdd.filter { findUser -> findUser.username.equals(strUserTheySearchedFor, ignoreCase = true) }
                if (filteredList.isNotEmpty()) {
                    val showSearchUserPopUp = SocialSearchedUserFragment.newInstance(filteredList[0], user)
                    showSearchUserPopUp.setOnDialogDismissedListener(object : SocialSearchedUserFragment.OnDialogDismissedListener {
                        override fun onDismiss(isFriendRequestSent: Boolean) {
                            if (isFriendRequestSent)
                            {
                                // remove the user from the list of users to add
                                mlAvailableUsersToAdd.remove(filteredList[0])
                            }
                            mcvSearchButton.setOnClickListener(null)
                            AllowSearch(user, owner, context, mlAvailableUsersToAdd, database)
                        }
                    })
                    showSearchUserPopUp.show(requireActivity().supportFragmentManager, "showPopUp")
                } else {
                    Toast.makeText(context, "@${strUserTheySearchedFor} was not found.", Toast.LENGTH_SHORT).show()
                    mcvSearchButton.setOnClickListener(null)
                    AllowSearch(user, owner, context, mlAvailableUsersToAdd, database)
                }
            } else {
                Toast.makeText(context, "Search bar is empty.", Toast.LENGTH_SHORT).show()
                mcvSearchButton.setOnClickListener(null)
                AllowSearch(user, owner, context, mlAvailableUsersToAdd, database)
            }
        }
    }



    private fun AllowClear()
    {
        mcvClearButton.setOnClickListener {
            etSearchBar.text.clear()
        }
    }

}
class RequestFragmentAdapter(private val owner: LifecycleOwner, val context: Context, private val mlUsersToDisplay: MutableList<Friend>,
                             private var user: User, private var tvNoFriend: TextView, private var rvRecycleView: RecyclerView, private var database: FirebaseDatabase): RecyclerView.Adapter<RequestFragmentAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_request_fragment_layout, parent, false)

        // Create and return a new ViewHolder with the inflated layout
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return mlUsersToDisplay.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Get the username and ID of the current item
        val username = mlUsersToDisplay[position].username
        val id = mlUsersToDisplay[position].id
        val isFriendRequest = mlUsersToDisplay[position].isFavorite

        holder.tvUsername.text = "@${username}"
        val profilePictureRepository = ProfilePictureRepository(database, id)
        profilePictureRepository.loadProfilePictureIntoImageView(holder.ivProfilePicture)

        if(isFriendRequest) // we got a friend request from the user
        {
            holder.mcvAcceptButton.setOnClickListener{
                if(holder.mcvDeclineButton.hasOnClickListeners()) holder.mcvDeclineButton.setOnClickListener(null)
                holder.mcvDeclineButton.visibility = View.INVISIBLE
                RequestedFriendsFate(true, Friend(id, username, false), user, mlUsersToDisplay, position, holder.mcvCard)
            }
            holder.mcvDeclineButton.setOnClickListener{
                if(holder.mcvAcceptButton.hasOnClickListeners()) holder.mcvAcceptButton.setOnClickListener(null)
                holder.mcvAcceptButton.visibility = View.INVISIBLE
                RequestedFriendsFate(false, Friend(id, username, false), user, mlUsersToDisplay, position, holder.mcvCard)
            }
        }
    }



    fun RequestedFriendsFate(isAdd: Boolean, friend: Friend, user: User, mlUsersToDisplay: MutableList<Friend>, position: Int, mcvCard: MaterialCardView)
    {
        val database = FirebaseDatabase.getInstance()
        var toastMessage: String = ""
        if(isAdd)
        {
            val friendRepo = FriendRepository(database, user.username, user.userId, owner)
            friendRepo.addFriend(friend)
            toastMessage = "@${friend.username} was successfully added!"
        }
        else toastMessage = "@${friend.username} was declined."

        val requestRepo = FriendRequestRepository(database, user.userId)
        requestRepo.removeFriendRequest(friend)
        val requestedRepo = RequestedRepository(database, friend.id)
        requestedRepo.removeRequestedUser(Friend(user.userId, user.username, false))
        mcvCard.visibility = View.INVISIBLE
        mlUsersToDisplay.removeAt(position)
        notifyItemRemoved(position)
        if(mlUsersToDisplay.size == 0)
        {
            tvNoFriend.visibility = View.VISIBLE
            rvRecycleView.visibility = View.INVISIBLE
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize the views in the item view
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val ivProfilePicture: ImageView = itemView.findViewById(R.id.ivProfilePicture)
        val mcvAcceptButton: MaterialCardView = itemView.findViewById(R.id.mcvAcceptButton)
        val mcvDeclineButton: MaterialCardView = itemView.findViewById(R.id.mcvDeclineButton)
        val mcvCard: MaterialCardView = itemView.findViewById(R.id.mcvFriendBox)
    }

}

