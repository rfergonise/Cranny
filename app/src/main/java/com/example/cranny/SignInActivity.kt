package com.example.cranny

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity()
{

    private lateinit var btnSignIn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnSignIn = findViewById(R.id.bGoogleSignIn)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        progressDialog = ProgressDialog(this@SignInActivity).apply {
            setTitle("Creating account")
            setMessage("Creating your account.")
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build() // creates the gso object with the specified options^

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        btnSignIn.setOnClickListener {
            // code to be executed on button click
            signIn()
        }

    }

    val RC_SIGN_IN = 40

    private fun signIn()
    {
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
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
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    // code to be executed if the sign-in is successful
                    val curUser = auth.currentUser
                    val user: User = User()
                    if (curUser != null) {
                        user.userId = curUser.uid
                        user.name = curUser.displayName.toString()
                        user.profile = curUser.photoUrl.toString()

                        // adds the user to the database and gives them a username of their google name + 9 random numbers on the end.
                        addUserToDatabase(user.userId, user.name, createRandomUsername(user.name), user.profile)

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(intent)

                    }

                }
                else
                {
                    // code to be executed if the sign-in fails
                    Toast.makeText(this@SignInActivity, "Error: firebaseAuth task was unsuccessful", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(userId: String, name: String, username: String, pfpURL: String)
    {
        database.reference.child("UserData").child(userId).child("Profile").child("UserId").setValue(userId)
        database.reference.child("UserData").child(userId).child("Profile").child("Username").setValue(username)
        database.reference.child("UserData").child(userId).child("Profile").child("Name").setValue(name)
        database.reference.child("UserData").child(userId).child("Profile").child("ProfilePictureURL").setValue(pfpURL)
        database.reference.child("UserData").child(userId).child("Profile").child("FriendCount").setValue(0)
        database.reference.child("UserData").child(userId).child("Profile").child("BookCount").setValue(0)
    }

    private fun createRandomUsername(s: String): String {
        val maxChars = 15
        val random = (0..999999999).random().toString().take(9)
        val newString = "$s$random".replace(" ", "").toLowerCase()
        return if (newString.length > maxChars) {
            newString.take(maxChars)
        } else {
            newString
        }
    }


}