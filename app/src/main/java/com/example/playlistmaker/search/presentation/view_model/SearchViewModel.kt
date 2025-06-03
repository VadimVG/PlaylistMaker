package com.example.playlistmaker.search.presentation.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.TrackSearchViewState

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val historyInteractor: TrackHistoryInteractor
): ViewModel() {

    private var _state = MutableLiveData<TrackSearchViewState>()
    val state: LiveData<TrackSearchViewState> get() = _state


    fun defaultStateValue() {
        _state.value = TrackSearchViewState.Loading
    }
    fun search(searchText: String) {
        Log.d("test_handler", "search")
        if (searchText.isNotEmpty()) {
            trackInteractor.searchTracks(searchText,
                object : TrackInteractor.TrackConsumer {
                    override fun consume(foundTrack: ArrayList<Track>?) {
                        if (foundTrack == null) {
                            _state.postValue(TrackSearchViewState.Error(1))
                        } else if (foundTrack.isEmpty()) {
                            _state.postValue(TrackSearchViewState.Error(2))
                        } else if (foundTrack.isNotEmpty()) {
                            _state.postValue(TrackSearchViewState.Content(foundTrack))
                        }
                    }
                }
            )
        }
        else getTrackSearchHistory()
    }

    fun getTrackSearchHistory() {
        _state.postValue(TrackSearchViewState.History(historyInteractor.get()))
    }

    fun addTrackToHistory(track: Track) {
        historyInteractor.add(track)
    }

    fun clearTrackHistorySearch() {
        historyInteractor.clear()
    }
}