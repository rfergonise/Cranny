package com.example.cranny

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel : ViewModel() {

    // This is used to hold a boolean value indicating whether a given text
    // (in this case, a username) is acceptable or not.
    private val _isTextAcceptable = MutableLiveData<Boolean>()
    val isTextAcceptable: LiveData<Boolean> = _isTextAcceptable


    // This function takes in a username as a parameter and checks whether the username is available by calling the isUsernameTaken() function.
    // If the username is not taken, it sets the value of the _isTextAcceptable variable to true using the value property.
    // If the username is taken, it sets the value of _isTextAcceptable to false by negating the isTaken value.
    fun checkTextAcceptable(username: String) {
        isUsernameTaken(username).addOnSuccessListener { isTaken ->
            _isTextAcceptable.value = !isTaken
        }
    }

    // This function takes in a username as a parameter and checks whether the username already exists in the Firebase Realtime Database.
    // It first retrieves a reference to the "Usernames" child node in the "ServerData" node of the database using the FirebaseDatabase.getInstance().getReference() method.
    // It then retrieves the data for the specified child node using the get() method, which returns a Task<DataSnapshot> object.
    // The continueWith() method is then called on the Task<DataSnapshot> object to transform the task into a Task<Boolean> object that indicates whether the data snapshot exists or not.
    // Finally, the Task<Boolean> object is returned.
    private fun isUsernameTaken(username: String): Task<Boolean> {
        val userDatabase = FirebaseDatabase.getInstance().getReference("ServerData").child("Usernames")
        return userDatabase.child(username).get().continueWith { task ->
            task.result.exists()
        }
    }
}