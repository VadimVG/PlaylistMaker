package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.audioplayer.ui.AudioPlayerActivity
import com.example.playlistmaker.AudioPlayerCurrentTrack.CURRENT_TRACK
import com.example.playlistmaker.DebounceSearchTime.CLICK_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.DebounceSearchTime.SEARCH_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.InputSearchText.SEARCH_TEXT
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.presentation.state.TrackSearchViewState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by viewModel<SearchViewModel>()
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
    private var shouldShowHistory = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        progressBar = binding.progressBar
        clearHistory = binding.clearHistory

        searchViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrackSearchViewState.Loading -> {
                    showProgressBar()
                    Log.d("my_test", "TrackSearchViewState.Loading")
                }
                is TrackSearchViewState.Error -> {
                    if (state.status == 1) showErrorMessage(1)
                    else showErrorMessage(2)
                    Log.d("my_test", "TrackSearchViewState.Error")
                }
                is TrackSearchViewState.Content -> {
                    tracks.addAll(state.tracks)
                    hideProgressBar()
                    Log.d("my_test", "TrackSearchViewState.Content")
                }
                is TrackSearchViewState.History -> {
                    searchHistoryTracks = state.tracks
                    refreshSearchHistoryTracks()
                    hideProgressBar()
                    Log.d("my_test", "TrackSearchViewState.History")
                }
            }
        }

        tracksAdapter.onItemClickListener = { track ->
            if (clickDebounce()) {
                searchViewModel.addTrackToHistory(track)
                startAudioPlayerActivity(track)
            }
        }

        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT)
            inputEditText.clearFocus()
            tracks.clear()
            recyclerView.adapter = tracksAdapter
            tracksAdapter.notifyDataSetChanged()
            youSearch.isVisible = false
            clearHistory.isVisible = false
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            shouldShowHistory = hasFocus && searchText.isEmpty()
            if (shouldShowHistory) {
                searchViewModel.getTrackSearchHistory()
            } else {
                resetToDefaultAdapter()
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchText = p0.toString()
                clearButton.isVisible = searchText.isNotEmpty()
                if (inputEditText.hasFocus() && searchText.isEmpty()) {
                    searchDebounce(1)
                    refreshSearchHistoryTracks()
                } else {
                    searchViewModel.defaultStateValue()
                    tracks.clear()
                    recyclerView.adapter  = tracksAdapter
                    searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS)
                    tracksAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        clearHistory.setOnClickListener {
            searchHistoryTracks.clear()
            searchViewModel.clearTrackHistorySearch()
            searchHistoryTracksAdapter.notifyDataSetChanged()
            searchHistoryTracksTextVisible()
        }

        refreshBt.setOnClickListener {
            showProgressBar()
            searchDebounce(SEARCH_DEBOUNCE_DELAY_MILLIS)
        }

    }



    private fun refreshSearchHistoryTracks() {
        if (shouldShowHistory) {
            searchHistoryTracksTextVisible()
            searchHistoryTracksAdapter = TrackAdapter(searchHistoryTracks)
            binding.trackList.adapter = searchHistoryTracksAdapter
            searchHistoryTracksAdapter.onItemClickListener = { track ->
                startAudioPlayerActivity(track)
            }
        }
    }
    private fun showErrorMessage(status: Int) {
        with(binding) {
            when (status) {
                1 -> {
                    clearHistory.isVisible = false
                    youSearch.isVisible = false
                    trackList.isVisible = false
                    errorMessage.isVisible = true
                    nothingFound.isVisible = false
                    somethingWentWrong.isVisible = true
                    refreshButton.isVisible = true
                    progressBar.isVisible = false
                    errorMessage.text = getString(R.string.something_went_wrong)
                }
                2 -> {
                    clearHistory.isVisible = false
                    youSearch.isVisible = false
                    trackList.isVisible = false
                    errorMessage.isVisible = true
                    nothingFound.isVisible = true
                    somethingWentWrong.isVisible = false
                    refreshButton.isVisible = false
                    progressBar.isVisible = false
                    errorMessage.text = getString(R.string.nothing_found)
                }
            }
        }
    }
    private fun showProgressBar() {
        with(binding) {
            youSearch.isVisible = false
            clearHistory.isVisible = false
            trackList.isVisible = false
            progressBar.isVisible = true
            errorMessage.isVisible = false
            nothingFound.isVisible = false
            somethingWentWrong.isVisible = false
            refreshButton.isVisible = false
        }
    }

    private fun hideProgressBar() {
        with(binding) {
            trackList.isVisible = true
            errorMessage.isVisible = false
            nothingFound.isVisible = false
            somethingWentWrong.isVisible = false
            refreshButton.isVisible = false
            progressBar.isVisible = false
        }
    }
    private fun searchHistoryTracksTextVisible() {
        binding.youSearch.isVisible = searchHistoryTracks.isNotEmpty()
        binding.clearHistory.isVisible = searchHistoryTracks.isNotEmpty()
    }
    private fun startAudioPlayerActivity(track: Track) {
        val intent = Intent(requireActivity(), AudioPlayerActivity::class.java).apply {
            putExtra(CURRENT_TRACK, track)
        }
        startActivity(intent)
    }
    private fun clickDebounce(): Boolean {
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


    private fun resetToDefaultAdapter() {
        shouldShowHistory = false
        if (searchText.isEmpty()) {
            binding.trackList.adapter = tracksAdapter
            binding.youSearch.isVisible = false
            binding.clearHistory.isVisible = false
            progressBar.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        binding.inputEditText.clearFocus()
        resetToDefaultAdapter()
    }

}