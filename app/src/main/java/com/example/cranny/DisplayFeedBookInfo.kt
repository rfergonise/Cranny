package com.example.cranny

import android.os.Parcel
import android.os.Parcelable

data class DisplayFeedBookInfo(
    var strBookOwnerUsername: String,
    var strTitle: String,
    var strAuthor: String,
    var strCoverURL: String,
    var nTotalReadPages: Int,
    var nCountPage: Int,
    var nTotalReadChapters: Int,
    var nCountChapter: Int,
    var strMainCharacters: String,
    var strGenres: String,
    var strTags: String,
    var fRating: Float,
    var strPurchasedFrom: String,
    var lLastReadTime: Long,
    var strUserSummary: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(strBookOwnerUsername)
        parcel.writeString(strTitle)
        parcel.writeString(strAuthor)
        parcel.writeString(strCoverURL)
        parcel.writeInt(nTotalReadPages)
        parcel.writeInt(nCountPage)
        parcel.writeInt(nTotalReadChapters)
        parcel.writeInt(nCountChapter)
        parcel.writeString(strMainCharacters)
        parcel.writeString(strGenres)
        parcel.writeString(strTags)
        parcel.writeFloat(fRating)
        parcel.writeString(strPurchasedFrom)
        parcel.writeLong(lLastReadTime)
        parcel.writeString(strUserSummary)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DisplayFeedBookInfo> {
        override fun createFromParcel(parcel: Parcel): DisplayFeedBookInfo {
            return DisplayFeedBookInfo(parcel)
        }

        override fun newArray(size: Int): Array<DisplayFeedBookInfo?> {
            return arrayOfNulls(size)
        }
    }
}
