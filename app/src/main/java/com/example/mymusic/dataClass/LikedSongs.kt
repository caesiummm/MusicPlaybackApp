package com.example.mymusic.dataClass

import android.os.Parcel
import android.os.Parcelable

@Suppress("DEPRECATION")
data class LikedSongs(
    val id: Long,
    val album: Album,
    val artist: Artist,
    val title: String,
    val preview: String,
    val likedStatus: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Album::class.java.classLoader)!!,
        parcel.readParcelable(Artist::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(album, flags)
        parcel.writeParcelable(artist, flags)
        parcel.writeString(title)
        parcel.writeString(preview)
        parcel.writeByte(if (likedStatus) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LikedSongs> {
        override fun createFromParcel(parcel: Parcel): LikedSongs {
            return LikedSongs(parcel)
        }

        override fun newArray(size: Int): Array<LikedSongs?> {
            return arrayOfNulls(size)
        }
    }


}
