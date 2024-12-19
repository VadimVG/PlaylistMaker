package com.example.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.example.playlistmaker.ThemeSwitcher
import com.example.playlistmaker.settings.domain.api.ThemeTypeRepository

class ThemeTypeRepositoryImpl(private val sharedPreferences: SharedPreferences):
    ThemeTypeRepository {
    override fun get(): Boolean {
        return sharedPreferences.getBoolean(ThemeSwitcher.DARK_THEME, false)
    }

    override fun save(theme: Boolean) {
        theme.let {
            sharedPreferences.edit()
                .putBoolean(ThemeSwitcher.DARK_THEME, it)
                .apply()
        }
    }

    override fun switch(theme: Boolean) = setDefaultNightMode(if (theme) MODE_NIGHT_YES else MODE_NIGHT_NO)
}