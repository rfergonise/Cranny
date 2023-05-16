package com.example.cranny

data class FriendAdapterData(val id: String, val username: String, val mlBooksToDisplay: MutableList<DisplayFeedBookInfo>, val isPrivateProfile: Boolean, val isFavorite: Boolean)
