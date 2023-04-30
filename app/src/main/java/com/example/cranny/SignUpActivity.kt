package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {

    // UI Elements needed
    private lateinit var buttonDone: Button
    private lateinit var textUsername: EditText
    private lateinit var textDisplayName: EditText
    private lateinit var textBio: EditText
    private lateinit var imageProfile: ImageView

    // Firebase stuff
    val database = FirebaseDatabase.getInstance()
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    var isProfilePictureSelected: Boolean = false

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Get the ui elements linked
        buttonDone = findViewById(R.id.bDoneCreatingProfile)
        textUsername = findViewById(R.id.etUsername)
        textDisplayName = findViewById(R.id.etDisplayName)
        textBio = findViewById(R.id.etBio)
        imageProfile = findViewById(R.id.ivUploadPFP)

        // Upload Profile On Click Event Handling
        imageProfile.setOnClickListener {
            // Create an Intent to select an image from the gallery
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"

            // Start the Intent with a request code
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        // Done Creating Profile On Click Event Handling
        buttonDone.setOnClickListener {

            if(isNotEmpty()) // checks the Username, Display Name, and Profile Picture for being empty
            {
                // if no space in username and starts with a letter,and both fields aren't blank
                // then check if the username exists in the database already
                val username = textUsername.text.toString()
                val ServerRepository = ServerRepository(database)
                ServerRepository.isUserListReady.observe(this, Observer { isUserListReady ->
                    if(isUserListReady)
                    {
                        for(user in ServerRepository.Users)
                        {
                            if(username == user.username)
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
                                ServerRepository.stopUserListener()
                            }
                            // start main activity
                            val i = Intent(this, DashboardActivity::class.java)
                            startActivity(i)

                        }
                    }
                })
            }
        }
    }

    private fun isNotEmpty(): Boolean
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

        if(!isProfilePictureSelected)
        {
            Toast.makeText(this, "No profile picture added.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            imageProfile.setImageURI(imageUri) // if we got an image from the user, set it as the image
            val pictureRepository = ProfilePictureRepository(database, currentUser!!.uid)
            pictureRepository.uploadProfilePicture(imageUri)
            isProfilePictureSelected = true
        }
    }

    private fun updateUserInformation(server: ServerRepository)
    {
        // Add User Profile Data
        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, currentUser!!.uid)
        val newUsername = textUsername.text.toString()
        val newDisplayName = textDisplayName.text.toString()
        val newUserId = currentUser!!.uid
        val newBio = textBio.text.toString()
        val newFriendCount = 0
        val newBookCount = 0
        profileRepo.updateProfileData(newUsername, newDisplayName, newUserId, newBio, newFriendCount, newBookCount)

        // Add User's Username to Usernames List
        server.addUser(Friend(newUserId, newUsername))
    }



}

