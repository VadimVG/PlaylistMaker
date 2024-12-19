package com.example.playlistmaker.search.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.playlistmaker.ITunesApiResponseStatuses

class RetrofitNetworkClient(private val connectivityManager: ConnectivityManager): NetworkClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")// передача базовго url
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)
    override fun doRequest(dto: Any): Response {
        if (isOnline() == false) {
            return Response().apply { resultCode = ITunesApiResponseStatuses.NETWORK_ERROR }
        }
        if (dto is TrackSearchRequest) {
            try {
                val response = iTunesApi.findSong(text = dto.expression).execute()
                val body = response.body() ?: Response()
                return body.apply { resultCode = response.code() }
            }
            catch (e: Exception) { return Response().apply { resultCode = ITunesApiResponseStatuses.NETWORK_ERROR }}
        }
        else {
            return  Response().apply { resultCode = ITunesApiResponseStatuses.BAD_REQUEST }
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