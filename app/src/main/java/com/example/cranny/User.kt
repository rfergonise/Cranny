package com.example.cranny

import android.os.Parcel
import android.os.Parcelable

data class User(
    var userId: String = "",
    var name: String = "",
    var username: String = "",
    var friendCount: Int = 0,
    var bookCount: Int = 0,
    var bio: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeInt(friendCount)
        parcel.writeInt(bookCount)
        parcel.writeString(bio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}


