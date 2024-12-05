package com.example.playlistmaker.domain.api

interface ThemeTypeRepository {
    fun get(): Boolean
    fun save(theme: Boolean)
    fun switch(theme: Boolean)
}