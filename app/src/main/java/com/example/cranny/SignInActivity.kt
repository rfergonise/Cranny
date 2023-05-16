package com.example.cranny

/**
This Kotlin file represents the SignInActivity, which handles the sign-in process with Google accounts using Firebase authentication.
It provides UI elements for signing in, initializes Firebase and Google Sign-In client, and manages the sign-in flow.
The user can click the sign-in button to initiate the sign-in process, which starts the sign-in intent.
After the user signs in with their Google account, the onActivityResult() method is called, retrieving the user's signed-in account and authenticating the user with Firebase.
If the sign-in is successful, the user's information is retrieved and checked against the database. If the user exists, they are directed to the DashboardActivity; otherwise, they are prompted to create a profile in the SignUpActivity.
This file utilizes the following dependencies:
AndroidX libraries
Firebase Authentication and Realtime Database
Google Sign-In API
Note: The RC_SIGN_IN constant is a custom request code used to handle the sign-in intent.
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity()
{
    // UI elements
    private lateinit var btnSignIn: Button

    // Firebase stuff
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    // Custom RC Sign In number to handle our sign in intent
    val RC_SIGN_IN = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Link the UI elements
        btnSignIn = findViewById(R.id.bGoogleSignIn)

        // Link the Firebase stuff
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Create GoogleSignInOptions with the specified options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Create a GoogleSignInClient with the GoogleSignInOptions
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up OnClickListener for the sign-in button
        btnSignIn.setOnClickListener {
            signIn()
        }
    }

    /**
     * Initiates the sign-in process by starting the sign-in intent.
     */
    private fun signIn() {
        // User wants to sign in, so start the sign-in intent
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    /**
     * Called after the user signs in with their Google account.
     *
     * @param requestCode The request code.
     * @param resultCode The result code.
     * @param data The intent data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // This function is called after the user signs in with their Google account.
        // It retrieves the user's signed-in account from the intent data and passes the user's ID token to the firebaseAuth() function to authenticate the user with Firebase.
        // If the account retrieval is unsuccessful, it throws a RuntimeException with the caught ApiException as its cause.

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuth(account.idToken)
            } catch (e: ApiException) {
                // Throw a RuntimeException with the caught ApiException as its cause
                throw RuntimeException(e)
            }
        }
    }

    /**
     * Authenticates the user with Firebase using the provided ID token.
     *
     * @param idToken The ID token obtained from Google Sign-In.
     */
    private fun firebaseAuth(idToken: String?) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Code to be executed if the sign-in is successful
                    val curUser = auth.currentUser
                    val user: User = User()
                    if (curUser != null) {
                        user.userId = curUser.uid
                        user.name = curUser.displayName.toString()

                        val userDatabase = FirebaseDatabase.getInstance().getReference("UserData")
                        userDatabase.child(curUser.uid).get().addOnSuccessListener {
                            if (it.exists()) {
                                // User already exists in the database
                                val intent = Intent(this@SignInActivity, DashboardActivity::class.java)
                                startActivity(intent)
                            } else {
                                // User doesn't exist in the database, send them to create a profile
                                val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                                startActivity(intent)
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to read database.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

}