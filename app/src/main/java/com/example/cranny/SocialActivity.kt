package com.example.cranny

/**
SocialActivity is an activity class in the Android application that displays a social feed.
It retrieves the recently read books from the user's friends and sets up the social feed UI.
The social feed data is fetched from a database, sorted by last read time, and displayed in a RecyclerView.
The activity also provides navigation options to go back to the main screen or view the user's profile.
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SocialActivity : AppCompatActivity() {

    // UI elements needed
    private lateinit var ivBackToMain: ImageView
    private lateinit var ivShowProfile: ImageView
    private lateinit var tvNoSocialFeed: TextView
    private lateinit var mcvFeedBorder: MaterialCardView

    // Used to store what will displayed in the social feed
    private val friendSocialFeed = ArrayList<SocialFeed>()


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        // link the ui elements
        ivBackToMain = findViewById(R.id.ivBackToMain)
        ivShowProfile = findViewById(R.id.ivProfileVisibility)
       tvNoSocialFeed = findViewById(R.id.tvNoSocialFeed)
       mcvFeedBorder = findViewById(R.id.mcvBorder)

       getFriendsRecentlyReadBooks()

        // Back Menu Button On Click Event
        ivBackToMain.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

       // Back Menu Button On Click Event
       ivShowProfile.setOnClickListener {
           val i = Intent(this, UserProfileActivity::class.java)
           startActivity(i)
       }

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
        profileRepository.profileData.observe(this, Observer { userProfile ->
            val username: String = userProfile.username // Get the current user's username
            val recentRepository = RecentRepository(database, username, mutableListOf()) // Create a recent repository for the current user

            recentRepository.fetchRecentData() // Fetch the recent data from the database
            recentRepository.isRecentDataReady.observe(this, Observer { isRecentDataReady ->
                if (isRecentDataReady) {
                    val sortedFeeds: MutableList<SocialFeed> = recentRepository.SocialFeeds.sortedByDescending { it.lastReadTime }.toMutableList()
                    // Sort the social feeds by last read time in descending order
                    setUpSocialFeed(sortedFeeds) // Set up the social feed UI using the sorted social feeds
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
        val rvSocial: RecyclerView = findViewById(R.id.rvSocial)

        // Check if friendSocialFeed is not empty before setting the adapter
        if (sortedFeeds.isNotEmpty()) {
            tvNoSocialFeed.visibility = android.view.View.INVISIBLE // Hide the "No Social Feed" message
            rvSocial.visibility = android.view.View.VISIBLE // Show the social feed RecyclerView

            // Set up the adapter
            val adapter = SocialFeedRecyclerViewAdapter(this, this, sortedFeeds)
            rvSocial.layoutManager = LinearLayoutManager(this)
            rvSocial.adapter = adapter
            adapter.notifyDataSetChanged() // Notify the adapter that the data set has changed
        } else {
            tvNoSocialFeed.visibility = android.view.View.VISIBLE // Show the "No Social Feed" message
            rvSocial.visibility = android.view.View.INVISIBLE // Hide the social feed RecyclerView
        }
    }


}