package com.example.playlistmaker.data.dto


import java.io.Serializable
import java.util.Date


data class TrackDto (

    var trackId: String = "", // id трека
    var trackName: String = "", // Название композиции
    var artistName: String = "", // Имя исполнителя
    var trackTimeMillis: String = "", // Продолжительность трека
    var artworkUrl100: String = "", // Ссылка на изображение обложки
    var collectionName: String? = "", // Название альбома
    var releaseDate: Date? = null, // Год релиза трека
    var primaryGenreName: String = "", // Жанр трека
    var country: String = "", // Страна исполнителя
    var previewUrl: String = "", // ссылка на трек

)