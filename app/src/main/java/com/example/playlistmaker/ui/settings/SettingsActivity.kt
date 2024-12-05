package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ThemeTypeInteractor
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity: AppCompatActivity()  {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var themeTypeInteractor: ThemeTypeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tvBack: TextView = findViewById<TextView>(R.id.settingsBack)
        tvBack.setOnClickListener{ finish() }

        themeSwitcher = findViewById(R.id.themeSwitcher)
        themeTypeInteractor = Creator.provideThemeTypeInteractor()
        themeSwitcher.isChecked = themeTypeInteractor.get()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            themeTypeInteractor.switch(theme = checked)
            themeTypeInteractor.save(theme = checked)
        }

        val tvShare: TextView = findViewById<TextView>(R.id.settingsShare)
        tvShare.setOnClickListener{
            val shareMessage = getString(R.string.share_message)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, null))
        }

        val tvSupport: TextView =  findViewById<TextView>(R.id.settingsSupport)
        tvSupport.setOnClickListener{
            val message1 = getString(R.string.support_theme_message)
            val message2 = getString(R.string.support_text_message)

            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.user_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, message1)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message2)
            startActivity(supportIntent)
        }

        val tvDocs: TextView = findViewById<TextView>(R.id.settingsDocs)
        tvDocs.setOnClickListener{
            val docsMessage = getString(R.string.docs_message)
            val docsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(docsMessage))
            startActivity(docsIntent)
        }
    }

}