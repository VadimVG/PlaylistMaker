package com.example.playlistmaker.data

import android.util.Log
import com.example.playlistmaker.ITunesApiResponseStatuses
import com.example.playlistmaker.data.dto.ITunesResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository {

    override fun searchTracks(expression: String): ArrayList<Track>? {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        Log.d("resultCode", response.resultCode.toString())
        if (response.resultCode == ITunesApiResponseStatuses.SUCCESS_REQUEST) {
            val list = (response as ITunesResponse).results.map {
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
        else if (response.resultCode == ITunesApiResponseStatuses.NETWORK_ERROR) return null
        else return ArrayList()
    }

}
