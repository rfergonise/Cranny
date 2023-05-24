package com.example.cranny

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.ImageView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.example.cranny.databinding.ActivityDashboardBinding

class DashboardActivity : DrawerBaseActivity() {

    // Used for view binding
    // What is view Binding in Android?
    // View Binding is a way of connecting your code to specific views in your layout.
    // This lets you access and modifies the view properties directly from your code.
    lateinit var activityDashboardBinding: ActivityDashboardBinding
     private lateinit var binding: ActivityDashboardBinding
     lateinit var ivBackToMainButton: ImageView
     lateinit var viewPager: ViewPager

    companion object {
        const val ACTION_CHANGE_VIEWPAGER_ITEM = "com.example.ACTION_CHANGE_VIEWPAGER_ITEM"
        const val EXTRA_VIEWPAGER_ITEM = "extra_viewpager_item"
    }

    private val viewPagerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val itemId = intent?.getIntExtra(EXTRA_VIEWPAGER_ITEM, 0)
            if (itemId != null) {
                viewPager.currentItem = itemId
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_layout)
        // binding activity to menu
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(activityDashboardBinding.root)

        // title attached to toolbar
        allocateActivityTitle(" ")

        viewPager = findViewById<ViewPager>(R.id.viewPager)

        val intentFilter = IntentFilter(ACTION_CHANGE_VIEWPAGER_ITEM)
        LocalBroadcastManager.getInstance(this).registerReceiver(viewPagerReceiver, intentFilter)

        val socialFragmentAdapter = SocialFragmentAdapter(supportFragmentManager)
        socialFragmentAdapter.addFragment(SocialRequestFragment(), "Find Friends")
        socialFragmentAdapter.addFragment(SocialProfileFragment(), "Profile")
        socialFragmentAdapter.addFragment(SocialFriendFragment(), "Friends")

        viewPager.adapter = socialFragmentAdapter
        viewPager.currentItem = 1

    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(viewPagerReceiver)
    }



}