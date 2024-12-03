package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.data.ThemeTypeRepositoryImpl
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
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

    fun initApplication(application: Application) {
        this.application = application.applicationContext
    }

    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(application))
    }
    fun provideTracksInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }

    private fun getTrackHistorySharedPrefs(): SharedPreferences {
        return this.application.getSharedPreferences(
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
        return this.application.getSharedPreferences(
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