package com.example.playlistmaker.data.repository

import com.example.playlistmaker.ITunesApiResponseStatuses
import com.example.playlistmaker.data.dto.ITunesResponse
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.mapper.Mapper
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository {

    override fun searchTracks(expression: String): ArrayList<Track>? {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == ITunesApiResponseStatuses.SUCCESS_REQUEST) {
            val tracks: ArrayList<TrackDto> = (response as ITunesResponse).results
            return Mapper.fromTrackDtoToTrack(tracks)
        }
        else if (response.resultCode == ITunesApiResponseStatuses.NETWORK_ERROR) return null
        else return ArrayList()
    }

}
