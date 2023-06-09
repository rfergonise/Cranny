package com.example.cranny


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Observer
import com.example.cranny.databinding.ActivitySettingsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SettingsActivity : AppCompatActivity() {
    private lateinit var backButton: AppCompatImageButton
    private lateinit var clearLibraryButton: Button
    private lateinit var deleteAccountButton: Button
    private lateinit var logoutButton: Button
    private val auth = FirebaseAuth.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    private var username: String = ""
    val firebaseDatabase = FirebaseDatabase.getInstance()
    val userId: String = currentUser!!.uid
    val database = FirebaseDatabase.getInstance()
    lateinit var bookRepository: BookRepository
    val profileRepository = ProfileRepository(firebaseDatabase, userId)

    // Used for view binding
    lateinit var activitySettingsBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding activity to menu
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding.root)

        //back button navigates back to the dashboard
        val btSettingsActivity : Button = findViewById(R.id.btSettingsActivity)
        btSettingsActivity.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
        }

        //back to main menu button logic
        /*backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            }*/


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_container, SettingsFragment())
            .commit()


        val profileRepo = ProfileRepository(database, currentUser!!.uid)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            val username: String = userProfile.username
            bookRepository = BookRepository(database, Friend(currentUser!!.uid, username, false))
            //Clear Library Function
            clearLibraryButton = findViewById(R.id.clearLibraryButton)
            clearLibraryButton.setOnClickListener {
                showClearLibraryDialog()
            }
        })
        profileRepo.stopProfileListener()

        //delete button logic
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        deleteAccountButton.setOnClickListener {
            showDeleteAccountDialog()
        }

        //sign out button logic
        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {

            signOut()
        }




    }


    //function to erase user data
    private fun deleteUserInformation() {
        val curUser = FirebaseAuth.getInstance().currentUser // get the current user
        val username = curUser.toString() // Replace with the actual username to be deleted
        val database = FirebaseDatabase.getInstance()
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
    //delete account confirmation
    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account? This is a pernament action.")
        builder.setPositiveButton("Agree") { _, _ ->
            deleteUserInformation()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    //Clear Library Confirmation and Deletion
    private fun showClearLibraryDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Clear Library")
            .setMessage("Are you sure you want to clear your Library?")
            .setPositiveButton("Yes") { _, _ ->

                // Clear user library
                bookRepository.clearUserLibrary(this)

                // Observe for changes in the library
                bookRepository.isBookDataReady.observe(this, Observer { isLibraryCleared ->
                    if (isLibraryCleared) {
                        Toast.makeText(
                            this,
                            "Library cleared successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, "Failed to clear library.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    // Remove observer to prevent memory leaks
                    bookRepository.isBookDataReady.removeObservers(this)
                })
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Do nothing
            }
            .create()
        alertDialog.show()
    }



}

