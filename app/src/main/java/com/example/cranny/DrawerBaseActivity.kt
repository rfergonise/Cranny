package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView


open class DrawerBaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Used for layout
    lateinit var drawerLayout: DrawerLayout

    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base_activity, null) as DrawerLayout
        val container = drawerLayout.findViewById<FrameLayout>(R.id.activityContainer)
        container.addView(view)

        super.setContentView(drawerLayout)

        val toolbar = drawerLayout.findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)

        val navigationView = drawerLayout.findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    //menu nav to click events activity
     override fun onNavigationItemSelected(item: MenuItem): Boolean {
         drawerLayout.closeDrawer(GravityCompat.START)

        // switch between activities click events
         when (item.itemId) {
             R.id.nav_settings -> {
                 startActivity(Intent(this, SettingsActivity::class.java))
                 overridePendingTransition(0, 0)
             }
             R.id.nav_social -> {
                 startActivity(Intent(this, SocialActivity::class.java))
                 overridePendingTransition(0, 0)
             }
             R.id.nav_library -> {
                 startActivity(Intent(this, LibraryActivity::class.java))
                 overridePendingTransition(0, 0)
             }
             R.id.nav_addabook -> {
                 startActivity(Intent(this, AddBookPage::class.java))
                 overridePendingTransition(0, 0)
             }
         }
        return false
    }

    fun allocateActivityTitle(titleString: String) {
        // will display whatever title you want on the tool bar
        supportActionBar?.title = titleString
    }

}