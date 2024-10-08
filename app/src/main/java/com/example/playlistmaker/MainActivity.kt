package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btSearch: Button = findViewById(R.id.btSearch)
        val textClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        btSearch.setOnClickListener(textClickListener)

        val btMedia: Button = findViewById(R.id.btMedia)
        btMedia.setOnClickListener{
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val btSettings: Button = findViewById(R.id.btSettings)
        btSettings.setOnClickListener{
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
        }
    }
}