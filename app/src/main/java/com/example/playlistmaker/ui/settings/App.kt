package com.example.playlistmaker.ui.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ThemeSwitcher.APP_THEME_PREFERENCES
import com.example.playlistmaker.ThemeSwitcher.DARK_THEME
import com.example.playlistmaker.domain.api.ThemeTypeInteractor


class App : Application() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var themeTypeInteractor: ThemeTypeInteractor
    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(APP_THEME_PREFERENCES, MODE_PRIVATE)
        Creator.initApplication(this)
        themeTypeInteractor = Creator.provideThemeTypeInteractor(sharedPreferences)
        themeTypeInteractor.switch(themeTypeInteractor.get())
    }
}