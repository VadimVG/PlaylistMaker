package com.example.playlistmaker.search



import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ITunesApi {
    @GET("/search?entity=song")
    fun findSong(
        @Query("term") text:String
    ): Call<ITunesResponse> // аннотация @Query позволяет фильтровать запрос по параметру
}