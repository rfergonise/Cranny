package com.example.cranny

import android.os.Bundle
import com.example.cranny.databinding.ActivityDashboardBinding

class DashboardActivity : DrawerBaseActivity() {

    private lateinit var activityDashboardBinding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(activityDashboardBinding.root)
    }
}