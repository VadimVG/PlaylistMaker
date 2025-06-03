package com.example.playlistmaker.search.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.TrackSearchViewState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            _state.value = TrackSearchViewState.Loading

            if (searchText.isNotEmpty()) {
                trackInteractor.searchTracks(searchText)
                    .catch { e ->
                        _state.value = TrackSearchViewState.Error(1)
                    }
                    .collect { foundTrack ->
                        _state.value = when {
                            foundTrack == null -> TrackSearchViewState.Error(1)
                            foundTrack.isEmpty() -> TrackSearchViewState.Error(2)
                            else -> TrackSearchViewState.Content(foundTrack)
                        }
                    }
            } else {
                getTrackSearchHistory()
            }
        }
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