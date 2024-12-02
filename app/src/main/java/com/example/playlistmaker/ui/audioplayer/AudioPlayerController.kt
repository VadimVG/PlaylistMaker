package com.example.playlistmaker.ui.audioplayer

import android.media.MediaPlayer
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.playlistmaker.AudioPlayerCurrentTrack

class AudioPlayerController(var mediaPlayer: MediaPlayer,
                            private var play: ImageView,
                            private var currentTime: TextView,
                            private var handler: Handler,
) {

    var playerState = AudioPlayerCurrentTrack.STATE_DEFAULT
    private var runnable = Runnable { setCurrentTrackTime() }
    fun preparePlayer(url: String) { // функция для подготовки медиаплеера к проигрыванию трека
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = AudioPlayerCurrentTrack.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = AudioPlayerCurrentTrack.STATE_PREPARED
            currentTime.text = "00:00"
            play.setImageResource(R.drawable.ic_play_audioplayer)
        }
    }
    private fun startPlayer() { // вопроизведение трека и изменение кнопки "играть" на кнопку "пауза"
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_pause_audioplayer)
        playerState = AudioPlayerCurrentTrack.STATE_PLAYING
    }

    fun pausePlayer() { // остановка вопроизведения трека и изменение кнопки "пауза" на кнопку "играть"
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_play_audioplayer)
        playerState = AudioPlayerCurrentTrack.STATE_PAUSED
    }
    fun playbackControl() { // выбор режима действия
        when(playerState) {
            AudioPlayerCurrentTrack.STATE_PLAYING -> {
                pausePlayer()
                stopTimer()
            }
            AudioPlayerCurrentTrack.STATE_PREPARED, AudioPlayerCurrentTrack.STATE_PAUSED -> {
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
                if (playerState == AudioPlayerCurrentTrack.STATE_PLAYING) {
                    setCurrentTrackTime()
                    handler.postDelayed(this, AudioPlayerCurrentTrack.DELAY_MILLIS)
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