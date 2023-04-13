package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var buttonLogout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonLogout = findViewById(R.id.bLogout)
        buttonLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val i  = Intent(this,SignInActivity::class.java)
            startActivity(i)
        }
    }
}