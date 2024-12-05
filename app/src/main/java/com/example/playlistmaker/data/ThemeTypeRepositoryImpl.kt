package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.ThemeSwitcher
import com.example.playlistmaker.domain.api.ThemeTypeRepository

class ThemeTypeRepositoryImpl(private val sharedPreferences: SharedPreferences): ThemeTypeRepository  {
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

    override fun switch(theme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}