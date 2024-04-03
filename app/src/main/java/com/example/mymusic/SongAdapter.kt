package com.example.mymusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SongAdapter(private val context: SongsFragment,
                  private var songsList: List<Data>,
                  private val onItemClick: (song: Data, type: String, position: Int) -> Unit
): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_list, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songsList[position]
        holder.songTitle.text = currentSong.title // Song's title
        holder.artistName.text = currentSong.artist.name // Song's artist
        Picasso.get().load(currentSong.album.cover).into(holder.coverImage) // Song's cover image

        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentSong, "mainSongs", position)
        }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    inner class SongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.cover_image)
        val songTitle: TextView = itemView.findViewById(R.id.song_title)
        val artistName: TextView = itemView.findViewById(R.id.artist_name)
    }

    fun setFilteredSongList(filteredList: List<Data>) {
        this.songsList = filteredList
        notifyDataSetChanged()
    }
}