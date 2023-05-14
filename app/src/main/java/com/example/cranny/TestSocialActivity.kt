package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class TestSocialActivity : AppCompatActivity() {

    lateinit var ivBackToMainButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_social)

        ivBackToMainButton = findViewById(R.id.ivBackToMain)
        ivBackToMainButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        var viewPager = findViewById<ViewPager>(R.id.viewPager)

        val socialFragmentAdapter = SocialFragmentAdapter(supportFragmentManager)
        socialFragmentAdapter.addFragment(SocialRequestFragment(), "Find Friends")
        socialFragmentAdapter.addFragment(SocialProfileFragment(), "Profile")
        socialFragmentAdapter.addFragment(SocialFriendFragment(), "Friends")

        viewPager.adapter = socialFragmentAdapter
        viewPager.currentItem = 1
    }
}