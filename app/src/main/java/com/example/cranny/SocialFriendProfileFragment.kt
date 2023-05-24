package com.example.cranny

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase


class SocialFriendProfileFragment : DialogFragment() {

    // View that hold each ui item
    private lateinit var fragmentView: View

    // ui items
    private lateinit var tvProfileUsername: TextView
    private lateinit var tvProfileBio: TextView
    private lateinit var tvProfileDisplayName: TextView
    private lateinit var tvManageFriendshipUsernameHeader: TextView
    private lateinit var ivProfilePicture: ImageView
    private lateinit var ivFavoriteFriendIcon: ImageView
    private lateinit var mcvManageFriendshipButton: MaterialCardView
    private lateinit var mcvBackgroundBlur: MaterialCardView
    private lateinit var mcvFavoriteFriendButton: MaterialCardView
    private lateinit var mcvRemoveFriendButton: MaterialCardView
    private lateinit var mcvManageFriendshipPopup: MaterialCardView
    private lateinit var rvFeed: RecyclerView
    private lateinit var tvInformUser: TextView

    interface DialogListener {
        fun onDialogClosed(result: Friend)
    }
    private var dialogListener: DialogListener? = null

    fun setDialogListener(listener: DialogListener) {
        dialogListener = listener
    }



    companion object {
        fun newInstance(friend: FriendAdapterData, user: Friend): SocialFriendProfileFragment {
            val fragment = SocialFriendProfileFragment()
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
        fragmentView =  inflater.inflate(R.layout.fragment_friend_profile, container, false)
        val friend = arguments?.getParcelable<FriendAdapterData>("friend")
        val user = arguments?.getParcelable<Friend>("user")
        val context = requireContext()

        ivProfilePicture = fragmentView.findViewById(R.id.ivProfilePicture)
        tvProfileUsername = fragmentView.findViewById(R.id.tvProfileUsername)
        mcvManageFriendshipButton = fragmentView.findViewById(R.id.mcvManageFriendshipButton)
        tvProfileDisplayName = fragmentView.findViewById(R.id.tvProfileDisplayName)
        tvProfileBio = fragmentView.findViewById(R.id.tvProfileBio)
        rvFeed = fragmentView.findViewById(R.id.rvSocial)
        tvInformUser = fragmentView.findViewById(R.id.tvRecycleViewInform)
        mcvBackgroundBlur = fragmentView.findViewById(R.id.mcvBackgroundBlur)
        mcvManageFriendshipPopup = fragmentView.findViewById(R.id.mcvManageFriendshipPopup)
        tvManageFriendshipUsernameHeader = fragmentView.findViewById(R.id.tvPopupUsername)
        mcvRemoveFriendButton = fragmentView.findViewById(R.id.mcvRemoveFriendButton)
        mcvFavoriteFriendButton = fragmentView.findViewById(R.id.mcvFavoriteFriendButton)
        ivFavoriteFriendIcon = fragmentView.findViewById(R.id.ivFavoriteFriendIcon)

        mcvBackgroundBlur.visibility = View.INVISIBLE
        mcvManageFriendshipPopup.visibility = View.INVISIBLE

        SetUpProfile(friend!!, user!!)

        return fragmentView
    }

    fun SetUpProfile(friend: FriendAdapterData, user: Friend)
    {
        val database = FirebaseDatabase.getInstance()
        val context = requireContext()
        val setupRepo = SetupProfileRepository(database, friend.id, false, context)
        setupRepo.isProfileReady.observe(requireActivity()) { isProfileReady ->
            if(isProfileReady)
            {
                val username = setupRepo.profile.username
                tvProfileUsername.text = "@${username}"
                tvManageFriendshipUsernameHeader.text = "@${username}"
                tvProfileDisplayName.text = setupRepo.profile.displayname
                tvProfileBio.text = setupRepo.profile.bio

                // Load the profile picture from the database into the image view
                val profilePictureRepository = ProfilePictureRepository(database, friend.id)
                profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture)


                // set up their book display
                val countOfThree = setupRepo.profile.books.size / 3
                val totalBooksMinusTheLoners = countOfThree * 3
                val countOfLoners = setupRepo.profile.books.size % 3
                var displayBooks = mutableListOf<ThreeDisplayBooks>()
                if(countOfThree > 0)
                {
                    var j = 0
                    for (i in 0 until countOfThree)
                    {
                        val group = ThreeDisplayBooks(
                            setupRepo.profile.books[0 + j],
                            setupRepo.profile.books[1 + j],
                            setupRepo.profile.books[2 + j],
                            3)
                        displayBooks.add(group)
                        j += 3
                    }
                }

                if(countOfLoners > 0)
                {
                    lateinit var groupLoner: ThreeDisplayBooks
                    var abort = false
                    when(countOfLoners)
                    {
                        1->
                        {
                            groupLoner = ThreeDisplayBooks(
                                setupRepo.profile.books[totalBooksMinusTheLoners],
                                setupRepo.profile.books[totalBooksMinusTheLoners], // isnt used
                                setupRepo.profile.books[totalBooksMinusTheLoners], // isnt used
                                1)
                        }
                        2->
                        {
                            groupLoner = ThreeDisplayBooks(
                                setupRepo.profile.books[totalBooksMinusTheLoners],
                                setupRepo.profile.books[totalBooksMinusTheLoners + 1],
                                setupRepo.profile.books[totalBooksMinusTheLoners], // isnt used
                                2)
                        }
                        else-> abort = true
                    }
                    if(!abort) displayBooks.add(groupLoner)
                }

                if(countOfThree == 0 && countOfLoners == 0)
                {
                    rvFeed.visibility = View.INVISIBLE
                    tvInformUser.text = "$username has no books."
                    tvInformUser.visibility = View.VISIBLE
                }
                else
                {
                    rvFeed.visibility = View.VISIBLE
                    tvInformUser.visibility = View.INVISIBLE
                    var data = ProfileAdapterData(friend.id, username, displayBooks, setupRepo.profile.iPriv, false)
                    // set up recycle view
                    rvFeed.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    rvFeed.setHasFixedSize(true)
                    rvFeed.adapter = FriendProfileFragmentAdapter(
                        requireActivity() as DashboardActivity,
                        context,
                        data,
                        database,
                        resources,
                        rvFeed,
                        tvInformUser
                    )
                }
                // set up the manage friendship button events
                StartManageFriendship(database, user, friend)
            }
        }
    }

