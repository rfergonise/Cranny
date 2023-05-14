package com.example.cranny

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cranny.network.googlebooks.RetrofitInstance
import com.example.cranny.network.googlebooks.apiKey
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap


// How to use each class?
//-----------------------------------------------------

// PROFILE CLASS

/* How to FETCH profile data?
val database = FirebaseDatabase.getInstance()
val profileRepo = ProfileRepository(database)
profileRepo.profileData.observe(this, Observer { userProfile ->
    val username: String = userProfile.username
})
profileRepo.stopProfileListener() // free the listener to stop memory leaks
*/

/* How to UPDATE profile data?
val database = FirebaseDatabase.getInstance()
val profileRepo = ProfileRepository(database)
val newUsername = "new_username"
val newDisplayName = "New Display Name"
val newPfpURL = "https://example.com/new_profile_pic.png"
val newUserId = "new_user_id"
val newBio = "This is my new bio"
val newFriendCount = 10
val newBookCount = 5
profileRepo.updateProfileData(newUsername, newDisplayName, newPfpURL, newUserId, newBio, newFriendCount, newBookCount)
*/

/* How to ADD profile data
val database = FirebaseDatabase.getInstance()
val profileRepo = ProfileRepository(database)
bookRepository.addUser(user)
*/

/* How to REMOVE profile data
val database = FirebaseDatabase.getInstance()
val profileRepo = ProfileRepository(database)
bookRepository.removeUser(user)
*/

//---------------------------------------------------------
// FRIEND CLASS

/* How to ADD friend data
val database = FirebaseDatabase.getInstance()
val friendRepo = FriendRepository(database)
val friend = Friend("friendId", "username")
friendRepo.addFriend(friend)
*/

/* How to REMOVE friend data
val database = FirebaseDatabase.getInstance()
val friendRepo = FriendRepository(database)
val friend = Friend("friendId", "username")
friendRepo.removeFriend(friend)
 */

/* How to FETCH friends?
val database = FirebaseDatabase.getInstance()
val friendRepo = FriendRepository(database, username, userId, this)
friendRepo.fetchFriends()
friendRepo.isFriendsReady.observe(this, Observer { isFriendsReady ->
    if(isFriendsReady)
    {
      // get the friend count by using friendRepo.FriendIds.size
      // get a friend by if(friendCount > 0) friendRepo.FriendIds[0]
    }
})
friendRepo.stopFriendListener() // free the listener to stop memory leaks
 */

//--------------------------------------------------------
// BOOK CLASS

/* How to FETCH library data?
val database = FirebaseDatabase.getInstance()
val bookRepository = BookRepository(database)
bookRepository.fetchBookData()
bookRepository.isBookDataReady.observe(this, Observer { isBookDataReady ->
    if(isBookDataReady)
    {
      // grab each book from bookRepository.Library
    }
})
bookRepository.stopBookListener() // free the listener to stop memory leaks
 */

/* How to ADD book data
val database = FirebaseDatabase.getInstance()
val bookRepository = BookRepository(database)
bookRepository.addBook(book)
*/

/* How to UPDATE book data
val database = FirebaseDatabase.getInstance()
val bookRepository = BookRepository(database)
// edit the book object's values, then pass it to this function
bookRepository.updateBookData(book)
*/

/* How to REMOVE book data
val database = FirebaseDatabase.getInstance()
val bookRepository = BookRepository(database)
bookRepository.removeBook(book)
*/

//-----------------------------------------------------------
// RECENTS CLASS

// How to FETCH recents data?
/*
val database = FirebaseDatabase.getInstance()
val recentRepository = RecentRepository(database)
recentRepository.fetchRecentData()
recentRepository.isRecentDataReady.observe(this, Observer { isRecentDataReady ->
 if(isRecentDataReady)
 {
      // grab each social feed from recentRepository.SocialFeeds
 }
})
*/

/* How to ADD recent data
val database = FirebaseDatabase.getInstance()
val recentRepository = RecentRepository(database)
recentRepository.addRecent(socialFeed)
*/

/* How to REMOVE recent data
val database = FirebaseDatabase.getInstance()
val recentRepository = RecentRepository(database)
recentRepository.removeRecent(socialFeed)
*/

/* How to UPDATE recent data
val database = FirebaseDatabase.getInstance()
val recentRepository = RecentRepository(database)
// edit the social feed object's values, then pass it to this function
recentRepository.updateRecent(socialFeed)
*/

//---------------------------------------------------------------

class ProfileRepository(private val database: FirebaseDatabase, private val userId: String)
{
    private val _profileData = MutableLiveData<User>()
    val profileData: LiveData<User>
        get() = _profileData

    private var listener: ValueEventListener? = null

