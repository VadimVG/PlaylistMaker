package com.example.playlistmaker.search;

data class Track(
        val trackId: String, // id трека
        val trackName: String, // Название композиции
        val artistName: String, // Имя исполнителя
        val trackTimeMillis: String, // Продолжительность трека
        val artworkUrl100: String // Ссылка на изображение обложки
) {
}