    private fun StartManageFriendship(database: FirebaseDatabase, user: Friend, friend: FriendAdapterData)
    {
        mcvManageFriendshipButton.setOnClickListener {

            if(friend.isFavorite) ivFavoriteFriendIcon.setImageResource(R.drawable.favorite_filter_icon)
            else ivFavoriteFriendIcon.setImageResource(R.drawable.unfavorite_filter_icon)

            // show the pop up
            mcvBackgroundBlur.visibility = View.VISIBLE
            mcvManageFriendshipPopup.visibility = View.VISIBLE

            // if they click on the blurred background
            mcvBackgroundBlur.setOnClickListener {
                // hide it
                mcvBackgroundBlur.visibility = View.INVISIBLE
                mcvManageFriendshipPopup.visibility = View.INVISIBLE
            }

            // if they click on the favorite button
            mcvFavoriteFriendButton.setOnClickListener {
                var friendRepo = FriendRepository(database, user.username, user.id, this)
                if(friend.isFavorite)
                {
                    // make them not favorite
                    ivFavoriteFriendIcon.setImageResource(R.drawable.unfavorite_filter_icon)
                    friendRepo.updateFavoriteStatus(Friend(friend.id, friend.username, false))
                }
                else
                {
                    // make them favorite
                    ivFavoriteFriendIcon.setImageResource(R.drawable.favorite_filter_icon)
                    friendRepo.updateFavoriteStatus(Friend(friend.id, friend.username, true))
                }
                mcvBackgroundBlur.visibility = View.INVISIBLE
                mcvManageFriendshipPopup.visibility = View.INVISIBLE
            }

            // if they click on the remove friend button
            mcvRemoveFriendButton.setOnClickListener {
                // remove them as a friend
                var friendRepo = FriendRepository(database, user.username, user.id, this)
                friendRepo.removeFriend(Friend(friend.id, friend.username, false))

                // hide the friendship manage pop up
                mcvBackgroundBlur.visibility = View.INVISIBLE
                mcvManageFriendshipPopup.visibility = View.INVISIBLE

                dialogListener?.onDialogClosed(Friend(friend.id, friend.username, true)) // true informs the SocialFriendFragment that we just removed this user as a friend
                dismiss()
            }

        }
    }
}
class FriendProfileFragmentAdapter(private val activity: DashboardActivity, val context: Context, private val data: ProfileAdapterData,
                             private var database: FirebaseDatabase, private var resource: Resources, private var rvFeed: RecyclerView, private var tvInformUser: TextView
): RecyclerView.Adapter<FriendProfileFragmentAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_profile, parent, false)

        // Create and return a new ViewHolder with the inflated layout
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return data.bookGrouping.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // todo load book covers
        SetUpBooks(holder, data.bookGrouping[position], data.username)
    }

    private fun SetUpBooks(holder: MyViewHolder, books: ThreeDisplayBooks, username: String)
    {
        holder.mcvBookButton1.visibility = View.INVISIBLE
        holder.mcvBookButton2.visibility = View.INVISIBLE
        holder.mcvBookButton3.visibility = View.INVISIBLE
        if(!data.isPriv || data.isOwner)
        {
            // they got a public profile or it's the owner's profile
            when (books.count) {
                1 -> {
                    // show book container
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    StartBookOnClick(books.book1, holder.mcvBookButton1)
                }
                2 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    StartBookOnClick(books.book1, holder.mcvBookButton1)
                    StartBookOnClick(books.book2, holder.mcvBookButton2)
                }
                3 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    holder.mcvBookButton3.visibility = View.VISIBLE
                    StartBookOnClick(books.book1, holder.mcvBookButton1)
                    StartBookOnClick(books.book2, holder.mcvBookButton2)
                    StartBookOnClick(books.book3, holder.mcvBookButton3)
                }
                else -> {
                    // you should not be here, how did you get here?? \_(x_x)_/
                    // you'll know you're here if nothing shows up in the book container
                }
            }
        }
        else
        {
            rvFeed.visibility = View.INVISIBLE
            tvInformUser.text = "@$username book's are private"
            tvInformUser.visibility = View.VISIBLE
        }

    }
    private fun StartBookOnClick(book: DisplayFeedBookInfo, button: MaterialCardView)
    {
        button.setOnClickListener {
            val showBookPopUp = ImprovedDisplayFeedBookInfoFragment.newInstance(book)
            showBookPopUp.show(activity.supportFragmentManager, "showPopUp")
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize the views in the item view
        val ivBook1 = itemView.findViewById<ImageView>(R.id.ivBook1)
        val ivBook2 = itemView.findViewById<ImageView>(R.id.ivBook2)
        val ivBook3 = itemView.findViewById<ImageView>(R.id.ivBook3)

        val mcvBookButton1 = itemView.findViewById<MaterialCardView>(R.id.mcvBook1)
        val mcvBookButton2 = itemView.findViewById<MaterialCardView>(R.id.mcvBook2)
        val mcvBookButton3 = itemView.findViewById<MaterialCardView>(R.id.mcvBook3)

    }

}

