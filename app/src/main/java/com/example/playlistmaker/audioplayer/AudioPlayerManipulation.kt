package com.example.playlistmaker.audioplayer

import android.media.MediaPlayer
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.AudioPlayerActivity
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerManipulation(private var mediaPlayer: MediaPlayer,
                              private var play: ImageView,
                              private var currentTime: TextView,
                              private var handler: Handler,
) {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        const val DELAY_MILLIS = 500L
    }
    private var playerState = STATE_DEFAULT
    private var runnable = Runnable { setCurrentTrackTime() }
    fun preparePlayer(url: String) { // функция для подготовки медиаплеера к проигрыванию трека
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            currentTime.text = "00:00"
            play.setImageResource(R.drawable.ic_play_audioplayer)
        }
    }
    private fun startPlayer() { // вопроизведение трека и изменение кнопки "играть" на кнопку "пауза"
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_pause_audioplayer)
        playerState = STATE_PLAYING
    }

    fun pausePlayer() { // остановка вопроизведения трека и изменение кнопки "пауза" на кнопку "играть"
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_play_audioplayer)
        playerState = STATE_PAUSED
    }
    fun playbackControl() { // выбор режима действия
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                stopTimer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                startTimer()
            }
        }
    }
    private fun setCurrentTrackTime() { // получение текущего времени продолжительности трека
        val res: String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        currentTime.text = res
    }

    private fun refreshCurrentTrackTime(): Runnable { // обновление времени трека на экране плеера
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
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

}