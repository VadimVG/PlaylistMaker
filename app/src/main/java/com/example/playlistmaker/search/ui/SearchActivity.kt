package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.audioplayer.ui.AudioPlayerActivity
import com.example.playlistmaker.AudioPlayerCurrentTrack.CURRENT_TRACK
import com.example.playlistmaker.DebounceSearchTime.CLICK_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.DebounceSearchTime.SEARCH_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.InputSearchText.SEARCH_KEY
import com.example.playlistmaker.InputSearchText.SEARCH_TEXT
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.presentation.state.TrackSearchViewState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity: AppCompatActivity() {
    private val searchViewModel by viewModel<SearchViewModel>()
    private lateinit var binding: ActivitySearchBinding
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
    private val searchRunnable = Runnable { searchViewModel.search(searchText) }
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets -> // пока непонятно, что это, уточнить
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputEditText = binding.inputEditText
        val clearButton = binding.clearIcon

        youSearch = binding.youSearch
        searchHistoryTracks = ArrayList<Track>()
        tracks = ArrayList<Track>()
        tracksAdapter = TrackAdapter(tracks)
        errorText = binding.errorMessage
        errorNotFound = binding.nothingFound
        errorWentWrong = binding.somethingWentWrong
        refreshBt = binding.refreshButton
        recyclerView = binding.trackList
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        progressBar = binding.progressBar
        clearHistory = binding.clearHistory

        binding.settingsBack.setOnClickListener{ finish() } // возвращение на главный экран

        searchViewModel.state.observe(this) { state ->
            when (state) {
                is TrackSearchViewState.Loading -> {
                    Log.d("TrackSearchViewState", "Loading")
                    showProgressBar()
                }

                is TrackSearchViewState.Error -> {
                    Log.d("TrackSearchViewState", "Error")
                    if (state.status == 1) {
                        showErrorMessage(1)
                    }
                    else showErrorMessage(2)
                }

                is TrackSearchViewState.Content -> {
                    Log.d("TrackSearchViewState", "Content")
                    tracks.addAll(state.tracks)
                    hideProgressBar()
                }

                is TrackSearchViewState.History -> {
                    Log.d("TrackSearchViewState", "History")
                    searchHistoryTracks = state.tracks
                    refreshSearchHistoryTracks()
                    hideProgressBar()
                }
            }
        }

        tracksAdapter.onItemClickListener = { track -> // сохраняем трек, на который кликнул пользователь, в файл sharedPreferences
            if (clickDebounce()) {
                searchViewModel.addTrackToHistory(track)
                startAudioPlayerActivity(track)
            }
        }

        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // скрываем клавиатуру
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0) // скрываем клавиатуру
            inputEditText.clearFocus() // удаление фокуса с EditText
            tracks.clear() // очистка списка треков
            recyclerView.adapter = tracksAdapter
            tracksAdapter.notifyDataSetChanged() // указываем адаптеру, что полученные ранее данные (список треков) изменились и следует перерисовать список на экране
            youSearch.isVisible = false
            clearHistory.isVisible = false
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus -> // отображение истории поиска
            if (hasFocus && searchText.isEmpty()) {
                searchViewModel.getTrackSearchHistory()
                refreshSearchHistoryTracks()
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { // отображение истории поиска
                searchText = p0.toString()
                clearButton.isVisible = searchText.isNotEmpty()
                if (inputEditText.hasFocus() && searchText.isEmpty() ) {
                    searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS = 1)
                    refreshSearchHistoryTracks()
                }
                else {
                    searchViewModel.defaultStateValue()
                    tracks.clear()
                    recyclerView.adapter  = tracksAdapter
                    searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS = SEARCH_DEBOUNCE_DELAY_MILLIS)
                    tracksAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        clearHistory.setOnClickListener{
            searchHistoryTracks.clear()
            searchViewModel.clearTrackHistorySearch()
            searchHistoryTracksAdapter.notifyDataSetChanged()
            searchHistoryTracksTextVisible()
        }

        refreshBt.setOnClickListener {
            showProgressBar()
            searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS = SEARCH_DEBOUNCE_DELAY_MILLIS)
        } // отправка повторного запроса, если что-то пошло не так

    }

    private fun refreshSearchHistoryTracks() {
        searchHistoryTracksTextVisible()
        searchHistoryTracksAdapter = TrackAdapter(searchHistoryTracks)
        recyclerView.adapter = searchHistoryTracksAdapter
        searchHistoryTracksAdapter.onItemClickListener = {track ->
            startAudioPlayerActivity(track)
        }
    }


    private fun showErrorMessage(status: Int) {
        when (status) {
            1 -> {
                clearHistory.isVisible = false
                youSearch.isVisible = false
                recyclerView.isVisible = false
                errorText.isVisible = true
                errorNotFound.isVisible = false
                errorWentWrong.isVisible = true
                refreshBt.isVisible = true
                progressBar.isVisible = false
                recyclerView.isVisible = true
                errorText.text = getString(R.string.something_went_wrong)
            }
            2-> {
                clearHistory.isVisible = false
                youSearch.isVisible = false
                recyclerView.isVisible = false
                errorText.isVisible = true
                errorNotFound.isVisible = true
                errorWentWrong.isVisible = false
                refreshBt.isVisible = false
                progressBar.isVisible = false
                recyclerView.isVisible = true
                errorText.text = getString(R.string.nothing_found)
            }
        }

    }

    private fun showProgressBar() {
        youSearch.isVisible = false
        clearHistory.isVisible = false
        recyclerView.isVisible = false
        progressBar.isVisible = true
        errorText.isVisible = false
        errorNotFound.isVisible = false
        errorWentWrong.isVisible = false
        refreshBt.isVisible = false
    }

    private fun hideProgressBar() {
        recyclerView.isVisible = true
        errorText.isVisible = false
        errorNotFound.isVisible = false
        errorWentWrong.isVisible = false
        refreshBt.isVisible = false
        progressBar.isVisible = false
        errorText.isVisible = false
        errorNotFound.isVisible = false
        errorWentWrong.isVisible = false
        refreshBt.isVisible = false
    }

    private fun searchHistoryTracksTextVisible() {
        youSearch.isVisible = searchHistoryTracks.isNotEmpty()
        clearHistory.isVisible = searchHistoryTracks.isNotEmpty()
    }

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