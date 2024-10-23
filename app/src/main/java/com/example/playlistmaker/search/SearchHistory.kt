package com.example.playlistmaker.search

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    private var sharedPreferences: SharedPreferences
) {
    private val maxSize: Int = 10
    fun get(): ArrayList<Track> {
        val json = sharedPreferences.getString(SearchHistorySharedPrefsConst.ARRAY_LIST_KEY, null)
        val arrayListTutorialType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (json == null) ArrayList<Track>() else Gson().fromJson(json, arrayListTutorialType)
    }

    fun add(track: Track) {
        val arrayList: ArrayList<Track> = get()
        if (arrayList.size > 0) arrayList.removeIf { it -> it.trackId == track.trackId }
        arrayList.add(0, track)
        if (arrayList.size > maxSize) arrayList.removeAt(maxSize)
        save(arrayList)
        Log.d("SearchHistory add", track.toString())
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(SearchHistorySharedPrefsConst.ARRAY_LIST_KEY)
            .apply()
    }

    private fun save(tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(SearchHistorySharedPrefsConst.ARRAY_LIST_KEY, json)
            .apply()
    }
}