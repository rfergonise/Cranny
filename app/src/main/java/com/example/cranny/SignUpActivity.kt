package com.example.cranny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {

    // Firebase stuff
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    // UI Elements needed
    private lateinit var buttonDone: Button
    private lateinit var textUsername: EditText
    private lateinit var textDisplayName: EditText
    private lateinit var textBio: EditText
    private lateinit var imageProfile: ImageView

    // View Model for fetching if a username exists in the database
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Get the login view model linked
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Get the ui elements linked
        buttonDone = findViewById(R.id.bDoneCreatingProfile)
        textUsername = findViewById(R.id.etUsername)
        textDisplayName = findViewById(R.id.etDisplayName)
        textBio = findViewById(R.id.etBio)
        imageProfile = findViewById(R.id.ivUploadPFP)

        // Done Creating Profile On Click Event Handling
        buttonDone.setOnClickListener {

            if(isTextNotEmpty()) // checks if the Username and Display Name edit text elements
            {
                // if no space in username and both fields aren't blank
                // then check if the username exists in the database already
                val username = textUsername.text.toString()
                viewModel.checkTextAcceptable(username)
            }
        }

        // When the view model gets the boolean value back from checking if username exists
        viewModel.isTextAcceptable.observe(this) { isTextAcceptable ->
            if (isTextAcceptable) // if the username doesn't exist
            {
                // update user profile data
                updateUserInformation()
                // start main activity
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
            else
            {
                // if the username does exist
                // tell the user and clear the username field
                Toast.makeText(this, "Username already in use.", Toast.LENGTH_SHORT).show()
                textUsername.text.clear()
            }
        }
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

        if(textDisplayName.text.toString() == "")
        {
            Toast.makeText(this, "Display name is blank.", Toast.LENGTH_SHORT).show()
            return false
        }
        // return true if username doesn't have a space and both aren't empty
        return true
    }



    private fun updateUserInformation()
    {
        var username: String = textUsername.text.toString()
        val displayname: String = textDisplayName.text.toString()
        val bio: String = textBio.text.toString()
        // TODO add image saving
        val pfpURL: String = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
        addUserToDatabase(currentUser!!.uid, displayname, username, pfpURL, bio)
    }

    private fun addUserToDatabase(userId: String, name: String, username: String, pfpURL: String, bio: String)
    {
        database.reference.child("UserData").child(userId).child("Profile").child("UserId").setValue(userId)
        database.reference.child("UserData").child(userId).child("Profile").child("Username").setValue(username)
        database.reference.child("ServerData").child("Usernames").child(username).setValue(username) // add their username to the list of usernames taken
        database.reference.child("UserData").child(userId).child("Profile").child("Name").setValue(name)
        database.reference.child("UserData").child(userId).child("Profile").child("ProfilePictureURL").setValue(pfpURL)
        database.reference.child("UserData").child(userId).child("Profile").child("FriendCount").setValue(0)
        database.reference.child("UserData").child(userId).child("Profile").child("BookCount").setValue(0)
        database.reference.child("UserData").child(userId).child("Profile").child("Bio").setValue(bio)
    }




}

