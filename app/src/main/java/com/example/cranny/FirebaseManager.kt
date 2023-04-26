package com.example.cranny

import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


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
val friendRepo = FriendRepository(database)
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
        val curUser = FirebaseAuth.getInstance().currentUser // get the current user
        if (curUser != null && userId == curUser.uid) {
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

class FriendRepository(private val database: FirebaseDatabase)
{
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    public var FriendIds = mutableListOf<Friend>()
    private val _isFriendReady = MutableLiveData<Boolean>()
    val isFriendsReady: LiveData<Boolean>
        get() = _isFriendReady
    private var listener: ValueEventListener? = null

    fun fetchFriends()
    {
        // Firebase Path References
        val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Friends")
        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isFriendReady.postValue(false) // inform the caller the list is not ready
                for (friend in dataSnapshot.children)
                {
                    val temp_friend = Friend(friend.child("id").value as? String ?: "", friend.child("username").value as? String ?: "")
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
        val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Friends")
        friendDataRef.child(friend.id).child("id").setValue(friend.id)
        friendDataRef.child(friend.id).child("username").setValue(friend.username)
    }

    // Searches the user's friend list and removes the passed in friendId from it
    fun removeFriend(friend: Friend) {
        val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Friends").child(friend.id)
        friendDataRef.removeValue()
    }

    fun stopFriendListener()
    {
        listener?.let {
            val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Friends")
            friendDataRef.removeEventListener(it)
            listener = null
        }
    }
}

class BookRepository(private val database: FirebaseDatabase)
{
    public var Library = mutableListOf<Book>()
    private val _isBookDataReady = MutableLiveData<Boolean>()
    val isBookDataReady: LiveData<Boolean>
        get() = _isBookDataReady

    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var listener: ValueEventListener? = null

    init
    {
        fetchBookData()
    }

    fun removeBook(book: Book)
    {
        val bookDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Books")
        bookDataRef.orderByValue().equalTo(book.id).addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                for (childSnapshot in dataSnapshot.children)
                {
                    if (childSnapshot.value == book.id)
                    {
                        childSnapshot.ref.removeValue()
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) { }
        })
    }

    fun fetchBookData()
    {
        // Firebase Path References
        val profileDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Books")
        Library.clear()
        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isBookDataReady.postValue(false) // inform the caller the list is not ready
                for (bookSnapshot in dataSnapshot.children)
                {
                    val AuthorNames = bookSnapshot.child("AuthorNames").value as? String ?: ""

                    val Description = bookSnapshot.child("Description").value as? String ?: ""
                    val EndDate = bookSnapshot.child("EndDate").value as? String ?: ""
                    val Genres = bookSnapshot.child("Genres").value as? String ?: ""
                    val Id = bookSnapshot.child("Id").value as? String ?: ""
                    val JournalEntry = bookSnapshot.child("JournalEntry").value as? String ?: ""
                    val LastReadDate = bookSnapshot.child("LastReadDate").value as? String ?: ""
                    val LastReadTime = bookSnapshot.child("LastReadTime").value as? String ?: ""
                    val MainCharacters = bookSnapshot.child("MainCharacters").value as? String ?: ""
                    val PublicationDate = bookSnapshot.child("PublicationDate").value as? String ?: ""
                    val Publisher = bookSnapshot.child("Publisher").value as? String ?: ""
                    val PurchaseFrom = bookSnapshot.child("PurchaseFrom").value as? String ?: ""
                    val Tags = bookSnapshot.child("Tags").value as? String ?: ""
                    val Thumbnail = bookSnapshot.child("Thumbnail").value as? String ?: ""
                    val Title = bookSnapshot.child("Title").value as? String ?: ""
                    val userProgress = bookSnapshot.child("UserProgress").value as? Long
                    val UserProgressInt = userProgress?.toInt()
                    val StarRating = bookSnapshot.child("StarRating").value as? Long
                    val StarRatingInt = StarRating?.toInt() ?: 0
                    val UserFinished = bookSnapshot.child("UserFinished").value as Boolean
                    val IsFavorite = bookSnapshot.child("IsFavorite").value as Boolean
                    val StartDate = bookSnapshot.child("StartDate").value as? String ?: ""
                    val pageCount = bookSnapshot.child("PageCount").value as? Long
                    val pageCountInt = pageCount?.toInt() ?: 0
                    val PrevReadCount = bookSnapshot.child("PrevReadCount").value as? Long
                    val PrevReadCountInt = PrevReadCount?.toInt() ?: 0


                    var book: Book = Book(Id, Title, AuthorNames, PublicationDate, StarRatingInt, Publisher, Description, pageCountInt, Thumbnail,
                    JournalEntry, UserProgressInt, UserFinished, IsFavorite, PurchaseFrom, MainCharacters, Genres, Tags, LastReadDate, LastReadTime,
                    PrevReadCountInt, StartDate, EndDate)
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
        bookData["LastReadDate"] = book.lastReadDate.toString()
        bookData["LastReadTime"] = book.lastReadTime.toString()
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

        // updates the book's current HashMap with the new one
        bookDataRef.updateChildren(bookData)
    }

    fun addBook(book: Book)
    {
        val bookDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Books")
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
        bookDataRef.child(book.id).child("Tags").setValue(book.tags)
        bookDataRef.child(book.id).child("Thumbnail").setValue(book.thumbnail)
        bookDataRef.child(book.id).child("Title").setValue(book.title)
        bookDataRef.child(book.id).child("UserFinished").setValue(book.userFinished)
        bookDataRef.child(book.id).child("UserProgress").setValue(book.userProgress)
        bookDataRef.child(book.id).child("IsFavorite").setValue(book.isFav)

    }

    fun stopBookListener()
    {
        listener?.let {
            val profileDataRef = database.getReference("UserData").child(currentUser!!.uid)
                .child("Books")
            profileDataRef.removeEventListener(it)
            listener = null
        }
    }
}

class RecentRepository(private val database: FirebaseDatabase)
{
    public var SocialFeeds = mutableListOf<SocialFeed>()
    private val _isRecentDataReady = MutableLiveData<Boolean>()
    val isRecentDataReady: LiveData<Boolean>
        get() = _isRecentDataReady

    private var listener: ValueEventListener? = null

    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    init
    {
        fetchRecentData()
    }

    fun updateRecent(socialFeed: SocialFeed)
    {
        // gets the path reference to the user's book we are updating
        val currentUser = FirebaseAuth.getInstance().currentUser
        val recentDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Recents").child(socialFeed.id)
        // creates a HashMap of the new data we are setting
        val recentData = HashMap<String, Any>()
        recentData["BookAuthor"] = socialFeed.bookAuthor
        recentData["BookCoverURL"] = socialFeed.bookCoverURL
        recentData["BookTitle"] = socialFeed.bookTitle
        recentData["DateRead"] = socialFeed.lastReadDate
        recentData["Id"] = socialFeed.id
        recentData["IsBookComplete"] = socialFeed.isBookComplete
        recentData["Status"] = socialFeed.status
        recentData["TimeRead"] = socialFeed.lastReadTime
        recentData["Username"] = socialFeed.username

        // updates the book's current HashMap with the new one
        recentDataRef.updateChildren(recentData)
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
                    val DateRead = bookSnapshot.child("DateRead").value as String
                    val Id = bookSnapshot.child("Id").value as String
                    val IsBookComplete = bookSnapshot.child("IsBookComplete").value as Boolean
                    val TimeRead = bookSnapshot.child("TimeRead").value as String
                    val Username = bookSnapshot.child("Username").value as String
                    val Status = bookSnapshot.child("Status").value as String

                    SocialFeeds.add(SocialFeed(Id, BookTitle, BookAuthor, IsBookComplete, Status, BookCoverURL, DateRead, TimeRead, Username))
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
        val recentDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Recents")
        recentDataRef.child(socialFeed.id).child("BookAuthor").setValue(socialFeed.bookAuthor)
        recentDataRef.child(socialFeed.id).child("BookCoverURL").setValue(socialFeed.bookCoverURL)
        recentDataRef.child(socialFeed.id).child("BookTitle").setValue(socialFeed.bookTitle)
        recentDataRef.child(socialFeed.id).child("DateRead").setValue(socialFeed.lastReadDate)
        recentDataRef.child(socialFeed.id).child("Id").setValue(socialFeed.id)
        recentDataRef.child(socialFeed.id).child("IsBookComplete").setValue(socialFeed.isBookComplete)
        recentDataRef.child(socialFeed.id).child("TimeRead").setValue(socialFeed.lastReadTime)
        recentDataRef.child(socialFeed.id).child("Username").setValue(socialFeed.username)
        recentDataRef.child(socialFeed.id).child("Status").setValue(socialFeed.status)
    }

    // Searches the user's recents list and removes the passed in SocialFeed from it
    fun removeRecent(socialFeed: SocialFeed)
    {
        val socialDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Recents")
        socialDataRef.orderByValue().equalTo(socialFeed.id).addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                for (childSnapshot in dataSnapshot.children)
                {
                    if (childSnapshot.value == socialFeed.id)
                    {
                        childSnapshot.ref.removeValue()
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) { }
        })
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

    fun fetchUsers()
    {
        val userRef = database.getReference("ServerData").child("UserList")

        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                Users.clear()
                _isUserListReady.postValue(false)
                for (user in dataSnapshot.children)
                {
                    val username = user.child("Username").value as String
                    val id = user.child("Id").value as String
                    Users.add(Friend(id, username))
                }
                _isUserListReady.postValue(true) // inform the caller we have filled the list with each recent book
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

class FriendRequestRepository(private val database: FirebaseDatabase)
{
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    public var RequestedUsers = mutableListOf<Friend>()
    private val _isFriendRequestReady = MutableLiveData<Boolean>()
    val isFriendsReady: LiveData<Boolean>
        get() = _isFriendRequestReady
    private var listener: ValueEventListener? = null

    fun fetchFriendRequests()
    {
        // Firebase Path References
        val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("FriendRequests")
        listener = object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                _isFriendRequestReady.postValue(false) // inform the caller the list is not ready
                for (friend in dataSnapshot.children)
                {
                    val temp_friend = Friend(friend.child("id").value as? String ?: "", friend.child("username").value as? String ?: "")
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
        val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("FriendRequests")
        friendDataRef.child(friend.id).child("id").setValue(friend.id)
        friendDataRef.child(friend.id).child("username").setValue(friend.username)
    }

    // Searches the user's friend list and removes the passed in friendId from it
    fun removeFriendRequest(friend: Friend)
    {
        val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("FriendRequests")
        friendDataRef.orderByValue().equalTo(friend.id).addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    if (childSnapshot.value == friend.id) {
                        childSnapshot.ref.removeValue()
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) { }
        })
    }

    fun stopFriendRequestListener()
    {
        listener?.let {
            val friendDataRef = database.getReference("UserData").child(currentUser!!.uid).child("Friends")
            friendDataRef.removeEventListener(it)
            listener = null
        }
    }
}


