package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackHistoryInteractor {
    fun get(): ArrayList<Track>
    fun add(track: Track)
    fun clear()
    fun save(tracks: ArrayList<Track>)
}