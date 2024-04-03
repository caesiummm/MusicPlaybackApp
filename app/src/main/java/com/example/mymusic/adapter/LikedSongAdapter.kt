package com.example.mymusic.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusic.dataClass.Data
import com.example.mymusic.fragment.LikedSongFragment
import com.example.mymusic.R
import com.squareup.picasso.Picasso

class LikedSongAdapter(private val context: LikedSongFragment,
                       private val likedSongsList: List<Data>,
                       private val onItemClick: (likedSongs: Data, type: String, position: Int) -> Unit
) : RecyclerView.Adapter<LikedSongAdapter.LikedSongViewHolder> (){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LikedSongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_list, parent, false)
        return LikedSongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LikedSongViewHolder, position: Int) {
        val currentSong = likedSongsList[position]
        Log.d("LikedSongAdapter", currentSong.title)
        holder.songTitle.text = currentSong.title // Song's title
        holder.artistName.text = currentSong.artist.name // Song's artist
        Picasso.get().load(currentSong.album.cover).into(holder.coverImage) // Song's cover image

        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentSong, "likedSongs", position)
            notifyItemChanged(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return likedSongsList.size
    }

    inner class LikedSongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val coverImage: ImageView = itemView.findViewById(R.id.cover_image)
        val songTitle: TextView = itemView.findViewById(R.id.song_title)
        val artistName: TextView = itemView.findViewById(R.id.artist_name)
    }

}