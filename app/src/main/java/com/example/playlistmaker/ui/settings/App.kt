package com.example.playlistmaker.ui.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.ThemeSwitcher.APP_THEME_PREFERENCES
import com.example.playlistmaker.ThemeSwitcher.DARK_THEME


class App : Application() {
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()

        val preferences = getSharedPreferences(APP_THEME_PREFERENCES, MODE_PRIVATE)
        darkTheme = preferences.getBoolean(DARK_THEME, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
             else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}