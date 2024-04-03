package com.example.mymusic.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymusic.service.ApiInterface
import com.example.mymusic.SongPlaybackActivity
import com.example.mymusic.adapter.SongAdapter
import com.example.mymusic.dataClass.Data
import com.example.mymusic.dataClass.Tracks
import com.example.mymusic.databinding.FragmentSongsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import java.util.Locale

class SongsFragment : Fragment() {

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private var dataList: List<Data>? = null
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(Short.toString(),"onCreateView is called")
        _binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SongsFragment","onViewCreated is called")

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getData("eminem")

        retrofitData.enqueue(object : Callback<Tracks?> {
            // Success API call
            override fun onResponse(call: Call<Tracks?>, response: Response<Tracks?>) {
                dataList = response.body()?.data!!
                Log.d("DataList", dataList.toString())

                songAdapter = SongAdapter(this@SongsFragment, dataList!!) { song, type, position ->
                    val intent = Intent(requireContext(), SongPlaybackActivity::class.java)
                    intent.putExtra("mainSongsList", dataList as Serializable)
                    intent.putExtra("playlistType", type)
                    intent.putExtra("position", position)
                    startActivity(intent)
                }
                val songRecyclerView = binding.recyclerViewSongList
                songRecyclerView.adapter = songAdapter
                songRecyclerView.setHasFixedSize(true)
                songRecyclerView.layoutManager = LinearLayoutManager(context)
                Log.d("TAG: onResponse", "onResponse: " + response.body())
            }

            // Failed API call
            override fun onFailure(call: Call<Tracks?>, t: Throwable) {
                Log.d("TAG: onFailure", "onFailure: " + t.message)
            }
        })

        binding.songsSearchBar.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterSongList(newText)
                return true
            }
        })
    }

    private fun filterSongList(query: String?) {
        if (query != null) {
            val filteredSongList = ArrayList<Data>()
            val lowercaseQuery = query.lowercase(Locale.getDefault())
            for (song in dataList!!) {
                val lowercaseTitle = song.title.lowercase(Locale.getDefault())
                if (lowercaseTitle.contains(lowercaseQuery)) {
                    filteredSongList.add(song)
                }
            }

            if (filteredSongList.isEmpty()) {
                Toast.makeText(requireContext(), "No search results", Toast.LENGTH_SHORT).show()
            } else {
                songAdapter.setFilteredSongList(filteredSongList)
            }
        }
    }
}
