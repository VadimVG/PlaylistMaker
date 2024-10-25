package com.example.playlistmaker


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.search.AudioPlayerCurrentTrack
import com.example.playlistmaker.search.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        findViewById<TextView>(R.id.audioplayerBack).setOnClickListener{ finish() } // возвращение на главный экран

        val currentTrack=intent.getSerializableExtra(AudioPlayerCurrentTrack.CURRENT_TRACK) as Track
        Log.d("currentTrack", "${currentTrack.trackName}, ${currentTrack.collectionName} ${currentTrack.collectionName?.length}")
        createAlbumСover(currentTrack)
    }



    private fun createAlbumСover(track: Track) {

        val trackLogo: ImageView = this.findViewById(R.id.trackLogo)

        val trackDurationValue: TextView = this.findViewById(R.id.trackDuration2)
        trackDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())

        val artistName: TextView = this.findViewById(R.id.trackArtist)
        artistName.text = track.artistName

        val trackName: TextView = this.findViewById(R.id.trackName)
        trackName.text = track.artistName

        val trackAlbum: TextView = this.findViewById(R.id.trackAlbum)
        val trackAlbumValue: TextView = this.findViewById(R.id.trackAlbum2)
        if(track.collectionName.isNullOrEmpty()) {
            trackAlbum.visibility = View.GONE
        }
        else trackAlbumValue.text = track.collectionName

        val trackYearValue: TextView = this.findViewById(R.id.trackYear2)
        trackYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate!!)

        val trackGenreValue: TextView = this.findViewById(R.id.trackGenre2)
        trackGenreValue.text = track.primaryGenreName

        val trackCountryValue: TextView = this.findViewById(R.id.trackCountry2)
        trackCountryValue.text = track.country


        Glide.with(this@AudioPlayerActivity)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .fitCenter()
            .placeholder(R.drawable.ic_placeholder_audioplayer)
            .error(R.drawable.ic_placeholder_audioplayer)
            .centerCrop()
            .transform(RoundedCorners(16))
            .into(trackLogo)
    }

}