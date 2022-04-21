package com.example.mgplayer.view.home

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mgplayer.R
import com.example.mgplayer.model.Song

class HomeAdapter(var listener:OnSongClickListener) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    var songsList: ArrayList<Song> = ArrayList()

    inner class HomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val musicName: TextView = itemView.findViewById(R.id.textViewMusicName)
        val artistName: TextView = itemView.findViewById(R.id.textViewMusicArtistName)
        val coverImage: ImageView = itemView.findViewById(R.id.musicCover)

        fun bind(song: Song,position: Int,list: ArrayList<Song>) {
            musicName.text = song.name
            artistName.text = song.artist

            val albumArt: String? = song.albumArt
            if (albumArt != null) {
                coverImage.setImageURI(Uri.parse(albumArt))

                if (coverImage.drawable == null) {
                    coverImage.setImageResource(R.drawable.man)
                }
            } else {
                coverImage.setImageResource(R.drawable.man)
            }

            itemView.setOnClickListener {
                listener.onSongClick(song,position,list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rec_main_music, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(songsList.get(position),position,songsList)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    interface OnSongClickListener {
        fun onSongClick(song: Song,position: Int,list: ArrayList<Song>)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSongsListAdapter(list: ArrayList<Song>) {
        songsList = list
        notifyDataSetChanged()
    }
}

