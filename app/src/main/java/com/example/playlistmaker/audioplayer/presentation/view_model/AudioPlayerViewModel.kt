package com.example.playlistmaker.audioplayer.presentation.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.audioplayer.presentation.state.AudioPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private var mediaPlayer: MediaPlayer
): ViewModel() {
    private val _playerState = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> get() = _playerState

    private val _currentPosition = MutableLiveData<String>()
    val currentPosition: LiveData<String> get() = _currentPosition
    private var progressJob: Job? = null
    init {
        _playerState.value = AudioPlayerState.Default
    }

    fun preparePlayer(url: String) { // функция для подготовки медиаплеера к проигрыванию трека
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.postValue(AudioPlayerState.Prepared)
        }
        mediaPlayer.setOnCompletionListener {
            _playerState.postValue(AudioPlayerState.Prepared)
            _currentPosition.value = "00:00"
            progressJob?.cancel()
        }
    }
    fun playbackControl() { // выбор режима действия
        when(playerState.value) {
            is AudioPlayerState.Playing -> {
                pausePlayer()
                stopProgressUpdates()
            }
            is AudioPlayerState.Prepared, AudioPlayerState.Paused -> {
                startPlayer()
                startProgressUpdates()
            }
            AudioPlayerState.Default, null -> {}
        }
    }
    fun onPause() {
        if (playerState.value == AudioPlayerState.Playing) {
            pausePlayer()
        }
    }

    fun onDestroy() {
        progressJob?.cancel()
        mediaPlayer.release()
    }

    private fun startPlayer() { // вопроизведение трека и изменение кнопки "играть" на кнопку "пауза"
        mediaPlayer.start()
        _playerState.value =  AudioPlayerState.Playing
    }
    private fun pausePlayer() { // остановка вопроизведения трека и изменение кнопки "пауза" на кнопку "играть"
        mediaPlayer.pause()
        _playerState.value = AudioPlayerState.Paused
    }
    private fun setCurrentTrackTime() { // получение текущего времени продолжительности трека
        val res: String = SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(mediaPlayer.currentPosition)
        _currentPosition.value = res
    }

    private fun startProgressUpdates() {
        progressJob?.cancel() // Отменяем предыдущую корутину, если была

        progressJob = viewModelScope.launch {
            while (isActive && playerState.value == AudioPlayerState.Playing) {
                setCurrentTrackTime()
                delay(DELAY_MILLIS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private companion object {
        const val DELAY_MILLIS = 300L
    }

}