package com.example.playlistmaker.settings.ui

import android.app.Application
import android.content.Context
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.api.ThemeTypeInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule
            )
        }
        val themeTypeInteractor: ThemeTypeInteractor by inject()
        themeTypeInteractor.switch(themeTypeInteractor.get())

    }

}