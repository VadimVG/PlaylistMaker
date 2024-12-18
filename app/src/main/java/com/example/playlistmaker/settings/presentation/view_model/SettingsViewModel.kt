package com.example.playlistmaker.settings.presentation.view_model

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
class SettingsViewModel: ViewModel() {

    private val themeTypeInteractor = Creator.provideThemeTypeInteractor()
    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> = _isDarkThemeEnabled

    private val sharingInteractor = Creator.provideSharingInteractor()
    private val _actionCommand = MutableLiveData<Intent?>()
    val actionCommand: LiveData<Intent?> = _actionCommand

    init {
        _isDarkThemeEnabled.value = themeTypeInteractor.get()
    }

    fun switchTheme(theme: Boolean) {
        themeTypeInteractor.save(theme)
        _isDarkThemeEnabled.value = theme
    }

    fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sharingInteractor.shareApp())
        }
        _actionCommand.postValue(Intent.createChooser(shareIntent, null))
    }

    fun openTerms() {
        val termsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(sharingInteractor.openTerms())
        }
        _actionCommand.postValue(termsIntent)
    }

    fun openSupport() {
        val emailData = sharingInteractor.openSupport()
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
        }
        _actionCommand.postValue(emailIntent)
    }

    fun clearActionCommand() {
        _actionCommand.postValue(null)
    }

}