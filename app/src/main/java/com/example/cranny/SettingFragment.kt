package com.example.cranny

import android.content.Context
import android.content.Intent
import android.icu.util.Freezable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.cranny.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : PreferenceFragmentCompat() {
    private fun getUserId(): String? {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return firebaseUser?.uid
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        //account privacy switch
        val accountPrivacySwitch: SwitchPreferenceCompat? = findPreference("account_private")
        accountPrivacySwitch?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val isPrivate = newValue as Boolean
                val userId = getUserId()
                if (userId != null) {
                    savePrivateAccountSetting(userId, isPrivate)
                }
                true
            }
        //account notification switch
        val accountNotificationSwitch: SwitchPreferenceCompat? =
            findPreference("enable_notifications")
        accountNotificationSwitch?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->

                val isNotified = newValue as Boolean
                val userId = getUserId()
                if (userId != null) {
                    saveNotificationsAccountSetting(userId, isNotified)
                }
                true
            }
        //account change username
        val usernamePreference: EditTextPreference? = findPreference("username")
        usernamePreference?.setOnPreferenceChangeListener { _, newValue ->
            val newUsername = newValue as String
            val userId = getUserId()
            if (userId != null) {
                updateUsername(userId, newUsername)
            }
            true
        }


    }

    //account privacy
    private fun savePrivateAccountSetting(userID: String, isPrivate: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val userPreferencesRef =
            database.getReference("UserData").child(userID).child("Preferences")
        userPreferencesRef.child("account_private").setValue(isPrivate)
    }

    //account notifications
    private fun saveNotificationsAccountSetting(userID: String, isNotified: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val userPreferencesRef =
            database.getReference("UserData").child(userID).child("Preferences")
        userPreferencesRef.child("enable_notifications").setValue(isNotified)
    }

    //account change username
    private fun updateUsername(userID: String, newUsername: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.reference.child("UserData").child(userID).child("Profile")

        val serverRepository = ServerRepository(database)

        serverRepository.isUserListReady.observe(this, Observer { isUserListReady ->
            if (isUserListReady) {
                var usernameAvailable = true

                for (user in serverRepository.Users) {
                    if (newUsername == user.username) {
                        // Username exists
                        usernameAvailable = false
                        break
                    }
                }

                if (usernameAvailable) {
                    // Username doesn't exist
                    userRef.child("Username").setValue(newUsername)
                    Toast.makeText(requireContext(), "Username changed to $newUsername", Toast.LENGTH_SHORT).show()
                } else {
                    // Username exists
                    Toast.makeText(requireContext(), "Username is already in use", Toast.LENGTH_SHORT).show()
                }

                // Stop the database listener checking usernames
                serverRepository.stopUserListener()
            }
        })


        // need to get old username in order to update it
        val profileRepo = ProfileRepository(database, userID)
        profileRepo.profileData.observe(this, Observer { userProfile ->
            val oldUsername: String = userProfile.username
            val ServerRepository = ServerRepository(database)
            ServerRepository.isUserListReady.observe(this, Observer { isUserListReady ->
                if (isUserListReady) {
                    for (user in ServerRepository.Users) {
                        if (newUsername == user.username) {
                            // Username exists
                            Toast.makeText(
                                requireContext(),
                                "Username is already in user",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Username doesn't exist
                            ServerRepository.updateUser(
                                Friend(userID, oldUsername, false),
                                Friend(userID, newUsername, false)
                            )
                            userRef.child("Username").setValue(newUsername)
                            Toast.makeText(
                                requireContext(),
                                "Username changed to $newUsername",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // Stop the database listener checking usernames
                        ServerRepository.stopUserListener()
                    }
                }
            })
        })
        profileRepo.stopProfileListener() // free the listener to stop memory leaks

    }
}










