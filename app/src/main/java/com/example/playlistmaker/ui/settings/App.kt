package com.example.playlistmaker.ui.settings

import android.app.Application
import android.content.Context
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.ThemeTypeInteractor


class App : Application() {
    private lateinit var themeTypeInteractor: ThemeTypeInteractor
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(applicationContext)
        themeTypeInteractor = Creator.provideThemeTypeInteractor()
        themeTypeInteractor.switch(themeTypeInteractor.get())
    }
}