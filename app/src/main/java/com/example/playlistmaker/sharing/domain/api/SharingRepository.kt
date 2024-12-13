package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingRepository {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
//    fun shareApp()
//    fun openTerms()
//    fun openSupport()
}