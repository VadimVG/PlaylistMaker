package com.example.playlistmaker.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

class TrackViewHolder (itemView:View) : RecyclerView.ViewHolder(itemView) {
    private val songName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.trackArtist)
    private val trackDuration: TextView = itemView.findViewById(R.id.trackDuration)
    private val trackCover: ImageView = itemView.findViewById(R.id.trackLogo)

    fun bind(track:Track) {
        songName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_logo_placeholder)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(trackCover)
    }
}