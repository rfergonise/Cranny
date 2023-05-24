package com.example.cranny
import android.os.Parcel
import android.os.Parcelable

data class FriendAdapterData(
    val id: String,
    val username: String,
    val mlBooksToDisplay: MutableList<DisplayFeedBookInfo>,
    val isPrivateProfile: Boolean,
    val isFavorite: Boolean
) : Parcelable {
    // Implement the Parcelable methods here

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeTypedList(mlBooksToDisplay)
        parcel.writeByte(if (isPrivateProfile) 1 else 0)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendAdapterData> {
        override fun createFromParcel(parcel: Parcel): FriendAdapterData {
            return FriendAdapterData(parcel)
        }

        override fun newArray(size: Int): Array<FriendAdapterData?> {
            return arrayOfNulls(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        mutableListOf<DisplayFeedBookInfo>().apply {
            parcel.readTypedList(this, DisplayFeedBookInfo.CREATOR)
        },
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )
}
