package com.example.playlistmaker.di

import com.example.playlistmaker.audioplayer.presentation.view_model.AudioPlayerViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        AudioPlayerViewModel(get())
    }

}