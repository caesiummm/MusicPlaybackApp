package com.example.mymusic

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore.Audio
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.system.exitProcess

class BackgroundPlaybackService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    var notification: Notification? = null
    var playbackSpeed: Float = 1F
    private lateinit var mediaSession: MediaSessionCompat
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder: Binder() {
        fun currentService(): BackgroundPlaybackService {
            return this@BackgroundPlaybackService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // SongPlaybackActivity.playbackService!!.audioManager.abandonAudioFocus(SongPlaybackActivity.playbackService)
        SongPlaybackActivity.playbackService!!.stopForeground(STOP_FOREGROUND_REMOVE)
        SongPlaybackActivity.playbackService!!.mediaPlayer!!.release()
        SongPlaybackActivity.playbackService!!.mediaPlayer = null
        exitProcess(1)
    }

    fun showNotification() {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val intent = Intent(baseContext, SongPlaybackActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP) }
        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val coverImageUrl = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].album.cover
        Picasso.get().load(coverImageUrl).into(object: com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
                    .setContentIntent(contentIntent)
                    .setContentTitle(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].title)
                    .setContentText(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].artist.name)
                    .setSmallIcon(R.drawable.music_app_logo)
                    .setLargeIcon(bitmap)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOnlyAlertOnce(true)
//                    .addAction(R.drawable.ic_skip_prev_noti_small, "Previous", prevPendingIntent)
//                    .addAction(playPauseBtn, "Play", playPendingIntent)
//                    .addAction(R.drawable.ic_skip_next_noti_small, "Next", nextPendingIntent)
//                    .addAction(R.drawable.ic_noti_exit, "Exit", exitPendingIntent)
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken))
                    .build()
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.d("onBitmapFailed", "Loading placeholder icon notification")
                NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.music_player_icon))
                    .build()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }
        })



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            playbackSpeed = if (SongPlaybackActivity.isPlaying) 1F else 0F
            mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())

            updatePlaybackState(SongPlaybackActivity.isPlaying, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
            mediaSession.setCallback(object: MediaSessionCompat.Callback() {
                // When play button in notification is pressed
                override fun onPlay() {
                    super.onPlay()
                    SongPlaybackActivity.isPlaying = true
                    SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                    NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_pause_playback_small)
                    //showNotification(R.drawable.ic_pause_noti_small)
                    updatePlaybackState(isPlaying = true, mediaPlayer!!.currentPosition.toLong(), 1F)
                    mediaPlayer!!.start()
                }

                // When pause button in notification is pressed
                override fun onPause() {
                    super.onPause()
                    SongPlaybackActivity.isPlaying = false
                    SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                    NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_play_playback_small)
                    //showNotification(R.drawable.ic_play_noti_small)
                    updatePlaybackState(isPlaying = false, mediaPlayer!!.currentPosition.toLong(), 0F)
                    mediaPlayer!!.pause()
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()
                    prevNextSong(increment = true)
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    prevNextSong(increment = false)
                }

                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    Log.d("onMediaButtonEvent", "In onMediaButtonEvent")
                    if (SongPlaybackActivity.isPlaying) {
                        SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                        NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_play_playback_small)
                        SongPlaybackActivity.isPlaying = false
                        mediaPlayer!!.pause()
                        showNotification()
                    } else {
                        SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                        NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_pause_playback_small)
                        SongPlaybackActivity.isPlaying = true
                        mediaPlayer!!.start()
                        showNotification()
                    }
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }

                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer!!.seekTo(pos.toInt())
                    updatePlaybackState(SongPlaybackActivity.isPlaying, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                }
            })
        }



        startForeground(13, notification)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            // Pause music
            SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_play_playback_small)
            showNotification()
            SongPlaybackActivity.isPlaying = false
            updatePlaybackState(isPlaying = false, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
            mediaPlayer!!.pause()
        } else {
            SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_pause_playback_small)
            showNotification()
            SongPlaybackActivity.isPlaying = true
            updatePlaybackState(isPlaying = true, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
            mediaPlayer!!.start()
        }
    }

    fun createMediaPlayer(context: Context) {
        if (mediaPlayer == null) mediaPlayer = MediaPlayer()
        mediaPlayer!!.reset()
        mediaPlayer = MediaPlayer.create(context, SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].preview.toUri())
        SongPlaybackActivity.binding.songPlaybackTitle.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].title
        SongPlaybackActivity.binding.songPlaybackArtist.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].artist.name
        Picasso.get().load(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].album.cover_big).into(
            SongPlaybackActivity.binding.songPlaybackCover)
    }

    fun updatePlaybackState(isPlaying: Boolean, currentPosition: Long, playbackSpeed: Float) {
        val playbackState = PlaybackStateCompat.Builder()
            .setState(if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                currentPosition,
                playbackSpeed)
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_SEEK_TO)
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
        } else {
            setSongPosition(increment = false)
        }
        createMediaPlayer(baseContext)
        Picasso.get()
            .load(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].album.cover_big)
            .into(NowPlayingFragment.binding.nowPlayingCoverImage)
        NowPlayingFragment.binding.nowPlayingSongTitle.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].title_short
        NowPlayingFragment.binding.nowPlayingArtistName.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].artist.name
        SongPlaybackActivity.isPlaying = true
        mediaPlayer!!.start()
        showNotification()
        SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        NowPlayingFragment.binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_pause_playback_small)
    }
}