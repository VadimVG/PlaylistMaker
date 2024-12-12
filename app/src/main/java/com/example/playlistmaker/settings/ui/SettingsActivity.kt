package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity: AppCompatActivity()  {

    private lateinit var themeSwitcher: SwitchMaterial
    private val settingsViewModel by lazy { ViewModelProvider(this)[SettingsViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsBack: TextView = findViewById(R.id.settingsBack)
        settingsBack.setOnClickListener{ finish() }

        themeSwitcher = findViewById(R.id.themeSwitcher)
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(theme = checked)
        }
        settingsViewModel.isDarkThemeEnabled.observe(this) {
            themeSwitcher.isChecked = it
            applyTheme(it)
        }

        findViewById<TextView>(R.id.settingsShare)
            .setOnClickListener{
                val shareMessage = getString(R.string.share_message)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, null))
            }

        findViewById<TextView>(R.id.settingsSupport)
            .setOnClickListener{
                val message1 = getString(R.string.support_theme_message)
                val message2 = getString(R.string.support_text_message)

                val supportIntent = Intent(Intent.ACTION_SENDTO)
                supportIntent.data = Uri.parse("mailto:")
                supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.user_email)))
                supportIntent.putExtra(Intent.EXTRA_SUBJECT, message1)
                supportIntent.putExtra(Intent.EXTRA_TEXT, message2)
                startActivity(supportIntent)
            }

        findViewById<TextView>(R.id.settingsDocs)
            .setOnClickListener{
                val docsMessage = getString(R.string.docs_message)
                val docsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(docsMessage))
                startActivity(docsIntent)
            }
    }
    private fun applyTheme(isDarkTheme: Boolean) = setDefaultNightMode(if (isDarkTheme) MODE_NIGHT_YES else MODE_NIGHT_NO)

}