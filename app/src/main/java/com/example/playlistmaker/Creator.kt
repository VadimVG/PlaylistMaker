package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.SearchHistoryList
import com.example.playlistmaker.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.domain.impl.TrackHistoryInteractorImpl

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(application))
    }

    fun provideTracksInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }

    private fun getTrackHistoryRepository(sharedPreferences: SharedPreferences): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(sharedPreferences = sharedPreferences)
    }

    fun provideTracksHistoryInteractor(sharedPreferences: SharedPreferences): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getTrackHistoryRepository(sharedPreferences))
    }
}