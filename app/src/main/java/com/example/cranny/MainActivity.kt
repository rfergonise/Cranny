package com.example.cranny



import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var buttonLogout: Button
    private lateinit var settingsButton: Button
    private lateinit var buttonSocial: Button
    private lateinit var buttonDeleteAccount: Button

    // Firebase stuff
    val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private var username: String = ""
    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getUserData() // retrieves the username from the database

        // Social Button On Click Event Handling
        buttonSocial = findViewById(R.id.bSocial)
        buttonSocial.setOnClickListener{
            val i = Intent(this, SocialActivity::class.java)
            startActivity(i)
        }
        // Log Out Button On Click Event Handling
        buttonLogout = findViewById(R.id.bLogout)
        buttonLogout.setOnClickListener{
            signOut()
        }
        // Delete Account Button On Click Event Handling
        buttonDeleteAccount = findViewById(R.id.bDeleteAccount)
        buttonDeleteAccount.setOnClickListener{
            deleteUserInformation(currentUser?.uid!!)
        }
        // Settings Button
       settingsButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener{
            val i = Intent(this, SettingsActivity::class.java)
            startActivity(i)
        }
        // Add Book Button
        val buttonAddBook = findViewById<Button>(R.id.bAddBook)
        buttonAddBook.setOnClickListener{
            val i = Intent(this, AddBookPage::class.java)
            startActivity(i)
        }
        // Library Button
        val buttonLibrary = findViewById<Button>(R.id.bLibrary)
        buttonLibrary.setOnClickListener{
            val i = Intent(this, LibraryActivity::class.java)
            startActivity(i)
        }
    }
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
    private fun getUserData()
    {
        val userDatabase = FirebaseDatabase.getInstance().getReference("UserData")
        userDatabase.child(currentUser!!.uid).get().addOnSuccessListener {

            if(it.exists())
            {
                username = it.child("Profile").child("Username").value as String
                id = currentUser!!.uid
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to read database.", Toast.LENGTH_SHORT).show()
        }
    }




    private fun deleteUserInformation(userId: String)
    {
        val serverRepository = ServerRepository(database)
        serverRepository.removeUser(Friend(userId, username, false))
        val profileRepository = ProfileRepository(database, userId)
        profileRepository.removeUser(username)
        val friendRepo = FriendRepository(database, username, userId, this)
        friendRepo.fetchFriends()
        friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
            if(isFriendsReady)
            {
                val friendCount = friendRepo.FriendIds.size
                if(friendCount > 0)
                {
                    val friends = mutableListOf<Friend>()
                    for(friend in friendRepo.FriendIds)
                    {
                        friends.add(Friend(friend.id, friend.username, false))
                    }
                    for (friend in friends)
                    {
                        val friends = FriendRepository(database, friend.username, friend.id, this)
                        friends.removeFriend(Friend(userId, username, false))
                    }
                    signOut() // sign the user out of the app
                }
            }
        })
        friendRepo.stopFriendListener() // free the listener to stop memory leaks

    }


}
