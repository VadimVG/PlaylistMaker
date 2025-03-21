package com.example.playlistmaker.di

import com.example.playlistmaker.audioplayer.presentation.view_model.AudioPlayerViewModel
import com.example.playlistmaker.media.presentation.view_model.FavoritesViewModel
import com.example.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        AudioPlayerViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}