    init
    {
        fetchProfileData()
    }

    private fun fetchProfileData()
    {
        // Firebase Path References
        val profileDataRef = database.getReference("UserData").child(userId).child("Profile")
        listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val username = dataSnapshot.child("Username").getValue(String::class.java) ?: ""
                val displayName = dataSnapshot.child("Name").getValue(String::class.java) ?: ""
                val userId = dataSnapshot.child("UserId").getValue(String::class.java) ?: ""
                val bio = dataSnapshot.child("Bio").getValue(String::class.java) ?: ""
                val friendCount = dataSnapshot.child("FriendCount").getValue(Long::class.java)?.toInt() ?: 0
                val bookCount = dataSnapshot.child("BookCount").getValue(Long::class.java)?.toInt() ?: 0

                val user = User(userId, displayName, username, friendCount, bookCount, bio)
                _profileData.postValue(user) // Update the LiveData object with the new data
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        profileDataRef.addListenerForSingleValueEvent(listener!!)
    }

    fun updateProfileData(username: String, displayName: String, userId: String, bio: String,friendCount: Int, bookCount: Int)
    {
            // gets the path reference to the profile
            val profileDataRef = database.getReference("UserData").child(userId).child("Profile")
            // creates a HashMap of the new data we are setting
            val profileData = HashMap<String, Any>()
            profileData["Username"] = username
            profileData["Name"] = displayName
            profileData["UserId"] = userId
            profileData["Bio"] = bio
            profileData["FriendCount"] = friendCount
            profileData["BookCount"] = bookCount
            // updates the profile's current HashMap with the new one
            profileDataRef.updateChildren(profileData)
    }

    fun addUser(user: User)
    {
        val userDataRef = database.getReference("UserData").child(user.userId).child("Profile")
        userDataRef.child("Bio").setValue(user.bio)
        userDataRef.child("BookCount").setValue(user.bookCount)
        userDataRef.child("FriendCount").setValue(user.friendCount)
        userDataRef.child("Name").setValue(user.name)
        userDataRef.child("UserId").setValue(user.userId)
        userDataRef.child("Username").setValue(user.username)
    }

    fun removeUser(username: String)
    {
        val curUser = FirebaseAuth.getInstance().currentUser // get the current user
        if (curUser != null && userId == curUser.uid)
        {
            // if the user isn't null

            // remove the user's saved profile picture
            val profilePictureRepository = ProfilePictureRepository(database, curUser.uid)
            profilePictureRepository.deleteProfilePicture()

            curUser.delete() // delete them from firebase
            val database = FirebaseDatabase.getInstance()
            val userRef = database.reference.child("UserData").child(curUser.uid) // get the path to their user data location in the database
            val usernameRef = database.reference.child("ServerData").child("Usernames").child(username) // get the path to their username in the taken username list

            usernameRef.removeValue() // clear their information from the database
            userRef.removeValue() // clear the username from the taken username list

        }
    }

    fun stopProfileListener()
    {
        listener?.let {
            val profileDataRef = database.getReference("UserData").child(userId)
                .child("Profile")
            profileDataRef.removeEventListener(it)
            listener = null
        }
    }
}

class FriendRepository(private val database: FirebaseDatabase, private val username: String,private val id: String, private val owner: LifecycleOwner)
{
    //private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    public var FriendIds = mutableListOf<Friend>()
    private val _isFriendReady = MutableLiveData<Boolean>()
    val isFriendsReady: LiveData<Boolean>
        get() = _isFriendReady
    private var listener: ValueEventListener? = null

    fun fetchFriends()
    {
        // Firebase Path References
        val friendDataRef = database.getReference("UserData").child(id).child("Friends")
        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isFriendReady.postValue(false) // inform the caller the list is not ready
                for (friend in dataSnapshot.children)
                {
                    val temp_friend = Friend(friend.child("id").value as? String ?: "", friend.child("username").value as? String ?: "",
                    friend.child("isFavorite").value as? Boolean ?: false)
                    FriendIds.add(temp_friend)
                }
                _isFriendReady.postValue(true) // inform the caller we have filled the list with each book
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        friendDataRef.addListenerForSingleValueEvent(listener!!)
    }

