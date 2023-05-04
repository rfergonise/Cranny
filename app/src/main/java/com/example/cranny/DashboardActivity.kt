package com.example.cranny

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.cranny.databinding.ActivityDashboardBinding

class DashboardActivity : DrawerBaseActivity() {

     lateinit var activityDashboardBinding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)

        setContentView(activityDashboardBinding.root)
        allocateActivityTitle("dashboard")

        val btMainActivity : Button = findViewById(R.id.btMainActivity)
        btMainActivity.setOnClickListener {
            val i = Intent (this, MainActivity::class.java)
            startActivity(i)
        }
    }
}