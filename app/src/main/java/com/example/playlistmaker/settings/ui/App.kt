package com.example.playlistmaker.settings.ui

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.ThemeTypeInteractor


class App : Application() {
    private lateinit var themeTypeInteractor: ThemeTypeInteractor
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(applicationContext)
        themeTypeInteractor = Creator.provideThemeTypeInteractor()
        themeTypeInteractor.switch(themeTypeInteractor.get())
    }
}