    // Adds whatever friendId that is passed in to the user's friend list
    fun addFriend(friend: Friend)
    {
        // add the friend to the user's friend list
        val userDataRef = database.getReference("UserData").child(id).child("Friends")
        userDataRef.child(friend.id).child("id").setValue(friend.id)
        userDataRef.child(friend.id).child("username").setValue(friend.username)
        userDataRef.child(friend.id).child("isFavorite").setValue(false)

        // update the user's friend count
        val userProfileRepo = ProfileRepository(database, id)
        userProfileRepo.profileData.observe(owner) { userProfile ->
            var friendCount = userProfile.friendCount
            friendCount++
            userProfileRepo.updateProfileData(userProfile.username, userProfile.name, userProfile.userId, userProfile.bio, friendCount, userProfile.bookCount)
            userProfileRepo.stopProfileListener()
        }

        // add the user to the friend's friend list
        val friendDataRef = database.getReference("UserData").child(friend.id).child("Friends")
        friendDataRef.child(id).child("id").setValue(id)
        friendDataRef.child(id).child("username").setValue(username)
        friendDataRef.child(id).child("isFavorite").setValue(false)

        // update the friend's friend count
        val friendProfileRepo = ProfileRepository(database, friend.id)
        friendProfileRepo.profileData.observe(owner) { friendProfile ->
            var friendCount = friendProfile.friendCount
            friendCount++
            friendProfileRepo.updateProfileData(friendProfile.username, friendProfile.name, friendProfile.userId, friendProfile.bio, friendCount, friendProfile.bookCount)
            friendProfileRepo.stopProfileListener()
        }
    }

    // Searches the user's friend list and removes the passed in friendId from it
    fun removeFriend(friend: Friend) {

        // remove the friend from the user's friend list
        val userDataRef = database.getReference("UserData").child(id).child("Friends").child(friend.id)
        userDataRef.removeValue()

        // update the user's friend count
        val userProfileRepo = ProfileRepository(database, id)
        userProfileRepo.profileData.observe(owner) { userProfile ->
            var friendCount = userProfile.friendCount
            friendCount--
            userProfileRepo.updateProfileData(userProfile.username, userProfile.name, userProfile.userId, userProfile.bio, friendCount, userProfile.bookCount)
            userProfileRepo.stopProfileListener()
        }

        // remove the user from the friend's friend list
        val friendDataRef = database.getReference("UserData").child(friend.id).child("Friends").child(id)
        friendDataRef.removeValue()

        // update the friend's friend count
        val friendProfileRepo = ProfileRepository(database, friend.id)
        friendProfileRepo.profileData.observe(owner) { friendProfile ->
            var friendCount = friendProfile.friendCount
            friendCount--
            friendProfileRepo.updateProfileData(friendProfile.username, friendProfile.name, friendProfile.userId, friendProfile.bio, friendCount, friendProfile.bookCount)
            friendProfileRepo.stopProfileListener()
        }

    }

    fun updateFavoriteStatus(friend: Friend)
    {
        val userDataRef = database.getReference("UserData").child(id).child("Friends")
        userDataRef.child(friend.id).child("isFavorite").setValue(friend.isFavorite)
    }

    fun stopFriendListener()
    {
        listener?.let {
            val friendDataRef = database.getReference("UserData").child(id).child("Friends")
            friendDataRef.removeEventListener(it)
            listener = null
        }
    }
}

class BookRepository(private val database: FirebaseDatabase, private val user: Friend)
{
    public var Library = mutableListOf<Book>()
    private val _isBookDataReady = MutableLiveData<Boolean>()
    val isBookDataReady: LiveData<Boolean>
        get() = _isBookDataReady

    private var listener: ValueEventListener? = null

    init
    {
        fetchBookData()
    }

    fun removeBook(book: Book, owner:LifecycleOwner) {
       val database = FirebaseDatabase.getInstance()
        val bookRef = database.reference.child("UserData").child(user.id).child("Books").child(book.id)
        bookRef.removeValue()

        // update user's book count
        val userProfileRepo = ProfileRepository(database, user.id)
        userProfileRepo.profileData.observe(owner) { userProfile ->
            var bookCount = userProfile.bookCount
            bookCount--
            userProfileRepo.updateProfileData(userProfile.username, userProfile.name, userProfile.userId, userProfile.bio, userProfile.friendCount, bookCount)
            userProfileRepo.stopProfileListener()
        }

        // remove the book from the user's friends' recent data
        val recentRepository = RecentRepository(database, user.username, mutableListOf())
        recentRepository.removeRecent(SocialFeed(book.id, book.title, book.authorNames!!, book.userFinished, book.pageCount.toString(),
        book.thumbnail!!, book.lastReadDate!!, book.lastReadTime!!, user.username, book.mainCharacters!!, book.journalEntry!!, book.purchasedFrom!!, book.genres!!, book.tags!!, book.starRating!!,
        book.totalPageCount!!, book.totalPagesRead!!), owner)

    }

