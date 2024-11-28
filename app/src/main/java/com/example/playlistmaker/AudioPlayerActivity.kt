package com.example.playlistmaker


import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.audioplayer.AudioPlayerController
import com.example.playlistmaker.search.AudioPlayerCurrentTrack
import com.example.playlistmaker.search.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity: AppCompatActivity() {

    private lateinit var currentTrack: Track
    private lateinit var play: ImageView
    private var mediaPlayer = MediaPlayer()
    private lateinit var url: String
    private lateinit var audioPlayerController: AudioPlayerController
    private lateinit var currentTime: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        findViewById<TextView>(R.id.audioplayerBack).setOnClickListener{ finish() } // возвращение на главный экран

        currentTrack=intent.getSerializableExtra(AudioPlayerCurrentTrack.CURRENT_TRACK) as Track // получение данных о треке, на который кликнул пользователь в меню поиска
        createAlbumСover(currentTrack) // отрисовка экрана плеера с данными о треке
        play = findViewById<ImageView>(R.id.playTrack)
        currentTime = findViewById<TextView>(R.id.currentTime)
        url = currentTrack.previewUrl
        audioPlayerController = AudioPlayerController(mediaPlayer, play, currentTime, handler)
        audioPlayerController.preparePlayer(url)
        play.setOnClickListener {
            audioPlayerController.playbackControl()
        }

    }


    private fun createAlbumСover(track: Track) { // функция для отрисовки инфомрации о выбранном пользователем треке

        val trackLogo: ImageView = this.findViewById(R.id.trackLogo)

        val trackDurationValue: TextView = this.findViewById(R.id.trackDuration2)
        trackDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())

        val artistName: TextView = this.findViewById(R.id.trackArtist)
        artistName.text = track.artistName

        val trackName: TextView = this.findViewById(R.id.trackName)
        trackName.text = track.trackName

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

    override fun onPause() {
        super.onPause()
        if (audioPlayerController.playerState == 2) {
            audioPlayerController.pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerController.mediaPlayer.release()
    }

}