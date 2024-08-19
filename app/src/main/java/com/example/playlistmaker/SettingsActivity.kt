package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tvBack: TextView = findViewById<TextView>(R.id.settingsBack)
        tvBack.setOnClickListener{
            finish()
        }

        val btShare: ImageButton = findViewById<ImageButton>(R.id.settingsShare)
        btShare.setOnClickListener{
            val shareMessage = getString(R.string.share_message)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, null))
        }

        val btSupport: ImageButton = findViewById(R.id.settingsSupport)
        btSupport.setOnClickListener{
            val message1 = getString(R.string.support_theme_message)
            val message2 = getString(R.string.support_text_message)

            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.user_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, message1)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message2)
            startActivity(supportIntent)
        }

        val btDocs: ImageButton = findViewById<ImageButton>(R.id.settingsDocs)
        btDocs.setOnClickListener{
            val docsMessage = getString(R.string.docs_message)
            val docsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(docsMessage))
            startActivity(docsIntent)
        }

    }
}