    fun fetchBookData()
    {
        // Firebase Path References
        val profileDataRef = database.getReference("UserData").child(user.id).child("Books")
        Library.clear()
        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isBookDataReady.postValue(false) // inform the caller the list is not ready
                for (bookSnapshot in dataSnapshot.children)
                {
                    val doesStarRatingExist: Boolean = bookSnapshot.hasChild("StarRating")
                    val type: String = "StarRating value type: ${bookSnapshot.child("StarRating").value?.javaClass}"
                    val value: String = "StarRating value: ${bookSnapshot.child("StarRating").value}"
                    val AuthorNames = bookSnapshot.child("AuthorNames").value as? String ?: ""

                    val Description = bookSnapshot.child("Description").value as? String ?: ""
                    val EndDate = bookSnapshot.child("EndDate").value as? String ?: ""
                    val Genres = bookSnapshot.child("Genres").value as? String ?: ""
                    val Id = bookSnapshot.child("Id").value as? String ?: ""
                    val JournalEntry = bookSnapshot.child("JournalEntry").value as? String ?: ""
                    val LastReadDate = bookSnapshot.child("LastReadDate").value as? Long ?: 0
                    val LastReadTime = bookSnapshot.child("LastReadTime").value as? Long ?: 0
                    val MainCharacters = bookSnapshot.child("MainCharacters").value as? String ?: ""
                    val PublicationDate = bookSnapshot.child("PublicationDate").value as? String ?: ""
                    val Publisher = bookSnapshot.child("Publisher").value as? String ?: ""
                    val PurchaseFrom = bookSnapshot.child("PurchaseFrom").value as? String ?: ""
                    val Tags = bookSnapshot.child("Tags").value as? String ?: ""
                    val Thumbnail = bookSnapshot.child("Thumbnail").value as? String ?: ""
                    val Title = bookSnapshot.child("Title").value as? String ?: ""
                    val userProgress = bookSnapshot.child("UserProgress").value as? Long
                    val UserProgressInt = userProgress?.toInt()
                    val StarRating = bookSnapshot.child("StarRating").getValue(Double::class.java)
                    val StarRatingFloat = StarRating?.toFloat()
                    val UserFinished = bookSnapshot.child("UserFinished").value as? Boolean ?: false
                    val IsFavorite = bookSnapshot.child("IsFavorite").value as? Boolean ?: false
                    val StartDate = bookSnapshot.child("StartDate").value as? String ?: ""
                    val pageCount = bookSnapshot.child("PageCount").value as? Long
                    val pageCountInt = pageCount?.toInt() ?: 0
                    val PrevReadCount = bookSnapshot.child("PrevReadCount").value as? Long
                    val PrevReadCountInt = PrevReadCount?.toInt() ?: 0
                    val TotalPageCount = bookSnapshot.child("TotalPageCount").value as? Int ?: 0
                    val TotalPageRead = bookSnapshot.child("TotalPageRead").value as? Int ?: 0

                    var book: Book = Book(Id, Title, AuthorNames, PublicationDate, StarRatingFloat, Publisher, Description, pageCountInt, Thumbnail,
                    JournalEntry, UserProgressInt, UserFinished, IsFavorite, PurchaseFrom, MainCharacters, Genres, Tags, LastReadDate, LastReadTime,
                    PrevReadCountInt, StartDate, EndDate, TotalPageCount!!, TotalPageRead!!)
                    Library.add(book)
                }
                _isBookDataReady.postValue(true) // inform the caller we have filled the list with each book
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        profileDataRef.addListenerForSingleValueEvent(listener!!)
    }

    fun updateBookData(book: Book)
    {
        // todo update the update function to match new data types
        // gets the path reference to the user's book we are updating
        val currentUser = FirebaseAuth.getInstance().currentUser
        val bookDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Books").child(book.id)
        // creates a HashMap of the new data we are setting
        val bookData = HashMap<String, Any>()
        bookData["AuthorNames"] = book.authorNames.toString()
        bookData["Description"] = book.description.toString()
        bookData["EndDate"] = book.endDate
        bookData["Genres"] = book.genres.toString()
        bookData["Id"] = book.id
        bookData["JournalEntry"] = book.journalEntry.toString()
        bookData["LastReadDate"] = book.lastReadDate!!
        bookData["LastReadTime"] = book.lastReadTime!!
        bookData["MainCharacters"] = book.mainCharacters.toString()
        bookData["PageCount"] = book.pageCount!!.toInt()
        bookData["PrevReadCount"] = book.prevReadCount.toString()
        bookData["PublicationDate"] = book.publicationDate.toString()
        bookData["Publisher"] = book.publisher.toString()
        bookData["PurchaseFrom"] = book.purchasedFrom.toString()
        bookData["StarRating"] = book.starRating!!.toInt()
        bookData["StartDate"] = book.startDate
        bookData["Tags"] = book.tags.toString()
        bookData["Thumbnail"] = book.thumbnail.toString()
        bookData["Title"] = book.title
        bookData["UserFinished"] = book.userFinished
        bookData["UserProgress"] = book.userProgress!!.toInt()
        bookData["IsFavorite"] = book.isFav!!
        //val TotalPageCount = bookSnapshot.child("TotalPageCount").value as? Int
        //val TotalPageRead = bookSnapshot.child("TotalPageRead").value as? Int

        // updates the book's current HashMap with the new one
        bookDataRef.updateChildren(bookData)
    }

