package com.example.playlistmaker.audioplayer.ui


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
import com.example.playlistmaker.R
import com.example.playlistmaker.AudioPlayerCurrentTrack
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAudioplayerBinding
    private var mediaPlayer = MediaPlayer()
    private lateinit var audioPlayerController: AudioPlayerController
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.audioplayerBack.setOnClickListener{ finish() } // возвращение на главный экран

        val currentTrack=intent.getSerializableExtra(AudioPlayerCurrentTrack.CURRENT_TRACK) as Track // получение данных о треке, на который кликнул пользователь в меню поиска
        createAlbumСover(currentTrack) // отрисовка экрана плеера с данными о треке

        val play = binding.playTrack
        val currentTime = binding.currentTime
        audioPlayerController = AudioPlayerController(mediaPlayer, play, currentTime, handler)
        audioPlayerController.preparePlayer(currentTrack.previewUrl)
        play.setOnClickListener {
            audioPlayerController.playbackControl()
        }

    }

    private fun createAlbumСover(track: Track) { // функция для отрисовки инфомрации о выбранном пользователем треке

        val trackLogo: ImageView = this.binding.trackLogo

        val trackDurationValue: TextView = this.binding.trackDuration2
        trackDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())

        val artistName: TextView = this.binding.trackArtist
        artistName.text = track.artistName

        val trackName: TextView = this.binding.trackName
        trackName.text = track.trackName

        val trackAlbum: TextView = this.binding.trackAlbum
        val trackAlbumValue: TextView = this.binding.trackAlbum2
        if(track.collectionName.isNullOrEmpty()) { trackAlbum.visibility = View.GONE }
        else trackAlbumValue.text = track.collectionName

        val trackYearValue: TextView = this.binding.trackYear2
        trackYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate!!)

        val trackGenreValue: TextView = this.binding.trackGenre2
        trackGenreValue.text = track.primaryGenreName

        val trackCountryValue: TextView = this.binding.trackCountry2
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