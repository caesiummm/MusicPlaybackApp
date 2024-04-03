package com.example.mymusic

import android.app.Service.STOP_FOREGROUND_REMOVE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mymusic.dataClass.setSongPosition
import com.example.mymusic.fragment.NowPlayingFragment
import com.squareup.picasso.Picasso
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            // Actions performed when Play/Pause/Prev/Next/Exit button in notification is clicked
            //only play next or prev song, when music list contains more than one song
            ApplicationClass.PREVIOUS -> if(SongPlaybackActivity.songsList!!.size > 1) prevNextSong(increment = false, context = context!!)
            ApplicationClass.PLAY -> if(SongPlaybackActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> if(SongPlaybackActivity.songsList!!.size > 1) prevNextSong(increment = true, context = context!!)
            ApplicationClass.EXIT -> {
                SongPlaybackActivity.playbackService!!.stopForeground(STOP_FOREGROUND_REMOVE)
                SongPlaybackActivity.playbackService!!.mediaPlayer!!.release()
                SongPlaybackActivity.playbackService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        SongPlaybackActivity.isPlaying = true
        SongPlaybackActivity.playbackService!!.mediaPlayer!!.start()
        SongPlaybackActivity.playbackService!!.showNotification()
        SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_pause_playback_small)
    }

    private fun pauseMusic() {
        SongPlaybackActivity.isPlaying = false
        SongPlaybackActivity.playbackService!!.mediaPlayer!!.pause()
        SongPlaybackActivity.playbackService!!.showNotification()
        SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_play_playback_small)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        SongPlaybackActivity.playbackService!!.createMediaPlayer(context)

        // Setting Playback Activity
        Picasso.get()
            .load(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].album.cover_big)
            .into(SongPlaybackActivity.binding.songPlaybackCover)
        SongPlaybackActivity.binding.songPlaybackTitle.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].title
        SongPlaybackActivity.binding.songPlaybackArtist.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].artist.name

        // Setting Now Playing Fragment
        Picasso.get()
            .load(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].album.cover_small)
            .into(NowPlayingFragment.binding.nowPlayingCoverImage)
        NowPlayingFragment.binding.nowPlayingSongTitle.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].title_short
        NowPlayingFragment.binding.nowPlayingArtistName.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].artist.name
        playMusic()
    }
}