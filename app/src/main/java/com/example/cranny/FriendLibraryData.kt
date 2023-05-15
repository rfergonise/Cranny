package com.example.cranny

data class FriendLibraryData(var friendUsername: String, var friendUserID: String, var friendLibrary: MutableList<DisplayFeedBookInfo>, var isFriendPrivate: Boolean)
