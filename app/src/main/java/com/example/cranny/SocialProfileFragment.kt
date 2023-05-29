package com.example.cranny

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SocialProfileFragment : Fragment() {

    // Declare UI Objects
    lateinit var ivProfilePicture: ImageView
    lateinit var tvUsername: TextView
    lateinit var tvDisplayName: TextView
    lateinit var tvBio: TextView
    lateinit var tvFriendsCount: TextView
    lateinit var tvBooksCount: TextView
    lateinit var cvBookButton: MaterialCardView
    lateinit var cvFriendButton: MaterialCardView
    lateinit var tvNoRecent: TextView
    lateinit var rvFeed: RecyclerView

    // Used to store what will displayed in the user feed
    private val userSocialFeed = ArrayList<SocialFeed>()

    private lateinit var database: FirebaseDatabase
    lateinit var username: String
    lateinit var userId: String
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_social_profile, container, false)

        // Initialize UI Objects
        ivProfilePicture = fragmentView.findViewById(R.id.ivProfilePicture)
        tvUsername = fragmentView.findViewById(R.id.tvProfileUsername)
        tvDisplayName = fragmentView.findViewById(R.id.tvProfileDisplayName)
        tvBio = fragmentView.findViewById(R.id.tvProfileBio)
        tvFriendsCount = fragmentView.findViewById(R.id.tvTotalFriends)
        tvBooksCount = fragmentView.findViewById(R.id.tvTotalBooks)
        cvBookButton = fragmentView.findViewById(R.id.cvBookCount)
        cvFriendButton = fragmentView .findViewById(R.id.cvFriendCount)
        tvNoRecent = fragmentView .findViewById(R.id.tvNoRecent)
        rvFeed = fragmentView.findViewById(R.id.rvSocial)

        val friendButton = fragmentView.findViewById<MaterialCardView>(R.id.cvFriendCount)
        friendButton.setOnClickListener{
            val i = Intent(requireContext(), MainActivity::class.java)
            startActivity(i)
        }

        // load user data from database into profile text views / image view
        //setUpSocialProfile()
        SetUpProfile()


        // Book On Click Listener
        cvBookButton.setOnClickListener {
            val i = Intent(requireContext(), LibraryActivity::class.java)
            startActivity(i)
        }

        return fragmentView
    }


    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                val profilePictureRepository = ProfilePictureRepository(database, userId)
                profilePictureRepository.updateProfilePicture(selectedImageUri, ivProfilePicture)
            }
        }
    }
    fun allowProfileChange(database: FirebaseDatabase, userId: String) {
        ivProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }
    }

    fun SetUpProfile()
    {
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance()
        val context = requireContext()
        val setupRepo = SetupProfileRepository(database, userId!!, true, context)
        setupRepo.isProfileReady.observe(requireActivity()) { isProfileReady ->
            if(isProfileReady)
            {
                username = setupRepo.profile.username
                tvUsername.text = "@${username}"
                tvBooksCount.text = setupRepo.profile.countBook.toString()
                tvFriendsCount.text = setupRepo.profile.countFriend.toString()
                tvDisplayName.text = setupRepo.profile.displayname
                tvBio.text = setupRepo.profile.bio

                // Load the profile picture from the database into the image view
                val profilePictureRepository = ProfilePictureRepository(database, userId)
                profilePictureRepository.loadProfilePictureIntoImageView(ivProfilePicture)
                allowProfileChange(database, setupRepo.profile.userId)

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
                    tvNoRecent.visibility = View.VISIBLE
                }
                else
                {
                    rvFeed.visibility = View.VISIBLE
                    tvNoRecent.visibility = View.INVISIBLE
                    var data = ProfileAdapterData(userId, username, displayBooks, setupRepo.profile.iPriv, true)
                    // set up recycle view
                    rvFeed.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    rvFeed.setHasFixedSize(true)
                    rvFeed.adapter = ProfileFragmentAdapter(
                        requireActivity() as DashboardActivity,
                        context,
                        data,
                        database,
                        resources
                    )
                }
            }
        }
    }

}

class ProfileFragmentAdapter(private val activity: DashboardActivity, val context: Context, private val data: ProfileAdapterData,
                            private var database: FirebaseDatabase, private var resource: Resources
): RecyclerView.Adapter<ProfileFragmentAdapter.MyViewHolder>()
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
        SetUpBooks(holder, data.bookGrouping[position])
    }

    private fun SetUpBooks(holder: MyViewHolder, books: ThreeDisplayBooks)
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
                    // load thumbnail
                    Glide.with(context).load(books.book1.strCoverURL).into(holder.ivBook1)
                    StartBookOnClick(books.book1, holder.mcvBookButton1)
                }
                2 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    // load thumbnails
                    Glide.with(context).load(books.book1.strCoverURL).into(holder.ivBook1)
                    Glide.with(context).load(books.book2.strCoverURL).into(holder.ivBook2)
                    StartBookOnClick(books.book1, holder.mcvBookButton1)
                    StartBookOnClick(books.book2, holder.mcvBookButton2)
                }
                3 -> {
                    // show book containers
                    holder.mcvBookButton1.visibility = View.VISIBLE
                    holder.mcvBookButton2.visibility = View.VISIBLE
                    holder.mcvBookButton3.visibility = View.VISIBLE
                    // load thumbnails
                    Glide.with(context).load(books.book1.strCoverURL).into(holder.ivBook1)
                    Glide.with(context).load(books.book2.strCoverURL).into(holder.ivBook2)
                    Glide.with(context).load(books.book3.strCoverURL).into(holder.ivBook3)
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