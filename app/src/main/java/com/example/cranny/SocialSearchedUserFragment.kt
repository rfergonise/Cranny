package com.example.cranny

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase

class SocialSearchedUserFragment : DialogFragment() {

    lateinit var fragmentView: View
    lateinit var tvUsername: TextView
    lateinit var ivProfilePicture: ImageView
    lateinit var mcvSendFriendRequestButton: MaterialCardView

    private var isFriendRequestSent = false


    interface OnDialogDismissedListener {
        fun onDismiss(isFriendRequestSent: Boolean)
    }
    private var onDialogDismissedListener: OnDialogDismissedListener? = null

    fun setOnDialogDismissedListener(listener: OnDialogDismissedListener) {
        onDialogDismissedListener = listener
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDialogDismissedListener?.onDismiss(isFriendRequestSent)
    }



    companion object {
        fun newInstance(friend: Friend, user: User): SocialSearchedUserFragment {
            val fragment = SocialSearchedUserFragment()
            val args = Bundle()
            args.putParcelable("friend", friend)
            args.putParcelable("user", user)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_social_searched_user, container, false)
        // get the passed in user
        val friend = arguments?.getParcelable<Friend>("friend")
        val user = arguments?.getParcelable<User>("user")

        tvUsername = fragmentView.findViewById(R.id.tvUsername)
        ivProfilePicture = fragmentView.findViewById(R.id.ivProfilePicture)
        mcvSendFriendRequestButton = fragmentView.findViewById(R.id.mcvRequestButton)

        tvUsername.text = "@${friend!!.username}"

        val database = FirebaseDatabase.getInstance()
        val profilePictureRepository = ProfilePictureRepository(database, friend.id)
        profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture)

        mcvSendFriendRequestButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val requestRepo = FriendRequestRepository(database, friend.id) // Creates an instance of the FriendRequestRepository
            requestRepo.addFriendRequest(Friend(user!!.userId, user!!.username, false)) // Adds a friend request to the repository
            val requestedRepo = RequestedRepository(database, user.userId)
            requestedRepo.addRequestedUser(friend) // tracks what users they send a friend request to
            Toast.makeText(context, "Friend Request Sent!", Toast.LENGTH_SHORT).show()
            isFriendRequestSent = true // inform the main thread that the user sent a friend request
            parentFragmentManager.beginTransaction().remove(this).commit() // close this fragment
        }


        return fragmentView
    }
}