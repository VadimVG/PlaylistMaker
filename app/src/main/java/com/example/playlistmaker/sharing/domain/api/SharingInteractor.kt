package com.example.playlistmaker.sharing.domain.api

import android.content.Intent
import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingInteractor {
    fun shareApp(): String
    fun openTerms(): String
    fun openSupport(): EmailData
}