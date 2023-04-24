package com.example.cranny


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {
    private lateinit var backButton: AppCompatImageButton
    private lateinit var deleteAccountButton: Button
    private lateinit var logoutButton: Button
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private var username: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //back to main menu button logic
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_container, SettingsFragment())
            .commit()

        //delete button logic
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        deleteAccountButton.setOnClickListener{
            deleteUserInformation()

        }

        //sign out button logic
        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {

            signOut()
        }


    }


    //function to erase user data
    private fun deleteUserInformation()
    {
        val curUser = FirebaseAuth.getInstance().currentUser // get the current user
        if (curUser != null)
        {
            // if the user isn't null
            curUser.delete() // delete them from firebase
            val database = FirebaseDatabase.getInstance()
            val userRef = database.reference.child("UserData").child(curUser.uid) // get the path to their user data location in the database
            val usernameRef = database.reference.child("ServerData").child("Usernames").child(username) // get the path to their username in the taken username list

            usernameRef.removeValue() // clear their information from the database
            userRef.removeValue() // clear the username from the taken username list
        }
        signOut() // sign the user out of the app
    }

    //function to sign out of account
    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.revokeAccess().addOnCompleteListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    //
}