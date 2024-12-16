package com.example.playlistmaker.search.presentation.state

import com.example.playlistmaker.search.domain.models.Track

sealed class TrackSearchViewState {
    data object Loading : TrackSearchViewState()
    data class Error(val status: Int) : TrackSearchViewState()
    data class Content(val tracks: ArrayList<Track>) : TrackSearchViewState()
    data class History(val tracks: ArrayList<Track>) : TrackSearchViewState()
}