    fun addBook(book: Book, owner: LifecycleOwner)
    {
        val bookDataRef = database.getReference("UserData").child(user.id).child("Books")
        bookDataRef.child(book.id).child("AuthorNames").setValue(book.authorNames)
        bookDataRef.child(book.id).child("Description").setValue(book.description)
        bookDataRef.child(book.id).child("EndDate").setValue(book.endDate)
        bookDataRef.child(book.id).child("Genres").setValue(book.genres)
        bookDataRef.child(book.id).child("Id").setValue(book.id)
        bookDataRef.child(book.id).child("JournalEntry").setValue(book.journalEntry)
        bookDataRef.child(book.id).child("LastReadDate").setValue(book.lastReadDate)
        bookDataRef.child(book.id).child("LastReadTime").setValue(book.lastReadTime)
        bookDataRef.child(book.id).child("MainCharacters").setValue(book.mainCharacters)
        bookDataRef.child(book.id).child("PageCount").setValue(book.pageCount)
        bookDataRef.child(book.id).child("PrevReadCount").setValue(book.prevReadCount)
        bookDataRef.child(book.id).child("PublicationDate").setValue(book.publicationDate)
        bookDataRef.child(book.id).child("Publisher").setValue(book.publisher)
        bookDataRef.child(book.id).child("PurchaseFrom").setValue(book.purchasedFrom)
        bookDataRef.child(book.id).child("StarRating").setValue(book.starRating)
        bookDataRef.child(book.id).child("StartDate").setValue(book.startDate)
        bookDataRef.child(book.id).child("Tags").setValue(book.tags)
        bookDataRef.child(book.id).child("Thumbnail").setValue(book.thumbnail)
        bookDataRef.child(book.id).child("Title").setValue(book.title)
        bookDataRef.child(book.id).child("UserFinished").setValue(book.userFinished)
        bookDataRef.child(book.id).child("UserProgress").setValue(book.userProgress)
        bookDataRef.child(book.id).child("IsFavorite").setValue(book.isFav)
        bookDataRef.child(book.id).child("TotalPageCount").setValue(book.totalPageCount)
        bookDataRef.child(book.id).child("TotalPageRead").setValue(book.totalPagesRead)

        // update user's book count
        val userProfileRepo = ProfileRepository(database, user.id)
        userProfileRepo.profileData.observe(owner) { userProfile ->
            var bookCount = userProfile.bookCount
            bookCount++
            userProfileRepo.updateProfileData(userProfile.username, userProfile.name, userProfile.userId, userProfile.bio, userProfile.friendCount, bookCount)
            userProfileRepo.stopProfileListener()
        }

    }

    fun updateFavoriteStatus(book: Book)
    {
        val userDataRef = database.getReference("UserData").child(user.id).child("Books")
        userDataRef.child(book.id).child("IsFavorite").setValue(book.isFav)
    }

    fun stopBookListener()
    {
        listener?.let {
            val profileDataRef = database.getReference("UserData").child(user.id)
                .child("Books")
            profileDataRef.removeEventListener(it)
            listener = null
        }
    }

