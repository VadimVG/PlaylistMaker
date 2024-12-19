package com.example.playlistmaker.audioplayer.presentation.state

sealed class AudioPlayerState {
        data object Default : AudioPlayerState()
        data object Prepared : AudioPlayerState()
        data object Playing : AudioPlayerState()
        data object Paused : AudioPlayerState()
}