package com.example.playlistmaker.ui.media

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R

class MediaActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val btback: ImageButton = findViewById<ImageButton>(R.id.mediaBack)
        btback.setOnClickListener{
            finish()
        }
    }
}