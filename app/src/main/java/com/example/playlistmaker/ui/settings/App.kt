package com.example.playlistmaker.ui.settings

import android.app.Application
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.ThemeTypeInteractor


class App : Application() {
    private lateinit var themeTypeInteractor: ThemeTypeInteractor
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        themeTypeInteractor = Creator.provideThemeTypeInteractor()
        themeTypeInteractor.switch(themeTypeInteractor.get())
    }
}