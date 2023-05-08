package com.example.cranny

import android.os.Parcel
import android.os.Parcelable

data class SocialFeed(
    var id: String,
    var bookTitle: String,
    var bookAuthor: String,
    var isBookComplete: Boolean,
    var status: String,
    var bookCoverURL: String,
    var lastReadDate: Long,
    var lastReadTime: Long,
    var username: String,
    var mainCharacters: String,
    var journalEntry: String,
    var purchasedFrom: String,
    var genres: String,
    var tags: String,
    var starRating: Float
): Parcelable {

    // Implement the describeContents() function
    override fun describeContents(): Int {
        return 0
    }

    // Implement the writeToParcel() function
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(bookTitle)
        parcel.writeString(bookAuthor)
        parcel.writeByte(if (isBookComplete) 1 else 0)
        parcel.writeString(status)
        parcel.writeString(bookCoverURL)
        parcel.writeLong(lastReadDate)
        parcel.writeLong(lastReadTime)
        parcel.writeString(username)
        parcel.writeString(mainCharacters)
        parcel.writeString(journalEntry)
        parcel.writeString(purchasedFrom)
        parcel.writeString(genres)
        parcel.writeString(tags)
        parcel.writeFloat(starRating)
    }

    // Companion object with a CREATOR field that implements the Parcelable.Creator interface
    companion object CREATOR : Parcelable.Creator<SocialFeed> {
        override fun createFromParcel(parcel: Parcel): SocialFeed {
            return SocialFeed(
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readByte() != 0.toByte(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readLong(),
                parcel.readLong(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readFloat()
            )
        }

        override fun newArray(size: Int): Array<SocialFeed?> {
            return arrayOfNulls(size)
        }
    }
}