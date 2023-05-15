package com.example.cranny

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.Observer


class SocialFriendFragment : Fragment() {

    private lateinit var tvNoSocialFeed: TextView
    private lateinit var mcvFeedBorder: MaterialCardView
    private lateinit var rvFriendFeed: RecyclerView

    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_social_friend, container, false)

        tvNoSocialFeed = fragmentView.findViewById(R.id.tvNoSocialFeed)
        mcvFeedBorder = fragmentView.findViewById(R.id.mcvBorder)
        rvFriendFeed = fragmentView.findViewById(R.id.rvSocial)

        getFriendsLibraryData()

        return fragmentView
    }

    private fun getFriendsLibraryData() {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().uid!! // Get the current user's ID
        val repoFriendLibraryData = FriendsLibraryRepository(database, userId, requireContext())

        // Observe the profile data changes
        repoFriendLibraryData.isLibraryDataRead.observe(requireActivity(), Observer { isLibraryDataReady ->
            if(isLibraryDataReady)
            {
                // repoFriendLibraryData.mlFriendLibraryData // every friend's username, id, and books
                //order the list from newest to oldest
                val mlNewest = repoFriendLibraryData.mlFriendLibraryData
                    .filter { friendLibraryData -> friendLibraryData.friendLibrary.isNotEmpty() } // Filter out empty friend libraries
                    .sortedByDescending { friendLibraryData -> friendLibraryData.friendLibrary.maxOfOrNull { it.lLastReadTime } }
                // set up the list needed for the adapter, but only pass the first 6 books
                var mlFriendDisplayData = mutableListOf<FriendAdapterData>()
                var i = 0
                for(DisplayBook in mlNewest)
                {
                    mlFriendDisplayData.add(FriendAdapterData(DisplayBook.friendUserID, DisplayBook.friendUsername, DisplayBook.friendLibrary, DisplayBook.isFriendPrivate))
                    ++i
                    if(i == 6) break
                }
                // attach the adapter
                rvFriendFeed.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // Sets the layout manager for the RecyclerView
                rvFriendFeed.setHasFixedSize(true) // Optimizes performance by indicating that the item size in the RecyclerView is fixed
                rvFriendFeed.adapter = FriendFragmentAdapter(requireActivity() as TestSocialActivity,this,requireContext(), mlFriendDisplayData, database)

            }
        })
    }
}

