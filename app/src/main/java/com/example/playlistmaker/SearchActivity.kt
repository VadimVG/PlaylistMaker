package com.example.playlistmaker


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.AudioPlayerCurrentTrack
import com.example.playlistmaker.search.ITunesApi
import com.example.playlistmaker.search.ITunesResponse
import com.example.playlistmaker.search.SearchHistory
import com.example.playlistmaker.search.SearchHistorySharedPrefsConst
import com.example.playlistmaker.search.Track
import com.example.playlistmaker.search.TrackAdapter
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response


class SearchActivity: AppCompatActivity() {


    private lateinit var tvBack: TextView

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private var searchText : String = SEARCH_TEXT

    private var clientRequest:String =""
    private lateinit var tracks: ArrayList<Track>
    private lateinit var tracksAdapter: TrackAdapter
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
                                    .baseUrl(iTunesBaseUrl)// передача базовго url
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)
    private lateinit var recyclerView: RecyclerView

    private lateinit var errorText: TextView
    private lateinit var errorNotFound: ImageView
    private lateinit var errorWentWrong: ImageView
    private lateinit var refreshBt: Button
    private lateinit var clearHistory: Button
    private lateinit var youSearch: TextView
    private lateinit var searchHistorySharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryTracks: ArrayList<Track>
    private lateinit var searchHistoryTracksAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets -> // пока непонятно, что это, уточнить
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tvBack = findViewById<TextView>(R.id.settingsBack)
        inputEditText = findViewById<EditText>(R.id.inputEditText)
        clearButton = findViewById<ImageView>(R.id.clearIcon)
        youSearch = findViewById<TextView>(R.id.youSearch)
        tracks = ArrayList<Track>()
        tracksAdapter = TrackAdapter(tracks)
        errorText = findViewById(R.id.errorMessage)
        errorNotFound = findViewById(R.id.nothing_found)
        errorWentWrong = findViewById(R.id.something_went_wrong)
        refreshBt = findViewById(R.id.refreshButton)
        recyclerView = findViewById<RecyclerView>(R.id.trackList)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchHistorySharedPrefs = getSharedPreferences(SearchHistorySharedPrefsConst.PREFERENCES_KEY, MODE_PRIVATE)// получаем экземпляр класса SharedPreferences
        searchHistory = SearchHistory(searchHistorySharedPrefs)
        tracksAdapter.onItemClickListener = { track -> // сохраняем трек, на который кликнул пользователь, в файл sharedPreferences
            searchHistory.add(track)
            startAudioPlayerActivity(track)
        }


        clearHistory = findViewById(R.id.clearHistory)


        tvBack.setOnClickListener{ finish() } // возвращение на главный экран


        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // скрываем клавиатуру
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0) // скрываем клавиатуру
            inputEditText.clearFocus() // удаление фокуса с EditText
            tracks.clear() // очистка списка треков
            tracksAdapter.notifyDataSetChanged() // указываем адаптеру, что полученные ранее данные (список треков) изменились и следует перерисовать список на экране
        }


//        inputEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                if (inputEditText.text.isNotEmpty()) {
//                    clientRequest = inputEditText.text.toString()
//                    Log.d("SearchActivity", "INPUT USER VALUE: $clientRequest")
//                    search()
//                }
//                true
//            }
//            false
//        }

        inputEditText.setOnFocusChangeListener { view, hasFocus -> // отображение истории поиска
            if (hasFocus && inputEditText.text.isEmpty()) {
                searchHistoryTracks = searchHistory.get()
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
                if (inputEditText.hasFocus() && p0?.isEmpty() == true) {
                    youSearch.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
                    clearHistory.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
                    recyclerView.adapter = searchHistoryTracksAdapter
                }
                else {
                    recyclerView.adapter  = tracksAdapter
                    if (p0?.isEmpty() == false) {
                        clientRequest = inputEditText.text.toString()
                        searchDebounce()
                    }
                }

                errorText.visibility = View.GONE
                errorNotFound.visibility = View.GONE
                errorWentWrong.visibility = View.GONE
                refreshBt.visibility = View.GONE
                youSearch.visibility = View.GONE
                clearHistory.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        clearHistory.setOnClickListener{
            searchHistoryTracks.clear()
            searchHistory.clear()
            searchHistoryTracksAdapter.notifyDataSetChanged()
            youSearch.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
            clearHistory.visibility = if (searchHistoryTracks.size > 0) View.VISIBLE else View.GONE
        }
        refreshBt.setOnClickListener { search() } // отправка повторного запроса, если что-то пошло не так


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

    }


    private fun search() {
        val request: String = clientRequest
        Log.d("SearchActivity", "INPUT USER VALUE TO SEARCH FUNC: $clientRequest")
        iTunesApi.findSong(request).enqueue(object : Callback<ITunesResponse> {
            override fun onResponse(call: Call<ITunesResponse>, response: Response<ITunesResponse>) {
                if (response.isSuccessful) {
                    Log.d("SearchActivity", "RESPONSE: $response")
                    Log.d("SearchActivity", "RESPONSE BODY: ${response.body()?.results!!}")
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        tracksAdapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) showErrorMessage(getString(R.string.nothing_found), 1)
                    else showErrorMessage("", 2)
                }
                else showErrorMessage(getString(R.string.something_went_wrong), 2)
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                showErrorMessage(getString(R.string.something_went_wrong), 2)
            }
        })
    }


    private fun showErrorMessage(text: String, type: Int) {
        if (text.isNotEmpty()) {
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
        audioplayerIntent.putExtra(AudioPlayerCurrentTrack.CURRENT_TRACK, track)
        startActivity(audioplayerIntent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    companion object { // для создания константной переменной мы используем companion object
        private const val SEARCH_KEY = "KEY_STRING" // ключ, по которому сохраняется и восстанавливается значение InstanceState
        private const val SEARCH_TEXT = "" // значение по умолчанию

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }


}