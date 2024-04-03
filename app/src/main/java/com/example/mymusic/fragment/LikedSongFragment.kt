package com.example.mymusic.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymusic.SongPlaybackActivity
import com.example.mymusic.adapter.LikedSongAdapter
import com.example.mymusic.dataClass.Data
import com.example.mymusic.databinding.FragmentLikedSongBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LikedSongFragment : Fragment() {
    private var _binding: FragmentLikedSongBinding? = null
    private val binding get() = _binding!!

    private lateinit var likedSongAdapter: LikedSongAdapter
    private var likedSongsList: HashSet<Data> = HashSet()
    // private lateinit var likedSongsList: List<LikedSongs>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLikedSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likedSongsList = loadLikedSongs(requireContext())
        Log.d("My Liked Songs List", likedSongsList.size.toString())

        likedSongAdapter = LikedSongAdapter(this@LikedSongFragment, likedSongsList.toList()) { likedSongs, type, position ->
            val intent = Intent(requireContext(), SongPlaybackActivity::class.java)
            intent.putExtra("likedSongsList", ArrayList(likedSongsList))
            intent.putExtra("playlistType", type)
            intent.putExtra("position", position)
            startActivity(intent)
        }

        val likedSongRecyclerView = binding.recyclerViewLikedSongList
        likedSongRecyclerView.adapter = likedSongAdapter
        likedSongRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun loadLikedSongs(context: Context): HashSet<Data> {
        val sharedPreferences = context.getSharedPreferences("LikedSongsPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("likedSongs", "")
        val type = object: TypeToken<HashSet<Data>>() {}.type
        return gson.fromJson(json, type)?: HashSet()
    }
}