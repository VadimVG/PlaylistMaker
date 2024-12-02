package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.ITunesResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.playlistmaker.ITunesApiResponseStatuses
import java.util.concurrent.Executors

class RetrofitNetworkClient(private val context: Context): NetworkClient {
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
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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