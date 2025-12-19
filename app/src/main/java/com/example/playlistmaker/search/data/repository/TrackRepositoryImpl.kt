package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.ITunesApiResponseStatuses
import com.example.playlistmaker.search.data.dto.ITunesResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.mapper.Mapper
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository {

    override fun searchTracks(expression: String): Flow<ArrayList<Track>?> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            ITunesApiResponseStatuses.SUCCESS_REQUEST -> {
                val tracks = (response as ITunesResponse).results
                val mappedTracks = Mapper.fromTrackDtoToTrack(tracks)
                emit(mappedTracks)
            }
            ITunesApiResponseStatuses.NETWORK_ERROR -> {
                emit(null)
            }
            else -> {
                emit(ArrayList())
            }
        }
    }

}
