package com.example.playlistmaker


import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SearchActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val btback: ImageButton = findViewById<ImageButton>(R.id.searchBack)
        btback.setOnClickListener{
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
    }
}