    fun clearUserLibrary(owner: LifecycleOwner) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            for (book in Library) {
                removeBook(book, owner)
            }
            // Signal that the user library has been cleared
            _isBookDataReady.postValue(true)
        } else {
            Log.e("BookRepository", "User ID is null. Cannot clear the library.")
            _isBookDataReady.postValue(false)
        }
    }

    //suspend function because network requests take so long, we put it in a suspend function and access in a corusinScope
     suspend fun getBookDetails(id: String): Book? {
        val response = RetrofitInstance.googleBooksApi.getBookDetails(id, apiKey)
        if (response.isSuccessful) {
            val bookDetailsResponse = response.body()
            /*return bookDetailsResponse?.let {
                Book(
                    id = UUID.randomUUID().toString(),
                    title = it.volumeInfo.title,
                    authorNames = it.volumeInfo.authors.toString(),
                    publicationDate = it.volumeInfo.publishedDate,
                    starRating = 0f,
                    publisher = it.volumeInfo.publisher,
                    description = it.volumeInfo.description,
                    pageCount = it.volumeInfo.pageCount,
                    thumbnail = it.volumeInfo.imageLinks?.thumbnail,
                    journalEntry = "",
                    userProgress = 0,
                    userFinished = false,
                    startDate = "",
                    endDate = "",
                    prevReadCount = 0,
                    purchasedFrom = "",
                    mainCharacters = "",
                    genres = it.volumeInfo.categories.toString(),
                    tags = "",
                    lastReadDate = 0,
                    lastReadTime = 0,
                    isFav = false,
                    totalPageCount = 0,
                    totalPagesRead = 0
                )
            }*/
            return Book(
                id = UUID.randomUUID().toString(),
                title = bookDetailsResponse?.volumeInfo?.title!!,
                authorNames = bookDetailsResponse?.volumeInfo?.authors?.toString(),
                publicationDate = bookDetailsResponse?.volumeInfo?.publishedDate,
                starRating = 0f,
                publisher = bookDetailsResponse?.volumeInfo?.publisher,
                description = bookDetailsResponse?.volumeInfo?.description,
                pageCount = bookDetailsResponse?.volumeInfo?.pageCount,
                thumbnail = bookDetailsResponse?.volumeInfo?.imageLinks?.thumbnail,
                journalEntry = "",
                userProgress = 0,
                userFinished = false,
                startDate = "",
                endDate = "",
                prevReadCount = 0,
                purchasedFrom = "",
                mainCharacters = "",
                genres = bookDetailsResponse?.volumeInfo?.categories?.toString(),
                tags = "",
                lastReadDate = 0,
                lastReadTime = 0,
                isFav = false,
                totalPageCount = 0,
                totalPagesRead = 0
            )
        }
        return null
    }
}

class RecentRepository(private val database: FirebaseDatabase, private val username: String, private val friendList: MutableList<Friend>)
{
    public var SocialFeeds = mutableListOf<SocialFeed>()
    private val _isRecentDataReady = MutableLiveData<Boolean>()
    val isRecentDataReady: LiveData<Boolean>
        get() = _isRecentDataReady

    private var listener: ValueEventListener? = null

    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    init
    {
        //fetchRecentData()
    }

