package com.example.cranny


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.database.core.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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



    private fun getFriendsRecentlyReadBooks() {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().uid!!
        val profileRepository = ProfileRepository(database, userId)
        profileRepository.profileData.observe(this, Observer { userProfile ->
            val username: String = userProfile.username
            val recentRepository = RecentRepository(database, username, mutableListOf())
            recentRepository.fetchRecentData()
            recentRepository.isRecentDataReady.observe(this, Observer { isRecentDataReady ->
                if(isRecentDataReady)
                {
                    val sortedFeeds: MutableList<SocialFeed> = recentRepository.SocialFeeds.sortedByDescending { it.lastReadTime }.toMutableList()
                    setUpSocialFeed(sortedFeeds)
                }
            })

        })
        profileRepository.stopProfileListener()
    }

    private fun setUpSocialFeed(sortedFeeds: MutableList<SocialFeed>)
    {
        val rvSocial: RecyclerView = findViewById(R.id.rvSocial)
        // Check if friendSocialFeed is not empty before setting the adapter
        if (sortedFeeds.isNotEmpty()) {
            tvNoSocialFeed.visibility = android.view.View.INVISIBLE
            rvSocial.visibility = android.view.View.VISIBLE
            // Set up the adapter
            val adapter = SocialFeedRecyclerViewAdapter(this, sortedFeeds)
            rvSocial.layoutManager = LinearLayoutManager(this)
            rvSocial.adapter = adapter
            adapter.notifyDataSetChanged() // Notify the adapter that the data set has changed
        }
        else
        {
            tvNoSocialFeed.visibility = android.view.View.VISIBLE
            rvSocial.visibility = android.view.View.INVISIBLE
        }
    }


}