package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btback: ImageButton = findViewById<ImageButton>(R.id.settingsBack)
        btback.setOnClickListener{
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
    }
}