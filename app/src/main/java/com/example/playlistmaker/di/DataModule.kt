package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.SearchHistoryList
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }

    single {
        androidContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")// передача базовго url
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    factory { Gson() }


    single {
        androidContext().getSharedPreferences(
                SearchHistoryList.PREFERENCES_KEY,
                AppCompatActivity.MODE_PRIVATE
            )
    }

    factory {
        MediaPlayer()
    }
}