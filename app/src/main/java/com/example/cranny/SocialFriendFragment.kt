package com.example.cranny

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
    private lateinit var etSearchBar: EditText

    private lateinit var mcvSearchButton: MaterialCardView
    private lateinit var mcvClearButton: MaterialCardView
    private lateinit var mcvFavFilterButton: MaterialCardView
    private lateinit var mcvSimilarFilterButton: MaterialCardView
    private lateinit var ivFavIcon: ImageView

    private var isShowingFavorite: Boolean = false
    private var isShowingSimilar: Boolean = false

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
        etSearchBar = fragmentView.findViewById(R.id.etSearchBar)
        mcvSearchButton = fragmentView.findViewById(R.id.mcvSearchButton)
        mcvClearButton = fragmentView.findViewById(R.id.mcvClearButton)
        mcvFavFilterButton = fragmentView.findViewById(R.id.mcvFavoriteFilterButton)
        mcvSimilarFilterButton = fragmentView.findViewById(R.id.mcvSimilarButton)
        ivFavIcon = fragmentView.findViewById(R.id.ivFavFilterButton)

        val database = FirebaseDatabase.getInstance()
        val profileRepo = ProfileRepository(database, FirebaseAuth.getInstance().uid!!)
        profileRepo.profileData.observe(viewLifecycleOwner, Observer { userProfile ->
            getFriendsLibraryData(Friend(userProfile.userId, userProfile.username, false))
        })
        profileRepo.stopProfileListener()

        return fragmentView
    }

    fun filterBooksByLastReadTime(
        bookList: MutableList<DisplayFeedBookInfo>,
        numBooks: Int
    ): MutableList<DisplayFeedBookInfo> {
        val filteredList = bookList.sortedByDescending { it.lLastReadTime }
            .take(numBooks)
            .toMutableList()

        return filteredList
    }

    private fun getFriendsLibraryData(user: Friend) {
        val database = FirebaseDatabase.getInstance()
        val userId = user.id // Get the current user's ID
        val repoFriendLibraryData = FriendsLibraryRepository(database, userId, requireContext())

        // Observe the profile data changes
        repoFriendLibraryData.isLibraryDataRead.observe(requireActivity(), Observer { isLibraryDataReady ->
            if(isLibraryDataReady)
            {
                // repoFriendLibraryData.mlFriendLibraryData // every friend's username, id, and books
                //order the list from newest to oldest
                if(repoFriendLibraryData.mlFriendLibraryData.size > 0) {
                    tvNoSocialFeed.visibility = View.INVISIBLE
                    // adapter can only display 6 books, so filter out the ones we dont wanna show
                    val mlNewest = repoFriendLibraryData.mlFriendLibraryData
                        .sortedByDescending { friendLibraryData -> friendLibraryData.friendLibrary.maxOfOrNull { it.lLastReadTime } }
                    var mlFriendDisplayData = mutableListOf<FriendAdapterData>()
                    for (mlFriendData in mlNewest) {
                        mlFriendDisplayData.add(
                            FriendAdapterData(
                                mlFriendData.friendUserID,
                                mlFriendData.friendUsername,
                                filterBooksByLastReadTime(mlFriendData.friendLibrary, 6),
                                mlFriendData.isFriendPrivate,
                                mlFriendData.isFavorite
                            )
                        )
                    }
                    var ml6SimilarBooks = mutableListOf<FriendAdapterData>()
                    for (mlFriendData in repoFriendLibraryData.mlFriendsWillSimilarBooks) {
                        ml6SimilarBooks.add(
                            FriendAdapterData(
                                mlFriendData.friendUserID,
                                mlFriendData.friendUsername,
                                filterBooksByLastReadTime(mlFriendData.friendLibrary, 6),
                                mlFriendData.isFriendPrivate,
                                mlFriendData.isFavorite
                            )
                        )
                    }
                    // attach the adapter
                    rvFriendFeed.layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                    ) // Sets the layout manager for the RecyclerView
                    rvFriendFeed.setHasFixedSize(true) // Optimizes performance by indicating that the item size in the RecyclerView is fixed
                    rvFriendFeed.adapter = FriendFragmentAdapter(
                        requireActivity() as DashboardActivity,
                        this,
                        requireContext(),
                        mlFriendDisplayData,
                        database,
                        resources,
                        user
                    )
                    allowSearch(mlFriendDisplayData, database, user)
                    allowClear(mlFriendDisplayData, database, user)
                    allowFavoriteFilter(mlFriendDisplayData, database, user)
                    allowSimilarFilter(mlFriendDisplayData, database, user, ml6SimilarBooks)
                }
                else tvNoSocialFeed.visibility = View.VISIBLE

            }
        })
    }

    private fun allowSimilarFilter(mlAvailableUsersToAdd: MutableList<FriendAdapterData>, database: FirebaseDatabase, user: Friend, mlSimilarBooks: MutableList<FriendAdapterData>)
    {
        mcvSimilarFilterButton.setOnClickListener {
            if(!isShowingSimilar)
            {
                // change the rv to show similar books
                rvFriendFeed.adapter = FriendFragmentAdapter(
                    requireActivity() as DashboardActivity,
                    this,
                    requireContext(),
                    mlSimilarBooks,
                    database,
                    resources,
                    user
                )
                rvFriendFeed.adapter?.notifyDataSetChanged()
                // are if currently displaying favorite friends
                if(isShowingFavorite)
                {
                    ivFavIcon.setImageResource(R.drawable.unfavorite_filter_icon)
                    isShowingFavorite = false
                }
                isShowingSimilar = true
            }
            else
            {
                // switch it back to the main list
                rvFriendFeed.adapter = FriendFragmentAdapter(
                    requireActivity() as DashboardActivity,
                    this,
                    requireContext(),
                    mlAvailableUsersToAdd,
                    database,
                    resources,
                    user
                )
                rvFriendFeed.adapter?.notifyDataSetChanged()
                isShowingSimilar = false
            }
        }
    }
    private fun allowSearch(mlAvailableUsersToAdd: MutableList<FriendAdapterData>, database: FirebaseDatabase, user: Friend) {
        mcvSearchButton.setOnClickListener {
            val strUserTheySearchedFor = etSearchBar.text.toString()
            if (etSearchBar.text.isNotBlank()) {
                val mlFilteredList = mlAvailableUsersToAdd.filter { friend ->
                    friend.username.contains(strUserTheySearchedFor, ignoreCase = true)
                }.toMutableList()

                rvFriendFeed.adapter = FriendFragmentAdapter(
                    requireActivity() as DashboardActivity,
                    this,
                    requireContext(),
                    mlFilteredList,
                    database,
                    resources,
                    user
                )
                if(isShowingFavorite)
                {
                    ivFavIcon.setImageResource(R.drawable.unfavorite_filter_icon)
                    isShowingFavorite = false
                }
                rvFriendFeed.adapter?.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Search bar is empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allowFavoriteFilter(mlAvailableUsersToAdd: MutableList<FriendAdapterData>, database: FirebaseDatabase, user: Friend) {
        mcvFavFilterButton.setOnClickListener {

            if(!isShowingFavorite)
            {
                ivFavIcon.setImageResource(R.drawable.favorite_filter_icon)
                val mlFilteredList = mlAvailableUsersToAdd.filter { friend ->
                    friend.isFavorite
                }.toMutableList()

                if(mlFilteredList.size > 0)
                {
                    rvFriendFeed.adapter = FriendFragmentAdapter(
                        requireActivity() as DashboardActivity,
                        this,
                        requireContext(),
                        mlFilteredList,
                        database,
                        resources,
                        user
                    )
                    rvFriendFeed.adapter?.notifyDataSetChanged()
                    if(isShowingSimilar) isShowingSimilar = false
                    isShowingFavorite = true
                }
                else
                {
                    Toast.makeText(requireContext(), "No favorite friends found", Toast.LENGTH_SHORT).show()
                    isShowingFavorite = false
                }
            }
            else
            {
                ivFavIcon.setImageResource(R.drawable.unfavorite_filter_icon)
                rvFriendFeed.adapter = FriendFragmentAdapter(
                    requireActivity() as DashboardActivity,
                    this,
                    requireContext(),
                    mlAvailableUsersToAdd,
                    database,
                    resources,
                    user
                )
                rvFriendFeed.adapter?.notifyDataSetChanged()
                isShowingFavorite = false
            }

        }
    }

    private fun allowClear(mlAvailableUsersToAdd: MutableList<FriendAdapterData>, database: FirebaseDatabase, user: Friend) {
        mcvClearButton.setOnClickListener {

            etSearchBar.text.clear()
            rvFriendFeed.adapter = FriendFragmentAdapter(
                requireActivity() as DashboardActivity,
                this,
                requireContext(),
                mlAvailableUsersToAdd,
                database,
                resources,
                user
            )
            if(isShowingFavorite)
            {
                ivFavIcon.setImageResource(R.drawable.unfavorite_filter_icon)
                isShowingFavorite = false
            }
        }
    }
}

class FriendFragmentAdapter(private val activity: DashboardActivity,private val owner: LifecycleOwner, val context: Context, private val mlFriends: MutableList<FriendAdapterData>,
                              private var database: FirebaseDatabase, private var resource: Resources, private val user: Friend
): RecyclerView.Adapter<FriendFragmentAdapter.MyViewHolder>(), SocialFriendProfileFragment.DialogListener
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

        holder.mcvFriendProfileButton.setOnClickListener {
            val showFriendProfile = SocialFriendProfileFragment.newInstance(mlFriends[position], user)
            showFriendProfile.setDialogListener(this) // Set the listener to the calling activity or fragment
            showFriendProfile.show(activity.supportFragmentManager, "showPopUp")

        }

        SetUpBooks(usernameText,mlFriends[position].isPrivateProfile ,mlFriends[position].mlBooksToDisplay, holder)
        if (position == mlFriends.size - 1)
        {
            // change bottom margin to be larger
            val layoutParams = holder.mcvFriendBox.layoutParams as ViewGroup.MarginLayoutParams
            val bottomMarginDp = 8
            val density = resource.displayMetrics.density
            val bottomMarginPx = (bottomMarginDp * density).toInt()
            layoutParams.bottomMargin = bottomMarginPx
            holder.mcvFriendBox.layoutParams = layoutParams
        }
    }
    override fun onDialogClosed(result: Friend) {
        if (result.isFavorite == true) {
            // we removed the friend
            // remove the friend from mlFriends and update the recycler view
            val iterator = mlFriends.iterator()
            while (iterator.hasNext()) {
                val friend = iterator.next()
                if (friend.id == result.id) {
                    iterator.remove()
                    notifyDataSetChanged() // Notify the adapter that the list has changed
                    break
                }
            }
        }
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
        var newHeight = 0
        if(!isPrivate)
        {
            // they got a public profile
            when (books.size) {
                0 -> {
                    // no book, sadge
                    holder.tvPrivateBooks.text = strEmpty
                    holder.tvPrivateBooks.visibility = View.VISIBLE
                    newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_1row)
                }
                1 -> {
                    // show book container
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_1row)
                }
                2 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    StartBookOnClick(books[1], holder.mcvBookButton2)
                    newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_1row)
                }
                3 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    holder.mcvBookButton3.visibility = View.VISIBLE
                    StartBookOnClick(books[0], holder.mcvBookButton1)
                    StartBookOnClick(books[1], holder.mcvBookButton2)
                    StartBookOnClick(books[2], holder.mcvBookButton3)
                    newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_1row)
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
                    newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_2row)
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
                    newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_2row)
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
                    newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_2row)
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
            newHeight = resource.getDimensionPixelSize(R.dimen.bookfriend_1row)
        }

        holder.mcvBookContainer.layoutParams.height = newHeight
        holder.mcvBookContainer.requestLayout()
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

        val mcvFriendBox = itemView.findViewById<MaterialCardView>(R.id.mcvFriendBox)
        val mcvBookContainer = itemView.findViewById<MaterialCardView>(R.id.mcvBooksContainer)
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