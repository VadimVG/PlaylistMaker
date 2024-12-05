package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track

object Mapper {

    fun fromTrackDtoToTrack(tracks: ArrayList<TrackDto>): ArrayList<Track> {
        val list: List<Track> = tracks.map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
        val arrayList = ArrayList<Track>()
        arrayList.addAll(list)
        return arrayList
    }
}