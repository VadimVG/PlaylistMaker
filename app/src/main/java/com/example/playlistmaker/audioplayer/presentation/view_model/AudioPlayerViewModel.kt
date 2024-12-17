package com.example.playlistmaker.audioplayer.presentation.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.audioplayer.presentation.state.AudioPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel: ViewModel() {
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable { setCurrentTrackTime() }

    private val _playerState = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> get() = _playerState

    private val _currentPosition = MutableLiveData<String>()
    val currentPosition: LiveData<String> get() = _currentPosition
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
        }
    }

    private fun startPlayer() { // вопроизведение трека и изменение кнопки "играть" на кнопку "пауза"
        mediaPlayer.start()
        _playerState.value =  AudioPlayerState.Playing
    }

    fun pausePlayer() { // остановка вопроизведения трека и изменение кнопки "пауза" на кнопку "играть"
        mediaPlayer.pause()
        _playerState.value = AudioPlayerState.Paused
    }

    fun playbackControl() { // выбор режима действия
        when(playerState.value) {
            is AudioPlayerState.Playing -> {
                pausePlayer()
                stopTimer()
            }
            is AudioPlayerState.Prepared, AudioPlayerState.Paused -> {
                startPlayer()
                startTimer()
            }
            AudioPlayerState.Default, null -> {}
        }
    }

    private fun setCurrentTrackTime() { // получение текущего времени продолжительности трека
        val res: String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        _currentPosition.value = res
    }

    private fun refreshCurrentTrackTime(): Runnable { // обновление времени трека на экране плеера
        return object : Runnable {
            override fun run() {
                if (playerState.value == AudioPlayerState.Playing) {
                    setCurrentTrackTime()
                    handler.postDelayed(this, DELAY_MILLIS)
                }
            }
        }
    }

    private fun startTimer() { // начало работы таймера для отсчета времени трека
        runnable = refreshCurrentTrackTime()
        handler.post(runnable)
    }

    private fun stopTimer() { // остановка таймера
        runnable.let { handler.removeCallbacks(it) }
    }

    fun onPause() {
        if (playerState.value == AudioPlayerState.Playing) {
            pausePlayer()
        }
    }

    fun onDestroy() {
        mediaPlayer.release()
    }

    companion object {
        const val DELAY_MILLIS = 500L
    }

}