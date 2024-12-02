package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.ThemeType

interface ThemeTypeInteractor {
    var darkTheme: Boolean
    fun get(): ThemeType
    fun save(theme: ThemeType)
    fun switch(theme: ThemeType)
}