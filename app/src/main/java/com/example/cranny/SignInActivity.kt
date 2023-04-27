package com.example.cranny

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

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // link the ui elements
        btnSignIn = findViewById(R.id.bGoogleSignIn)

        // link the firebase stuff
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.API_KEY)
            .requestEmail()
            .build() // creates the gso object with the specified options^
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // OnClickListener that calls the signIn() function when clicked.
        btnSignIn.setOnClickListener{
            signIn()
        }

    }

    private fun signIn()
    {
        // user wants to sign in so start our intent
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        // This function is called after the user signs in with their Google account.
        // It retrieves the user's signed-in account from the intent data and passes the user's ID token to the firebaseAuth() function to authenticate the user with Firebase.
        // If the account retrieval is unsuccessful, it throws a RuntimeException with the caught ApiException as its cause.
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try
            {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuth(account.idToken)
            }
            catch (e: ApiException)
            {
                throw RuntimeException(e)
            }
        }
    }

    private fun firebaseAuth(idToken: String?)
    {
        // This function takes in a Google ID token as a parameter and uses it to create a Google authentication credential using the GoogleAuthProvider.getCredential() method.
        // The credential is then used to sign in to Firebase using the auth.signInWithCredential() method.
        // If the sign-in is successful, the function gets the current user's ID, name, and profile picture URL from Firebase and creates a User object.
        // It then checks whether the user already exists in the database and starts the appropriate activity based on the result.
        // If the database read fails, a toast message is displayed.
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // code to be executed if the sign-in is successful
                    val curUser = auth.currentUser
                    val user: User = User()
                    if (curUser != null)
                    {
                        user.userId = curUser.uid
                        user.name = curUser.displayName.toString()
                        val userDatabase = FirebaseDatabase.getInstance().getReference("UserData")
                        userDatabase.child(curUser.uid).get().addOnSuccessListener {
                            if(it.exists())
                            {
                                // User already exists in the database
                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            else
                            {
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