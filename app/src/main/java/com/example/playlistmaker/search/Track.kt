package com.example.playlistmaker.search;

data class Track(
        val trackName: String, // Название композиции
        val artistName: String, // Имя исполнителя
        val trackDuration: String, // Продолжительность трека
        val artworkUrl100: String // Ссылка на изображение обложки
) {
}