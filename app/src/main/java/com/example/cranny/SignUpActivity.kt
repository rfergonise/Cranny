package com.example.cranny

/**
This Kotlin file represents the SignUpActivity class, which is an activity in an Android app.
It provides functionality for user sign-up, including capturing user details such as username, display name,
and profile picture. It communicates with Firebase for authentication and database operations.
The activity allows users to select a profile picture from the gallery and update their profile information.
The sign-up process includes validation checks for input fields and displays appropriate error messages.
Upon successful sign-up, the user's profile information is stored in the Firebase Realtime Database.
 */

import android.annotation.SuppressLint
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

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Get the UI elements linked
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
            if (isNotEmpty()) {
                // Check if the Username, Display Name, and Profile Picture are not empty

                val username = textUsername.text.toString()
                val ServerRepository = ServerRepository(database)
                ServerRepository.isUserListReady.observe(this, Observer { isUserListReady ->
                    if (isUserListReady) {
                        for (user in ServerRepository.Users) {
                            if (username == user.username) {
                                // Username exists
                                Toast.makeText(this, "Username already in use.", Toast.LENGTH_SHORT).show()
                                textUsername.text.clear()
                            } else {
                                // Username doesn't exist
                                updateUserInformation(ServerRepository)

                                // Stop the database listener checking usernames
                                ServerRepository.stopUserListener()
                            }
                            // Start the main activity
                            val i = Intent(this, DashboardActivity::class.java)
                            startActivity(i)
                        }
                    }
                })
            }
        }
    }

    /**
     * Checks if the username, display name, and profile picture are not empty.
     *
     * @return `true` if the fields are not empty, `false` otherwise.
     */
    private fun isNotEmpty(): Boolean {
        if (textUsername.text.toString() == "") {
            Toast.makeText(this, "Username is blank.", Toast.LENGTH_SHORT).show()
            return false
        } else if (textUsername.text.toString().contains(" ")) {
            Toast.makeText(this, "Username cannot have spaces.", Toast.LENGTH_SHORT).show()
            return false
        } else if (!textUsername.text.matches(Regex("^[a-zA-Z].*"))) {
            Toast.makeText(this, "Username must start with a letter.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (textDisplayName.text.toString() == "") {
            Toast.makeText(this, "Display name is blank.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isProfilePictureSelected) {
            Toast.makeText(this, "No profile picture added.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    /**
     * Called after an activity returns a result. Handles the result of image selection from the gallery.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode The result code returned by the child activity.
     * @param data The data returned from the child activity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            imageProfile.setImageURI(imageUri) // If we got an image from the user, set it as the image
            val pictureRepository = ProfilePictureRepository(database, currentUser!!.uid)
            pictureRepository.uploadProfilePicture(imageUri)
            isProfilePictureSelected = true
        }
    }

    /**
     * Updates the user's profile information in the server.
     *
     * @param server The server repository to communicate with the database.
     */
    private fun updateUserInformation(server: ServerRepository) {
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
        server.addUser(Friend(newUserId, newUsername, false))
    }



}

