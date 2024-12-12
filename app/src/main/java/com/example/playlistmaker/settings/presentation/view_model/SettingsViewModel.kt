package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
class SettingsViewModel: ViewModel() {

    private val themeTypeInteractor = Creator.provideThemeTypeInteractor()

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> = _isDarkThemeEnabled

    init {
        _isDarkThemeEnabled.value = themeTypeInteractor.get()
    }

    fun switchTheme(theme: Boolean) {
        themeTypeInteractor.save(theme)
        _isDarkThemeEnabled.value = theme
    }

}