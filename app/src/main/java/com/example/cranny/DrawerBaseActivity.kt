package com.example.cranny

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView


open class DrawerBaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

     override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    fun allocateActivityTitle(titleString: String) {
        supportActionBar?.title = titleString
    }

}