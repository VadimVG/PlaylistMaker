package com.example.playlistmaker.domain.api

interface ThemeTypeInteractor {
    var darkTheme: Boolean
    fun get(): Boolean
    fun save(theme: Boolean)
    fun switch(theme: Boolean)
}