    fun fetchRecentData()
    {
        // Firebase Path References
        val profileDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Recents")
        SocialFeeds.clear()
         listener = object : ValueEventListener
         {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isRecentDataReady.postValue(false) // inform the caller the list is not ready
                for (bookSnapshot in dataSnapshot.children)
                {
                    val BookAuthor = bookSnapshot.child("BookAuthor").value as String
                    val BookCoverURL = bookSnapshot.child("BookCoverURL").value as String
                    val BookTitle = bookSnapshot.child("BookTitle").value as String
                    val DateRead = bookSnapshot.child("DateRead").value as Long
                    val Id = bookSnapshot.child("Id").value as String
                    val IsBookComplete = bookSnapshot.child("IsBookComplete").value as Boolean
                    val TimeRead = bookSnapshot.child("TimeRead").value as Long
                    val Username = bookSnapshot.child("Username").value as String
                    val Status = bookSnapshot.child("Status").value as String
                    val MainCharacters = bookSnapshot.child("Characters").value as String
                    val Log = bookSnapshot.child("Log").value as String
                    val PurchaseFrom = bookSnapshot.child("PurchaseFrom").value as String
                    val Genres = bookSnapshot.child("Genres").value as String
                    val Tags = bookSnapshot.child("Tags").value as String
                    val starRatingObj = bookSnapshot.child("StarRating").value
                    val starRating = if (starRatingObj != null) {
                        if (starRatingObj is Float) {
                            starRatingObj
                        } else {
                            starRatingObj.toString().toFloatOrNull() ?: 0f
                        }
                    } else {
                        0f
                    }
                    val TotalPageCount = bookSnapshot.child("TotalPageCount").value as? Int ?: 0
                    val TotalPageRead = bookSnapshot.child("TotalPageRead").value as? Int ?: 0

                    SocialFeeds.add(SocialFeed(Id, BookTitle, BookAuthor, IsBookComplete, Status, BookCoverURL, DateRead, TimeRead, Username,
                        MainCharacters, Log, PurchaseFrom, Genres, Tags, starRating, TotalPageCount!!, TotalPageRead!!))
                }
                _isRecentDataReady.postValue(true) // inform the caller we have filled the list with each recent book
            }
            override fun onCancelled(error: DatabaseError) { }
         }
         profileDataRef.addListenerForSingleValueEvent(listener!!)
    }

    // Adds whatever SocialFeed that is passed in, to the user's recents data
    fun addRecent(socialFeed: SocialFeed)
    {
        for(friend in friendList)
        {
            val recentDataRef = database.getReference("UserData").child(friend.id).child("Recents")
            recentDataRef.child(socialFeed.id).child("BookAuthor").setValue(socialFeed.bookAuthor)
            recentDataRef.child(socialFeed.id).child("BookCoverURL").setValue(socialFeed.bookCoverURL)
            recentDataRef.child(socialFeed.id).child("BookTitle").setValue(socialFeed.bookTitle)
            recentDataRef.child(socialFeed.id).child("DateRead").setValue(socialFeed.lastReadDate)
            recentDataRef.child(socialFeed.id).child("Id").setValue(socialFeed.id)
            recentDataRef.child(socialFeed.id).child("IsBookComplete").setValue(socialFeed.isBookComplete)
            recentDataRef.child(socialFeed.id).child("TimeRead").setValue(socialFeed.lastReadTime)
            recentDataRef.child(socialFeed.id).child("Username").setValue(socialFeed.username)
            recentDataRef.child(socialFeed.id).child("Status").setValue(socialFeed.status)
            recentDataRef.child(socialFeed.id).child("Characters").setValue(socialFeed.mainCharacters)
            recentDataRef.child(socialFeed.id).child("Log").setValue(socialFeed.journalEntry)
            recentDataRef.child(socialFeed.id).child("PurchaseFrom").setValue(socialFeed.purchasedFrom)
            recentDataRef.child(socialFeed.id).child("Genres").setValue(socialFeed.genres)
            recentDataRef.child(socialFeed.id).child("Tags").setValue(socialFeed.tags)
            recentDataRef.child(socialFeed.id).child("StarRating").setValue(socialFeed.starRating)
        }
    }

    // Searches the user's recents list and removes the passed in SocialFeed from it
    fun removeRecent(socialFeed: SocialFeed, owner: LifecycleOwner) {

        // go through each friend and remove the book from their recent list
        val friendRepo = FriendRepository(database, username, currentUser!!.uid, owner)
        friendRepo.fetchFriends()
        friendRepo.isFriendsReady.observe(owner, Observer { isFriendsReady ->
            if(isFriendsReady)
            {
                val friendCount =  friendRepo.FriendIds.size
                if(friendCount > 0)
                {
                    for(friend in friendRepo.FriendIds)
                    {
                        val recentBookRef = database.getReference("UserData").child(friend.id).child("Recents").child(socialFeed.id)
                        recentBookRef.removeValue()
                    }
                }
            }
        })
        friendRepo.stopFriendListener()
    }

    fun stopRecentListener()
    {
        listener?.let {
            val profileDataRef = database.getReference("UserData").child(currentUser!!.uid)
                .child("Recents")
            profileDataRef.removeEventListener(it)
            listener = null
        }
    }


}
class ServerRepository(private val database: FirebaseDatabase)
{
    public var Users = mutableListOf<Friend>()
    private val _isUserListReady = MutableLiveData<Boolean>()
    val isUserListReady: LiveData<Boolean>
        get() = _isUserListReady

    private var listener: ValueEventListener? = null

    init
    {
        fetchUsers()
    }

    // UserDirectory
    fun addUser(friend: Friend)
    {
        val usernameRef = database.getReference("ServerData").child("UserList")
        usernameRef.child(friend.username).child("Username").setValue(friend.username)
        usernameRef.child(friend.username).child("Id").setValue(friend.id)
    }

    fun removeUser(friend: Friend)
    {
        val friendDataRef = database.getReference("ServerData").child("UserList").child(friend.username)
        friendDataRef.removeValue()
    }

    fun updateUser(friendOld: Friend, friendNew: Friend)
    {
        removeUser(friendOld)
        addUser(friendNew)
    }

    fun fetchUsers() {
        val userRef = database.getReference("ServerData").child("UserList")

        listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Users.clear()
                _isUserListReady.postValue(false)
                for (user in dataSnapshot.children) {
                    val username = user.child("Username").value as? String
                    val id = user.child("Id").value as? String
                    if (username != null && id != null) {
                        Users.add(Friend(id, username, false))
                    }
                }
                _isUserListReady.postValue(true)
            }

