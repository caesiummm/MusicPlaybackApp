package com.example.mymusic.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.mymusic.R
import com.example.mymusic.SongPlaybackActivity
import com.example.mymusic.dataClass.setSongPosition
import com.example.mymusic.databinding.FragmentNowPlayingBinding
import com.squareup.picasso.Picasso

class NowPlayingFragment : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var _binding: FragmentNowPlayingBinding? = null
        val binding get() = _binding!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        binding.root.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNowPlayingPlayPause.setOnClickListener {
            if (SongPlaybackActivity.isPlaying) pauseMusic() else playMusic()
        }

        binding.btnNowPlayingSkipNext.setOnClickListener {
            setSongPosition(increment = true)
            SongPlaybackActivity.playbackService!!.createMediaPlayer(requireContext())
            Picasso.get()
                .load(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].album.cover_big)
                .into(binding.nowPlayingCoverImage)
            binding.nowPlayingSongTitle.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].title_short
            binding.nowPlayingArtistName.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].artist.name
            playMusic()
        }

        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), SongPlaybackActivity::class.java)
            intent.putExtra("index", SongPlaybackActivity.currentSongIndex)
            intent.putExtra("class", "Now Playing")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
    }

    override fun onResume() {
        super.onResume()
        if (SongPlaybackActivity.playbackService != null) {
            binding.root.visibility = View.VISIBLE
            Picasso.get()
                .load(SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].album.cover_big)
                .into(binding.nowPlayingCoverImage)
            binding.nowPlayingSongTitle.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].title_short
            binding.nowPlayingArtistName.text = SongPlaybackActivity.songsList!![SongPlaybackActivity.currentSongIndex].artist.name

            if (SongPlaybackActivity.isPlaying) binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_pause_playback_small)
            else binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_play_playback_small)
        }
    }

    private fun playMusic() {
        SongPlaybackActivity.isPlaying = true
        SongPlaybackActivity.playbackService!!.mediaPlayer!!.start()
        SongPlaybackActivity.playbackService!!.showNotification()
        SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_pause_playback_small)
    }

    private fun pauseMusic() {
        SongPlaybackActivity.isPlaying = false
        SongPlaybackActivity.playbackService!!.mediaPlayer!!.pause()
        SongPlaybackActivity.playbackService!!.showNotification()
        SongPlaybackActivity.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        binding.btnNowPlayingPlayPause.setImageResource(R.drawable.ic_play_playback_small)
    }
}




















