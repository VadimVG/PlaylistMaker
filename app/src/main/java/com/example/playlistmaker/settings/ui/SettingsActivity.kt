package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel


class SettingsActivity: AppCompatActivity()  {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel by lazy { ViewModelProvider(this)[SettingsViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsBack.setOnClickListener{ finish() }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(theme = checked)
        }
        settingsViewModel.isDarkThemeEnabled.observe(this) {
            binding.themeSwitcher.isChecked = it
            applyTheme(it)
        }

        binding.settingsShare.setOnClickListener{ settingsViewModel.shareApp() }
        binding.settingsSupport.setOnClickListener{ settingsViewModel.openSupport() }
        binding.settingsDocs.setOnClickListener{ settingsViewModel.openTerms() }

        settingsViewModel.actionCommand.observe(this) {intent ->
            intent?.let { startActivity(intent) }
            settingsViewModel.clearActionCommand()
        }
    }
    private fun applyTheme(isDarkTheme: Boolean) = setDefaultNightMode(if (isDarkTheme) MODE_NIGHT_YES else MODE_NIGHT_NO)

}