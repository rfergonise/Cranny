package com.example.cranny

data class ProfileData(
    var userId: String,
    var username: String,
    var displayname: String,
    var bio: String,
    var countFriend: Int,
    var countBook: Int,
    var iPriv: Boolean,
    var isOwner: Boolean,
    var books: MutableList<DisplayFeedBookInfo>,
)
