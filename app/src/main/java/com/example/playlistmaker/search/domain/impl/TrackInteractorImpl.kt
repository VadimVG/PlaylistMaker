package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTracks(expression: String): Flow<ArrayList<Track>?> {
        return repository.searchTracks(expression)
    }
}