class FriendFragmentAdapter(private val activity: TestSocialActivity,private val owner: LifecycleOwner, val context: Context, private val mlFriends: MutableList<FriendAdapterData>,
                              private var database: FirebaseDatabase): RecyclerView.Adapter<FriendFragmentAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_friends, parent, false)

        // Create and return a new ViewHolder with the inflated layout
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return mlFriends.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val usernameText = "@${mlFriends[position].username}"
        holder.tvUsername.text = usernameText // load username
        val profilePictureRepository = ProfilePictureRepository(database, mlFriends[position].id)
        profilePictureRepository.loadProfilePictureIntoImageView(holder.ivProfilePicture) // load profile picture

        // todo load book covers
        SetUpBooks(usernameText,mlFriends[position].isPrivateProfile ,mlFriends[position].mlBooksToDisplay, holder)


    }

    private fun SetUpBooks(username: String, isPrivate: Boolean, books: MutableList<DisplayFeedBookInfo>, holder: MyViewHolder)
    {
        holder.mcvBookButton1.visibility = View.INVISIBLE
        holder.mcvBookButton2.visibility = View.INVISIBLE
        holder.mcvBookButton3.visibility = View.INVISIBLE
        holder.mcvBookButton4.visibility = View.INVISIBLE
        holder.mcvBookButton5.visibility = View.INVISIBLE
        holder.mcvBookButton6.visibility = View.INVISIBLE
        holder.tvPrivateBooks.visibility = View.INVISIBLE
        val strPrivate = "${username}'s books are private"
        val strEmpty = "${username} has no books"
        if(!isPrivate)
        {
            // they got a public profile
            when (books.size) {
                0 -> {
                    // no book, sadge
                    holder.tvPrivateBooks.text = strEmpty
                    holder.tvPrivateBooks.visibility = View.VISIBLE
                }
                1 -> {
                    // show book container
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                }
                2 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    StartBookOnClick(books[1], holder.mcvBookButton2)
                }
                3 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    holder.mcvBookButton3.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    StartBookOnClick(books[1], holder.mcvBookButton2)
                    StartBookOnClick(books[2], holder.mcvBookButton3)
                }
                4 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    holder.mcvBookButton3.visibility = View.VISIBLE
                    holder.mcvBookButton4.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    StartBookOnClick(books[1], holder.mcvBookButton2)
                    StartBookOnClick(books[2], holder.mcvBookButton3)
                    StartBookOnClick(books[3], holder.mcvBookButton4)
                }
                5 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    holder.mcvBookButton3.visibility = View.VISIBLE
                    holder.mcvBookButton4.visibility = View.VISIBLE
                    holder.mcvBookButton5.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    StartBookOnClick(books[1], holder.mcvBookButton2)
                    StartBookOnClick(books[2], holder.mcvBookButton3)
                    StartBookOnClick(books[3], holder.mcvBookButton4)
                    StartBookOnClick(books[4], holder.mcvBookButton5)
                }
                6 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    holder.mcvBookButton3.visibility = View.VISIBLE
                    holder.mcvBookButton4.visibility = View.VISIBLE
                    holder.mcvBookButton5.visibility = View.VISIBLE
                    holder.mcvBookButton6.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    StartBookOnClick(books[1], holder.mcvBookButton2)
                    StartBookOnClick(books[2], holder.mcvBookButton3)
                    StartBookOnClick(books[3], holder.mcvBookButton4)
                    StartBookOnClick(books[4], holder.mcvBookButton5)
                    StartBookOnClick(books[5], holder.mcvBookButton6)
                }
                else -> {
                    // you should not be here, how did you get here?? \_(x_x)_/
                    // you'll know you're here if nothing shows up in the book container
                }
            }
        }
        else
        {
            // friend's profile is private so don't show any books
            holder.tvPrivateBooks.text = strPrivate
            holder.tvPrivateBooks.visibility = View.VISIBLE
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
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val ivProfilePicture = itemView.findViewById<ImageView>(R.id.ivProfilePicture)

        val ivBook1 = itemView.findViewById<ImageView>(R.id.ivBook1)
        val ivBook2 = itemView.findViewById<ImageView>(R.id.ivBook2)
        val ivBook3 = itemView.findViewById<ImageView>(R.id.ivBook3)
        val ivBook4 = itemView.findViewById<ImageView>(R.id.ivBook4)
        val ivBook5 = itemView.findViewById<ImageView>(R.id.ivBook5)
        val ivBook6 = itemView.findViewById<ImageView>(R.id.ivBook6)

        val mcvBookButton1 = itemView.findViewById<MaterialCardView>(R.id.mcvBook1)
        val mcvBookButton2 = itemView.findViewById<MaterialCardView>(R.id.mcvBook2)
        val mcvBookButton3 = itemView.findViewById<MaterialCardView>(R.id.mcvBook3)
        val mcvBookButton4 = itemView.findViewById<MaterialCardView>(R.id.mcvBook4)
        val mcvBookButton5 = itemView.findViewById<MaterialCardView>(R.id.mcvBook5)
        val mcvBookButton6 = itemView.findViewById<MaterialCardView>(R.id.mcvBook6)

        val mcvFriendProfileButton = itemView.findViewById<MaterialCardView>(R.id.mcvFriendProfileButton)

        val tvPrivateBooks = itemView.findViewById<TextView>(R.id.tvPrivateBooks)

    }

}