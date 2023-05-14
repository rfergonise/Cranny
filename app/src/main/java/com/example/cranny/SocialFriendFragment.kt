package com.example.cranny

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.Observer


class SocialFriendFragment : Fragment() {

    private lateinit var tvNoSocialFeed: TextView
    private lateinit var mcvFeedBorder: MaterialCardView

    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_social_friend, container, false)

        tvNoSocialFeed = fragmentView.findViewById(R.id.tvNoSocialFeed)
        mcvFeedBorder = fragmentView.findViewById(R.id.mcvBorder)

        getFriendsRecentlyReadBooks()

        return fragmentView
    }

    /**
     * Retrieves the recently read books from the user's friends and sets up the social feed.
     * Fetches recent data from the database, sorts it by last read time, and sets up the social feed UI.
     */
    private fun getFriendsRecentlyReadBooks() {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().uid!! // Get the current user's ID
        val profileRepository = ProfileRepository(database, userId) // Create a profile repository for the current user

        // Observe the profile data changes
        profileRepository.profileData.observe(requireActivity(), Observer { userProfile ->
            val username: String = userProfile.username // Get the current user's username
            val recentRepository = RecentRepository(database, username, mutableListOf())
            recentRepository.fetchRecentData()
            recentRepository.isRecentDataReady.observe(requireActivity(), Observer { isRecentDataReady ->
                if (isRecentDataReady) {
                    val sortedFeeds: MutableList<SocialFeed> = recentRepository.SocialFeeds.sortedByDescending { it.lastReadTime }.toMutableList()
                    setUpSocialFeed(sortedFeeds)
                }
            })
        })


        profileRepository.stopProfileListener() // Stop listening to profile data changes
    }

    /**
     * Sets up the social feed UI based on the sorted social feeds.
     *
     * @param sortedFeeds The list of sorted social feeds.
     */
    private fun setUpSocialFeed(sortedFeeds: MutableList<SocialFeed>) {
        val rvSocial: RecyclerView = fragmentView.findViewById(R.id.rvSocial)

        // Check if friendSocialFeed is not empty before setting the adapter
        if (sortedFeeds.isNotEmpty()) {
            tvNoSocialFeed.visibility = android.view.View.INVISIBLE // Hide the "No Social Feed" message
            rvSocial.visibility = android.view.View.VISIBLE // Show the social feed RecyclerView

            val context = requireContext()
            // Set up the adapter
            val adapter = SocialFeedRecyclerViewAdapter(requireActivity() as AppCompatActivity, context, sortedFeeds)
            rvSocial.layoutManager = LinearLayoutManager(context)
            rvSocial.adapter = adapter
            adapter.notifyDataSetChanged() // Notify the adapter that the data set has changed
        } else {
            tvNoSocialFeed.visibility = android.view.View.VISIBLE // Show the "No Social Feed" message
            rvSocial.visibility = android.view.View.INVISIBLE // Hide the social feed RecyclerView
        }
    }


}