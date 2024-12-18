package com.example.playlistmaker.audioplayer.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.AudioPlayerCurrentTrack
import com.example.playlistmaker.audioplayer.presentation.state.AudioPlayerState
import com.example.playlistmaker.audioplayer.presentation.view_model.AudioPlayerViewModel
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity: AppCompatActivity() {
    private val audioPlayerViewModel by lazy { ViewModelProvider(this)[AudioPlayerViewModel::class.java]}
    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.audioplayerBack.setOnClickListener{ finish() } // возвращение на главный экран

        val play = binding.playTrack
        val currentTrack=intent.getSerializableExtra(AudioPlayerCurrentTrack.CURRENT_TRACK) as Track // получение данных о треке, на который кликнул пользователь в меню поиска
        createAlbumCover(currentTrack) // отрисовка экрана плеера с данными о треке
        audioPlayerViewModel.preparePlayer(currentTrack.previewUrl)
        audioPlayerViewModel.playerState.observe(this) { state ->
            when (state) {
                is AudioPlayerState.Playing -> {
                    Log.d("AudioPlayerViewModel", "Playing")
                    play.setImageResource(R.drawable.ic_pause_audioplayer)
                }
                is AudioPlayerState.Paused -> {
                    Log.d("AudioPlayerViewModel", "Paused")
                    play.setImageResource(R.drawable.ic_play_audioplayer)
                }
                is AudioPlayerState.Default, AudioPlayerState.Prepared -> {
                    Log.d("AudioPlayerViewModel", "Default / Prepared")
                    play.setImageResource(R.drawable.ic_play_audioplayer)}
            }
        }

        audioPlayerViewModel.currentPosition.observe(this) { position ->
            binding.currentTime.text = position
        }

        play.setOnClickListener {
            audioPlayerViewModel.playbackControl()
        }

    }

    private fun createAlbumCover(track: Track) { // функция для отрисовки инфомрации о выбранном пользователем треке
        val trackLogo: ImageView = binding.trackLogo
        val trackDurationValue: TextView = binding.trackDuration2
        trackDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())
        val artistName: TextView = binding.trackArtist
        artistName.text = track.artistName
        val trackName: TextView = binding.trackName
        trackName.text = track.trackName
        val trackAlbum: TextView = binding.trackAlbum
        val trackAlbumValue: TextView = binding.trackAlbum2
        if(track.collectionName.isNullOrEmpty()) { trackAlbum.visibility = View.GONE }
        else trackAlbumValue.text = track.collectionName
        val trackYearValue: TextView = binding.trackYear2
        trackYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate!!)
        val trackGenreValue: TextView = binding.trackGenre2
        trackGenreValue.text = track.primaryGenreName
        val trackCountryValue: TextView = binding.trackCountry2
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
        audioPlayerViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerViewModel.onDestroy()
    }

}