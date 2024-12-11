package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.SearchHistoryList
import com.example.playlistmaker.ThemeSwitcher
import com.example.playlistmaker.data.repository.ThemeTypeRepositoryImpl
import com.example.playlistmaker.data.repository.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.ThemeTypeInteractor
import com.example.playlistmaker.domain.api.ThemeTypeRepository
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.domain.impl.ThemeTypeInteractorImpl
import com.example.playlistmaker.domain.impl.TrackHistoryInteractorImpl

object Creator {
    private lateinit var application: Context

    fun initApplication(application: Context) {
        Creator.application = application.applicationContext
    }

    private fun getConnectivityManager(): ConnectivityManager {
        return application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(getConnectivityManager()))
    }
    fun provideTracksInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }

    private fun getTrackHistorySharedPrefs(): SharedPreferences {
        return application.getSharedPreferences(
            SearchHistoryList.PREFERENCES_KEY,
            AppCompatActivity.MODE_PRIVATE
        )
    }
    private fun getTrackHistoryRepository(sharedPreferences: SharedPreferences): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(sharedPreferences = sharedPreferences)
    }
    fun provideTracksHistoryInteractor(): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getTrackHistoryRepository(getTrackHistorySharedPrefs()))
    }

    private fun getThemeTypeSharedPrefs(): SharedPreferences {
        return application.getSharedPreferences(
            ThemeSwitcher.APP_THEME_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
    }
    private fun getThemeTypeRepository(sharedPreferences: SharedPreferences): ThemeTypeRepository {
        return ThemeTypeRepositoryImpl(sharedPreferences = sharedPreferences)
    }
    fun provideThemeTypeInteractor(): ThemeTypeInteractor {
        return ThemeTypeInteractorImpl(getThemeTypeRepository(getThemeTypeSharedPrefs()))
    }

}