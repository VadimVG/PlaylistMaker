package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackHistoryRepository {
    fun get(): ArrayList<Track>
    fun add(track: Track)
    fun clear()
    fun save(tracks: ArrayList<Track>)
}