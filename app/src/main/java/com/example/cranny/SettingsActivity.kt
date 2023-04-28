package com.example.cranny


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton

class SettingsActivity : AppCompatActivity() {
    private lateinit var backButton: AppCompatImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_container, SettingsFragment())
            .commit()



    }

    //
}