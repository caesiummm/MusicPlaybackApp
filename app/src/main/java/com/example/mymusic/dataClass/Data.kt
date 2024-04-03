package com.example.mymusic.dataClass

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.SeekBar
import com.example.mymusic.R
import com.example.mymusic.SongPlaybackActivity
import java.lang.Exception

@Suppress("DEPRECATION")
data class Data(
    val album: Album,
    val artist: Artist,
    val duration: Int,
    val explicit_content_cover: Int,
    val explicit_content_lyrics: Int,
    val explicit_lyrics: Boolean,
    val id: Long,
    val link: String,
    val md5_image: String,
    val preview: String,
    val rank: Int,
    val readable: Boolean,
    val title: String,
    val title_short: String,
    val title_version: String,
    val type: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Album::class.java.classLoader)!!,
        parcel.readParcelable(Artist::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(album, flags)
        parcel.writeParcelable(artist, flags)
        parcel.writeInt(duration)
        parcel.writeInt(explicit_content_cover)
        parcel.writeInt(explicit_content_lyrics)
        parcel.writeByte(if (explicit_lyrics) 1 else 0)
        parcel.writeLong(id)
        parcel.writeString(link)
        parcel.writeString(md5_image)
        parcel.writeString(preview)
        parcel.writeInt(rank)
        parcel.writeByte(if (readable) 1 else 0)
        parcel.writeString(title)
        parcel.writeString(title_short)
        parcel.writeString(title_version)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}

fun setSongPosition(increment: Boolean) {
    if(increment) {
        if(SongPlaybackActivity.currentSongIndex < SongPlaybackActivity.songsList!!.size - 1) {
            SongPlaybackActivity.currentSongIndex++
            Log.d("Skip to Next", "Skipping to next song")
        } else {
            // If current = last song, go to the first song in the list
            SongPlaybackActivity.currentSongIndex = 0
            Log.d("Skip to First", "Skipping to first song")
        }
    } else {
        if(SongPlaybackActivity.currentSongIndex > 0) {
            SongPlaybackActivity.currentSongIndex--
            Log.d("Skip to Previous", "Skipping to previous song")
        } else {
            // If current = first song, go to the last song in the list
            SongPlaybackActivity.currentSongIndex = SongPlaybackActivity.songsList!!.size - 1
            Log.d("Skip to Last", "Skipping to last song")
        }
    }
}

fun monitorSongProgress() {
    SongPlaybackActivity.binding.songPlaybackSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
        // When user alters the seekbar
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(fromUser) {
                SongPlaybackActivity.binding.songPlaybackSeekBar.progress = progress
                SongPlaybackActivity.playbackService!!.mediaPlayer!!.seekTo(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            SongPlaybackActivity.playbackService!!.mediaPlayer!!.pause()
            SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            SongPlaybackActivity.playbackService!!.showNotification()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            SongPlaybackActivity.playbackService!!.mediaPlayer!!.start()
            SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            SongPlaybackActivity.playbackService!!.showNotification()
        }
    })

    // Song progress updates in every 1000 ms
    SongPlaybackActivity.handler.postDelayed(object: Runnable {
        override fun run() {
            try {
                SongPlaybackActivity.binding.songPlaybackSeekBar.progress = SongPlaybackActivity.playbackService!!.mediaPlayer!!.currentPosition
                updatePlaybackInfo(SongPlaybackActivity.playbackService!!.mediaPlayer!!.currentPosition, SongPlaybackActivity.playbackService!!.mediaPlayer!!.duration)

                SongPlaybackActivity.handler.postDelayed(this, 1000)
            } catch (exception: Exception) {
                SongPlaybackActivity.binding.songPlaybackSeekBar.progress = 0
            }
        }

    }, 1000)
}

fun updatePlaybackInfo(currentPosition: Int, duration: Int) {
    val durationMinutes = duration / 1000 / 60
    val durationSeconds = duration / 1000 % 60
    val currentPositionMinutes = currentPosition / 1000 / 60
    val currentPositionSeconds = currentPosition / 1000 % 60

    val durationText = String.format("%02d:%02d", durationMinutes, durationSeconds)
    val currentPositionText = String.format("%02d:%02d", currentPositionMinutes, currentPositionSeconds)

    SongPlaybackActivity.binding.timeSongProgress.text = currentPositionText // updating every 1000ms
    SongPlaybackActivity.binding.timeSongDuration.text = durationText // song's duration - fixed
}