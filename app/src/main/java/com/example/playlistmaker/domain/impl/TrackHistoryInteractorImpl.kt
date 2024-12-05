package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track

class TrackHistoryInteractorImpl(private val repository: TrackHistoryRepository): TrackHistoryInteractor {
    override fun get(): ArrayList<Track> {
        return repository.get()
    }

    override fun add(track: Track) {
        repository.add(track)
    }

    override fun save(tracks: ArrayList<Track>) {
        repository.save(tracks)
    }

    override fun clear() {
        repository.clear()
    }
}