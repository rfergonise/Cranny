package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {

    // Firebase stuff
    //private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    //private val auth = FirebaseAuth.getInstance()
    //private var currentUser = auth.currentUser

    // UI Elements needed
    private lateinit var buttonDone: Button
    private lateinit var textUsername: EditText
    private lateinit var textDisplayName: EditText
    private lateinit var textBio: EditText
    private lateinit var imageProfile: ImageView

    private lateinit var userInfo: User

    private lateinit var profileRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // get the user info
        val database = FirebaseDatabase.getInstance()
        profileRepository = ProfileRepository(database)
        profileRepository.profileData.observe(this) { userProfile ->
            // get the info
            val username = userProfile.username
            val name = userProfile.name
            val friendCount = userProfile.friendCount
            val bookCount = userProfile.bookCount
            val pfpURL = userProfile.profile
            val bio = userProfile.bio
            val id = userProfile.userId
            userInfo = User(id, name, pfpURL, username, friendCount, bookCount, bio)
        }

        // Get the ui elements linked
        buttonDone = findViewById(R.id.bDoneCreatingProfile)
        textUsername = findViewById(R.id.etUsername)
        textDisplayName = findViewById(R.id.etDisplayName)
        textBio = findViewById(R.id.etBio)
        imageProfile = findViewById(R.id.ivUploadPFP)

        // Done Creating Profile On Click Event Handling
        buttonDone.setOnClickListener {

            if(isTextNotEmpty()) // checks the Username and Display Name edit text elements
            {
                // if no space in username and starts with a letter,and both fields aren't blank
                // then check if the username exists in the database already
                val username = textUsername.text.toString()
                val ServerRepository = ServerRepository(database)
                ServerRepository.fetchTakenUsernames()
                ServerRepository.isTakenUsernameListReady.observe(this, Observer { isTakenUsernameListReady ->
                    if(isTakenUsernameListReady)
                    {
                        if(username in ServerRepository.TakenUsernames)
                        {
                            // Username exists
                            Toast.makeText(this, "Username already in use.", Toast.LENGTH_SHORT).show()
                            textUsername.text.clear()
                        }
                        else
                        {
                            // Username doesn't exist
                            updateUserInformation(ServerRepository)

                            // Stop the database listener checking usernames
                            ServerRepository.stopUsernameListener()

                            // start main activity
                            val i = Intent(this, DashboardActivity::class.java)
                            startActivity(i)
                        }
                    }
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        profileRepository.stopProfileListener()
    }

    private fun isTextNotEmpty(): Boolean
    {
        if(textUsername.text.toString() == "")
        {
            Toast.makeText(this, "Username is blank.", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(textUsername.text.toString().contains(" "))
        {
            Toast.makeText(this, "Username cannot have spaces.", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (!textUsername.text.matches(Regex("^[a-zA-Z].*")))
        {
            Toast.makeText(this, "Username must start with a letter.", Toast.LENGTH_SHORT).show()
            return false
        }

        if(textDisplayName.text.toString() == "")
        {
            Toast.makeText(this, "Display name is blank.", Toast.LENGTH_SHORT).show()
            return false
        }
        // return true if username doesn't start with a letter and doesnt have a space
        // and both, username and display name, aren't empty
        return true
    }



    private fun updateUserInformation(server: ServerRepository)
    {
        // Add User Profile Data
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database)
        val newUsername = textUsername.text.toString()
        val newDisplayName = textDisplayName.text.toString()
        val newPfpURL = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
        val newUserId = userInfo.userId
        val newBio = textBio.text.toString()
        val newFriendCount = userInfo.friendCount
        val newBookCount = userInfo.bookCount
        profileRepo.updateProfileData(newUsername, newDisplayName, newPfpURL, newUserId, newBio, newFriendCount, newBookCount)

        // Add User's Username to Usernames List
        server.addUsername(newUsername)
    }


}

