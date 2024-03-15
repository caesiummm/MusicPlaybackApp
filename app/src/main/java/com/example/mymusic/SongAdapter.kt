package com.example.mymusic


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private val songsList: ArrayList<Songs>): RecyclerView.Adapter<SongAdapter.MyViewHolder>() {

    // private lateinit var binding: SongListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // val binding = SongListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = songsList[position]
        holder.coverImage.setImageResource(currentItem.coverImage)
        holder.songTitle.text = currentItem.songTitle
        holder.artistName.text = currentItem.artistName
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val coverImage: ImageView = itemView.findViewById(R.id.cover_image)
        val songTitle: TextView = itemView.findViewById(R.id.song_title)
        val artistName: TextView = itemView.findViewById(R.id.artist_name)
    }
}