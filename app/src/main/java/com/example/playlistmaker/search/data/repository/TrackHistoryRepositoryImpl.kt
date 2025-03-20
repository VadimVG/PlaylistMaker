package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.SearchHistoryList
import com.example.playlistmaker.search.domain.api.TrackHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackHistoryRepositoryImpl(
    private var sharedPreferences: SharedPreferences,
    private val gson: Gson
):
    TrackHistoryRepository {

    private val maxSize: Int = 10
    override fun get(): ArrayList<Track> {
        val json = sharedPreferences.getString(SearchHistoryList.ARRAY_LIST_KEY, null)
        val arrayListTutorialType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (json == null) ArrayList<Track>() else gson.fromJson(json, arrayListTutorialType)
    }

    override fun add(track: Track) {
        val arrayList: ArrayList<Track> = get()
        if (arrayList.size > 0) arrayList.removeIf { it -> it.trackId == track.trackId }
        arrayList.add(0, track)
        if (arrayList.size > maxSize) arrayList.removeAt(maxSize)
        save(arrayList)
    }

    override fun clear() {
        sharedPreferences.edit()
            .remove(SearchHistoryList.ARRAY_LIST_KEY)
            .apply()
    }

    override fun save(tracks: ArrayList<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(SearchHistoryList.ARRAY_LIST_KEY, json)
            .apply()
    }
}