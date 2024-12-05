package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeTypeInteractor
import com.example.playlistmaker.domain.api.ThemeTypeRepository

class ThemeTypeInteractorImpl(private val repository: ThemeTypeRepository): ThemeTypeInteractor {
    override var darkTheme = false
    override fun get(): Boolean {
        return repository.get()
    }

    override fun save(theme: Boolean) {
        repository.save(theme)
    }

    override fun switch(theme: Boolean) {
        repository.switch(theme)
    }
}