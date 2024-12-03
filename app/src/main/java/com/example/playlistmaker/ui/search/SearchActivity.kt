package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioplayer.AudioPlayerActivity
import com.example.playlistmaker.AudioPlayerCurrentTrack.CURRENT_TRACK
import com.example.playlistmaker.DebounceSearchTime.CLICK_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.DebounceSearchTime.SEARCH_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.InputSearchText.SEARCH_KEY
import com.example.playlistmaker.InputSearchText.SEARCH_TEXT
import com.example.playlistmaker.domain.api.TrackHistoryInteractor

class SearchActivity: AppCompatActivity() {
    private var searchText : String = SEARCH_TEXT

    private lateinit var tracks: ArrayList<Track>
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var errorText: TextView
    private lateinit var errorNotFound: ImageView
    private lateinit var errorWentWrong: ImageView
    private lateinit var refreshBt: Button
    private lateinit var clearHistory: Button
    private lateinit var youSearch: TextView
    private lateinit var searchHistoryTracks: ArrayList<Track>
    private lateinit var searchHistoryTracksAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private lateinit var progressBar: ProgressBar

    private lateinit var trackInteractor: TrackInteractor
    private lateinit var trackHistoryInteractor: TrackHistoryInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets -> // пока непонятно, что это, уточнить
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvBack = findViewById<TextView>(R.id.settingsBack)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        trackInteractor = Creator.provideTracksInteractor()
        trackHistoryInteractor = Creator.provideTracksHistoryInteractor()
        youSearch = findViewById<TextView>(R.id.youSearch)
        tracks = ArrayList<Track>()
        tracksAdapter = TrackAdapter(tracks)
        errorText = findViewById(R.id.errorMessage)
        errorNotFound = findViewById(R.id.nothing_found)
        errorWentWrong = findViewById(R.id.something_went_wrong)
        refreshBt = findViewById(R.id.refreshButton)
        recyclerView = findViewById<RecyclerView>(R.id.trackList)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        tracksAdapter.onItemClickListener = { track -> // сохраняем трек, на который кликнул пользователь, в файл sharedPreferences
            if (clickDebounce()) {
                trackHistoryInteractor.add(track)
                startAudioPlayerActivity(track)
            }
        }

        progressBar = findViewById(R.id.progressBar)

        clearHistory = findViewById(R.id.clearHistory)

        tvBack.setOnClickListener{ finish() } // возвращение на главный экран

        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // скрываем клавиатуру
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0) // скрываем клавиатуру
            inputEditText.clearFocus() // удаление фокуса с EditText
            tracks.clear() // очистка списка треков
            tracksAdapter.notifyDataSetChanged() // указываем адаптеру, что полученные ранее данные (список треков) изменились и следует перерисовать список на экране
            youSearch.visibility = View.GONE
            clearHistory.visibility = View.GONE
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus -> // отображение истории поиска
            if (hasFocus && searchText.isEmpty()) {
                searchHistoryTracks = trackHistoryInteractor.get()
                youSearch.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
                clearHistory.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
                searchHistoryTracksAdapter = TrackAdapter(searchHistoryTracks)
                recyclerView.adapter = searchHistoryTracksAdapter
                searchHistoryTracksAdapter.onItemClickListener = {track ->
                    startAudioPlayerActivity(track)
                }
            }
            else {
                recyclerView.adapter = tracksAdapter
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { // отображение истории поиска
                searchText = p0.toString()
                clearButton.visibility = clearButtonVisibility(searchText)
                if (inputEditText.hasFocus() && searchText.isEmpty() ) {
                    searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS = 1)
                    youSearch.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
                    recyclerView.adapter = searchHistoryTracksAdapter
                    clearHistory.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
                }
                else {
                    youSearch.visibility = View.GONE
                    clearHistory.visibility = View.GONE
                    recyclerView.adapter  = tracksAdapter
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS = SEARCH_DEBOUNCE_DELAY_MILLIS)
                }
                errorText.visibility = View.GONE
                errorNotFound.visibility = View.GONE
                errorWentWrong.visibility = View.GONE
                refreshBt.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        clearHistory.setOnClickListener{
            searchHistoryTracks.clear()
            trackHistoryInteractor.clear()
            searchHistoryTracksAdapter.notifyDataSetChanged()
            youSearch.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
            clearHistory.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
        }

        refreshBt.setOnClickListener {
            errorWentWrong.visibility = View.GONE
            refreshBt.visibility = View.GONE
            errorText.visibility = View.GONE
            search()
        } // отправка повторного запроса, если что-то пошло не так

    }


    private fun search(){
        if (searchText.isNotEmpty()) {
            trackInteractor.searchTracks(searchText,
                object : TrackInteractor.TrackConsumer {
                    override fun consume(foundTrack: ArrayList<Track>?) {
                        handler.post {
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            if (foundTrack == null){
                                showErrorMessage(getString(R.string.something_went_wrong), 2)
                            }
                            else if (foundTrack.isNotEmpty()) {
                                tracks.addAll(foundTrack)
                                tracksAdapter.notifyDataSetChanged()
                            }
                            else if (foundTrack.isEmpty()) showErrorMessage(getString(R.string.nothing_found), 1)
                        }
                    }
                }
            )
        }
        else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }


    private fun showErrorMessage(text: String, type: Int) {
        if (text.isNotEmpty()) {
            clearHistory.visibility = View.GONE
            youSearch.visibility = View.GONE
            recyclerView.visibility = View.GONE
            errorText.visibility = View.VISIBLE
            errorNotFound.visibility = View.GONE
            errorWentWrong.visibility = View.GONE
            refreshBt.visibility = View.GONE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            errorText.text = text
            if (type == 1) errorNotFound.visibility = View.VISIBLE
            else {
                errorWentWrong.visibility = View.VISIBLE
                refreshBt.visibility = View.VISIBLE
            }
        } else {
            errorText.visibility = View.GONE
            errorNotFound.visibility = View.GONE
            errorWentWrong.visibility = View.GONE
            refreshBt.visibility = View.GONE
        }
    }


    private fun clearButtonVisibility(s: CharSequence?): Int = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE // настройка видимости кнопки для удаления текста из строки поиска

    override fun onSaveInstanceState(outState: Bundle) { // сохранение введенного текста из строки поиска (состояния) перед уничтожением активити
        outState.putString(SEARCH_KEY, searchText)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // восстановление введенного текста из строки поиска после создания активити
        searchText = savedInstanceState.getString(SEARCH_KEY).toString()
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun startAudioPlayerActivity(track: Track) {
        val audioplayerIntent = Intent(this, AudioPlayerActivity::class.java)
        audioplayerIntent.putExtra(CURRENT_TRACK, track)
        startActivity(audioplayerIntent)
    }

    private fun clickDebounce(): Boolean { // отложенное вополнение какого-либо действия
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    private fun searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS: Long) { //  отправка запроса через указанное время
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

}