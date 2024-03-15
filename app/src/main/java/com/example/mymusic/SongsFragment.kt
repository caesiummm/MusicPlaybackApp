package com.example.mymusic

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusic.databinding.FragmentSongsBinding

class SongsFragment : Fragment() {

    // private lateinit var binding: FragmentSongsBinding
    //private var _binding: FragmentSongsBinding? = null
    //private val binding get() = _binding!!
    private lateinit var songAdapter: SongAdapter
    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songArrayList: ArrayList<Songs>

    private lateinit var imageId: Array<Int>
    private lateinit var songTitle: Array<String>
    private lateinit var artistName: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(Short.toString(),"onCreateView is called")
        return inflater.inflate(R.layout.fragment_songs, container, false)
        //_binding = FragmentSongsBinding.inflate(inflater, container, false)
        //return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Short.toString(),"onCreate is called")

        songArrayList = arrayListOf<Songs>()

        imageId = arrayOf(
            R.drawable.cover_a,
            R.drawable.cover_b,
            R.drawable.cover_c,
            R.drawable.cover_d,
            R.drawable.cover_e,
            R.drawable.cover_f,
            R.drawable.cover_g,
            R.drawable.cover_h,
            R.drawable.cover_i,
            R.drawable.cover_j,
            R.drawable.cover_k,
            R.drawable.cover_l
        )

        songTitle = arrayOf(
            "Rewrite the Star",
            "Everything has Changed",
            "Wish you were Here",
            "Someone you Loved",
            "Talking to the Moon",
            "Easy On Me",
            "Cruel Summer",
            "Yellow",
            "Call Me Maybe",
            "All of Me",
            "Mia and Sebastian’s Theme",
            "Bauklötze"
        )

        artistName = arrayOf(
            "Zac Efron and Zendaya",
            "Taylor Swift (ft. Ed Sheeran)",
            "Avril Lavigne",
            "Lewis Capaldi",
            "Bruno Mars",
            "Adele",
            "Taylor Swift",
            "Coldplay",
            "Carly Rae Jepsen",
            "John Legend",
            "Justin Hurwitz",
            "Hiroyuki Sawanno and Mika Kobayashi"
        )

        for(i in imageId.indices){
            val songs = Songs(imageId[i], songTitle[i], artistName[i])
            songArrayList.add(songs)
        }

        val layoutManager = LinearLayoutManager(context)
        songRecyclerView = view.findViewById(R.id.recycler_view_song_list)
        songRecyclerView.layoutManager = layoutManager
        songRecyclerView.setHasFixedSize(true)
        songAdapter = SongAdapter(songArrayList)
        songRecyclerView.adapter = songAdapter
//        _binding?.recyclerViewSongList?.layoutManager = LinearLayoutManager(requireContext())
//        _binding?.recyclerViewSongList?.setHasFixedSize(true)


        // getUserData()

    }

//    private fun getUserData() {
//
//        _binding?.recyclerViewSongList?.adapter = SongAdapter(songArrayList)
//    }
}