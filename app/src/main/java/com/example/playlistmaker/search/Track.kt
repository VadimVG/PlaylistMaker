package com.example.playlistmaker.search;

import java.io.Serializable
import java.util.Date

class Track: Serializable {

        var trackId: String = "" // id трека
        var trackName: String = "" // Название композиции
        var artistName: String = "" // Имя исполнителя
        var trackTimeMillis: String = "" // Продолжительность трека
        var artworkUrl100: String = "" // Ссылка на изображение обложки
        var collectionName: String? = "" // Название альбома
        var releaseDate: Date? = null // Год релиза трека
        var primaryGenreName: String = "" // Жанр трека
        var country: String = "" // Страна исполнителя

        constructor(
                trackId: String,
                trackName: String,
                artistName: String,
                trackTimeMillis: String,
                artworkUrl100: String,
                collectionName: String?,
                releaseDate: Date?,
                primaryGenreName: String,
                country: String
        ){
                this.trackId = trackId
                this.trackName = trackName
                this.artistName = artistName
                this.trackTimeMillis = trackTimeMillis
                this.artworkUrl100 = artworkUrl100
                this.collectionName= collectionName
                this.releaseDate= releaseDate
                this.primaryGenreName= primaryGenreName
                this.country= country
        }
        constructor()
}