            override fun onCancelled(error: DatabaseError) { }
        }

        userRef.addListenerForSingleValueEvent(listener!!)
    }

    fun stopUserListener()
    {
        listener?.let {
            val userRef = database.getReference("ServerData").child("UserList")
            userRef.removeEventListener(it)
            listener = null
        }
    }
}
class ProfilePictureRepository(private val database: FirebaseDatabase, val userId: String)
{
    private val profileRef = database.getReference("UserData/${userId}/Profile/ProfilePictureURI")
    private val storageRef = FirebaseStorage.getInstance().reference.child("UserData/ProfilePictures/${userId}")
    fun uploadProfilePicture(imageUri: Uri?)
    {
        // Upload the image to Firebase Storage
        storageRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
            // Get the download URL of the uploaded image
            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl?.toString()

            // Save the download URL to the Realtime Database
            profileRef.setValue(downloadUrl).addOnSuccessListener {
                // Image URI saved successfully
            }.addOnFailureListener { exception ->
                // Handle the error
            }
        }.addOnFailureListener { exception ->
            // Handle the error
        }
    }

    fun loadProfilePictureIntoImageView(imageView: ImageView)
    {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef = storageRef.child("UserData/ProfilePictures/${userId}")

        // Get the download URL for the image
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the image using Picasso
            Picasso.get().load(uri.toString()).into(imageView)
        }.addOnFailureListener { exception ->
            // Handle any errors
        }
    }

    fun deleteProfilePicture()
    {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef = storageRef.child("UserData/ProfilePictures/${userId}")
        imageRef.delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
            }
    }
}

class FriendRequestRepository(private val database: FirebaseDatabase, private val id: String)
{
    public var RequestedUsers = mutableListOf<Friend>()
    private val _isFriendRequestReady = MutableLiveData<Boolean>()
    val isFriendsReady: LiveData<Boolean>
        get() = _isFriendRequestReady
    private var listener: ValueEventListener? = null

    fun fetchFriendRequests()
    {
        // Firebase Path References
        val friendDataRef = database.getReference("UserData").child(id).child("FriendRequests")
        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isFriendRequestReady.postValue(false) // inform the caller the list is not ready
                for (friend in dataSnapshot.children)
                {
                    val temp_friend = Friend(friend.child("id").value as? String ?: "", friend.child("username").value as? String ?: "", false)
                    RequestedUsers.add(temp_friend)
                }
                _isFriendRequestReady.postValue(true) // inform the caller we have filled the list with each book
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        friendDataRef.addListenerForSingleValueEvent(listener!!)
    }

    // Adds whatever friendId that is passed in to the user's friend list
    fun addFriendRequest(friend: Friend)
    {
        val friendDataRef = database.getReference("UserData").child(id).child("FriendRequests")
        friendDataRef.child(friend.id).child("id").setValue(friend.id)
        friendDataRef.child(friend.id).child("username").setValue(friend.username)
    }

    // Searches the user's friend list and removes the passed in friendId from it
    fun removeFriendRequest(friend: Friend)
    {

        val friendDataRef = database.getReference("UserData").child(id).child("FriendRequests").child(friend.id)
        friendDataRef.removeValue()
    }

    fun stopFriendRequestListener()
    {
        listener?.let {
            val friendDataRef = database.getReference("UserData").child(id).child("Friends")
            friendDataRef.removeEventListener(it)
            listener = null
        }
    }
}
class RequestedRepository(private val database: FirebaseDatabase, private val id: String)
{
    public var RequestedUsers = mutableListOf<Friend>()
    private val _isFriendRequestReady = MutableLiveData<Boolean>()
    val isFriendsReady: LiveData<Boolean>
        get() = _isFriendRequestReady
    private var listener: ValueEventListener? = null

    fun fetchRequestedUsers()
    {
        // Firebase Path References
        val friendDataRef = database.getReference("UserData").child(id).child("SentFriendRequestToTheseUsers")
        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isFriendRequestReady.postValue(false) // inform the caller the list is not ready
                for (friend in dataSnapshot.children)
                {
                    val temp_friend = Friend(friend.child("id").value as? String ?: "", friend.child("username").value as? String ?: "", false)
                    RequestedUsers.add(temp_friend)
                }
                _isFriendRequestReady.postValue(true) // inform the caller we have filled the list with each book
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        friendDataRef.addListenerForSingleValueEvent(listener!!)
    }

    // Adds whatever friendId that is passed in to the user's friend list
    fun addRequestedUser(friend: Friend)
    {
        val friendDataRef = database.getReference("UserData").child(id).child("SentFriendRequestToTheseUsers")
        friendDataRef.child(friend.id).child("id").setValue(friend.id)
        friendDataRef.child(friend.id).child("username").setValue(friend.username)
    }

    // Searches the user's friend list and removes the passed in friendId from it
    fun removeRequestedUser(friend: Friend)
    {

        val friendDataRef = database.getReference("UserData").child(id).child("SentFriendRequestToTheseUsers").child(friend.id)
        friendDataRef.removeValue()
    }

    fun stopRequestedUsersListener()
    {
        listener?.let {
            val friendDataRef = database.getReference("UserData").child(id).child("Friends")
            friendDataRef.removeEventListener(it)
            listener = null
        }
    }
}


