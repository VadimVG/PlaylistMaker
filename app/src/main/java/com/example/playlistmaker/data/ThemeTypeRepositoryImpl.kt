package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.ThemeSwitcher
import com.example.playlistmaker.domain.api.ThemeTypeRepository
import com.example.playlistmaker.domain.models.ThemeType

class ThemeTypeRepositoryImpl(private val sharedPreferences: SharedPreferences): ThemeTypeRepository  {
    override fun get(): ThemeType {
        var darkTheme = ThemeType(sharedPreferences.getBoolean(ThemeSwitcher.DARK_THEME, false))
        return darkTheme
    }

    override fun save(theme: ThemeType) {
        theme.type?.let {
            sharedPreferences.edit()
                .putBoolean(ThemeSwitcher.DARK_THEME, it)
                .apply()
        }
    }

    override fun switch(theme: ThemeType) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme.type == true) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}