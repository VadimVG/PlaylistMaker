package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeTypeInteractor
import com.example.playlistmaker.domain.api.ThemeTypeRepository
import com.example.playlistmaker.domain.models.ThemeType

class ThemeTypeInteractorImpl(private val repository: ThemeTypeRepository): ThemeTypeInteractor {
    override var darkTheme = false
    override fun get(): ThemeType {
        return repository.get()
    }

    override fun save(theme: ThemeType) {
        repository.save(theme)
    }

    override fun switch(theme: ThemeType) {
        repository.switch(theme)
    }
}