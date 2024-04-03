package com.example.mymusic

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.mymusic.databinding.ActivitySongPlaybackBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import java.lang.Exception
import java.lang.RuntimeException

class SongPlaybackActivity : AppCompatActivity(), ServiceConnection {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivitySongPlaybackBinding
        lateinit var placeholderPlaylist: List<Any>
        var songsList: List<Data>? = null
        var likedSongsList: HashSet<Data> = HashSet()
        var currentSongIndex: Int = 0
        var isPlaying: Boolean = false
        var isLiked = false
        var playbackService: BackgroundPlaybackService? = null
        @Suppress("Deprecation")
        var handler = Handler()
        var songExtraData = Bundle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getStringExtra("class") == "Now Playing") {
            binding.songPlaybackTitle.text = songsList!![currentSongIndex].title
            binding.songPlaybackArtist.text = songsList!![currentSongIndex].artist.name
            Picasso.get().load(songsList!![currentSongIndex].album.cover_big).into(binding.songPlaybackCover)

            binding.songPlaybackSeekBar.progress = playbackService!!.mediaPlayer!!.currentPosition
            binding.songPlaybackSeekBar.max = playbackService!!.mediaPlayer!!.duration
            if (isPlaying) binding.btnPlayPause.setImageResource(R.drawable.ic_pause) else binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            checkIsLiked(songsList!!, currentSongIndex)
            monitorSongProgress()
        } else {
            // Create a placeholder playlist for accepting playlist
            val placeholderPlaylistName: String? = intent.getStringExtra("playlistType")

            // Assign corresponding playlist according to playlist type
            songExtraData = intent.extras!!
            currentSongIndex = songExtraData.getInt("position", 0)

            @Suppress("Deprecation")
            placeholderPlaylist = when(placeholderPlaylistName) {
                "mainSongs" -> songExtraData.getParcelableArrayList("mainSongsList")!! // get songs list
                "likedSongs" -> songExtraData.getParcelableArrayList("likedSongsList")!! // get liked songs list
                else -> emptyList()
            }
            songsList = placeholderPlaylist as List<Data>

            // Start playback service
            val intent = Intent(this, BackgroundPlaybackService::class.java)
            bindService(intent, this, BIND_AUTO_CREATE)
            startService(intent)
        }

        Log.d("Is my song list null?", songsList.isNullOrEmpty().toString())
        Log.d("Current Song Position #1", currentSongIndex.toString())

        binding.btnPlayPause.setOnClickListener { togglePlayback() }

        binding.btnSkipNext.setOnClickListener { prevNextSong(increment = true) }

        binding.btnSkipPrev.setOnClickListener { prevNextSong(increment = false) }

        binding.btnMinimize.setOnClickListener { finish() }

        val sharedPreferences = getSharedPreferences("LikedSongsPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        binding.apply {
            binding.btnLike.setOnClickListener {
                val likedSong = songsList!![currentSongIndex]
                if(!isLiked) {
                    likedSongsList.add(likedSong)
                    Log.d("Adding My Liked Songs", likedSongsList.size.toString())

                    val gson = Gson()
                    val json = gson.toJson(likedSongsList)
                    editor.apply {
                        putString("likedSongs", json)
                        putBoolean("isLiked", true)
                        apply()
                    }

                    binding.btnLike.setImageResource(R.drawable.ic_like_filled)
                    Toast.makeText(this@SongPlaybackActivity, "Saved to liked music", Toast.LENGTH_SHORT).show()
                }
                else {
                    likedSongsList.remove(likedSong)
                    //likedSongAdapter.notifyDataSetChanged()
                    Log.d("Removing My Liked Songs", likedSongsList.size.toString())

                    editor.apply {
                        val gson = Gson()
                        val json = gson.toJson(likedSongsList)
                        putBoolean("isLiked", false)
                        putString("likedSongs", json)
                        apply()
                    }

                    binding.btnLike.setImageResource(R.drawable.ic_like_outlined)
                    Toast.makeText(this@SongPlaybackActivity, "Removed from liked music", Toast.LENGTH_SHORT).show()
                }
                isLiked = !isLiked
            }
        }

        var isAdded = false
        binding.btnAddToPlaylist.setOnClickListener {
            if(!isAdded) {
                binding.btnAddToPlaylist.setImageResource(R.drawable.ic_playlist_check)
                Toast.makeText(this, "Saved to playlist", Toast.LENGTH_SHORT).show()
            } else {
                binding.btnAddToPlaylist.setImageResource(R.drawable.ic_playlist_add)
                Toast.makeText(this, "Removed from playlist", Toast.LENGTH_SHORT).show()
            }
            isAdded = !isAdded
        }
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            initializeMusicPlayer()
        } else {
            setSongPosition(increment = false)
            initializeMusicPlayer()
        }
    }

    private fun loadLikedSongs(context: Context): HashSet<Data> {
        Log.d("loadLikedSongs", "Loading liked songs")
        val sharedPreferences = context.getSharedPreferences("LikedSongsPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("likedSongs", "")
        val type = object: TypeToken<HashSet<Data>>() {}.type
        return gson.fromJson(json, type)?: HashSet()
    }

    private fun togglePlayback() {
        // When the song is already playing
        if (isPlaying) {
            isPlaying = false
            playbackService!!.mediaPlayer!!.pause()
            playbackService!!.showNotification()
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        } else {
            isPlaying = true
            playbackService!!.mediaPlayer!!.start()
            playbackService!!.showNotification()
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun initializeMusicPlayer() {
        // Initializing media player
        // Song always play when media player is created
        // Shows notification when a song is selected
        if (playbackService!!.mediaPlayer == null) playbackService!!.mediaPlayer = MediaPlayer()
        playbackService!!.mediaPlayer!!.reset()
        playbackService!!.mediaPlayer = MediaPlayer.create(this, songsList!![currentSongIndex].preview.toUri())

        // Set song's title, artist's name, and cover image in playback
        binding.songPlaybackTitle.text = songsList!![currentSongIndex].title
        binding.songPlaybackArtist.text = songsList!![currentSongIndex].artist.name
        Picasso.get().load(songsList!![currentSongIndex].album.cover_big).into(binding.songPlaybackCover)
        Log.d("Current Song", songsList!![currentSongIndex].title)
        Log.d("Current position", "Song number $currentSongIndex")

        // Start playing the song
        playbackService!!.mediaPlayer!!.setOnPreparedListener {
            // Set limit of seekbar to maximum of song's duration
            binding.songPlaybackSeekBar.max = playbackService!!.mediaPlayer!!.duration
            playbackService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        }
        playbackService!!.showNotification()

        // After song finishes
        playbackService!!.mediaPlayer!!.setOnCompletionListener {
            Log.d("OnCompletion", "Song ends")
            prevNextSong(increment = true)
        }

        monitorSongProgress() // Data class

        // Check if the song has been liked or not
        // Configure the like button correspondingly
        checkIsLiked(songsList!!, currentSongIndex)
    }

    private fun checkIsLiked(songsList: List<Data>, position: Int) {
        val currentSongId = songsList[position].id
        likedSongsList = loadLikedSongs(this)
        isLiked = likedSongsList.any { it.id == currentSongId }
        binding.btnLike.setImageResource(if(isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outlined)
        Log.d("Is this song liked?", isLiked.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("onServiceConnected", "Connecting to Playback Service")
        if (playbackService ==  null) {
            val binder = service as BackgroundPlaybackService.MyBinder
            playbackService = binder.currentService()
            playbackService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            playbackService!!.audioManager.requestAudioFocus(playbackService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            //playbackService!!.updatePlaybackState(isPlaying = true, playbackService!!.mediaPlayer!!.currentPosition.toLong(), playbackSpeed = 1F)
        }
        initializeMusicPlayer()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d("onServiceDisconnected", "Disconnecting Playback Service")
        playbackService = null
    }

//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        super.onBackPressed()
//        supportFragmentManager.popBackStack()
//    }
}