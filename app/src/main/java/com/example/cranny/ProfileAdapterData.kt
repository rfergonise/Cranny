package com.example.cranny

data class ProfileAdapterData(var userId: String, var username: String, var bookGrouping: MutableList<ThreeDisplayBooks>, var isPriv: Boolean, var isOwner: Boolean)
