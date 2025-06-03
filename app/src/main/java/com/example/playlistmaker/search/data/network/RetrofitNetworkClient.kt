package com.example.playlistmaker.search.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.ITunesApiResponseStatuses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val connectivityManager: ConnectivityManager,
    private val iTunesApi: ITunesApi
): NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        if (!isOnline()) {
            return Response().apply { resultCode = ITunesApiResponseStatuses.NETWORK_ERROR }
        }
        if(dto !is TrackSearchRequest) {
            return  Response().apply { resultCode = ITunesApiResponseStatuses.BAD_REQUEST }
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesApi.findSong(text = dto.expression)
                response.apply { resultCode = ITunesApiResponseStatuses.SUCCESS_REQUEST }
            } catch (e: Throwable) {
                Response().apply { resultCode = ITunesApiResponseStatuses.SERVER_ERROR }
            }
        }
    }

    private fun isOnline(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }

}