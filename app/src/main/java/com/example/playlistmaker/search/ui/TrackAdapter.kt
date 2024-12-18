package com.example.playlistmaker.search.ui


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track


class TrackAdapter (
    private val tracks: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder> () {

    var onItemClickListener: ((Track) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent,false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.setIsRecyclable(false)
        holder.itemView.setOnClickListener{
            onItemClickListener?.invoke(tracks[position])
        }
    }

}