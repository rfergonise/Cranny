package com.example.cranny

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.example.cranny.databinding.ActivityDashboardBinding

class DashboardActivity : DrawerBaseActivity() {

    // Used for view binding
    // What is view Binding in Android?
    // View Binding is a way of connecting your code to specific views in your layout.
    // This lets you access and modifies the view properties directly from your code.
    /* lateinit var activityDashboardBinding: ActivityDashboardBinding
     private lateinit var binding: ActivityDashboardBinding*/
     lateinit var ivBackToMainButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_layout)
        // binding activity to menu
        /*activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(activityDashboardBinding.root)*/

        // title attached to toolbar
        allocateActivityTitle("Dashboard")

        // button navigates to temp home screen
        /*val btMainActivity : Button = findViewById(R.id.btMain)
        btMainActivity.setOnClickListener {
            val i = Intent (this, MainActivity::class.java)
            startActivity(i)
        }*/

        var viewPager = findViewById<ViewPager>(R.id.viewPager)

        val socialFragmentAdapter = SocialFragmentAdapter(supportFragmentManager)
        socialFragmentAdapter.addFragment(SocialRequestFragment(), "Find Friends")
        socialFragmentAdapter.addFragment(SocialProfileFragment(), "Profile")
        socialFragmentAdapter.addFragment(SocialFriendFragment(), "Friends")

        viewPager.adapter = socialFragmentAdapter
        viewPager.currentItem = 1
    }



}