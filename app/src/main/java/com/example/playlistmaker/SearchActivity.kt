package com.example.playlistmaker


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import com.example.playlistmaker.search.ITunesApi
import com.example.playlistmaker.search.ITunesResponse
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
    private val tracks = ArrayList<Track>()
    private val tracksAdapter = TrackAdapter(tracks)
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
                                    .baseUrl(iTunesBaseUrl)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)
    private lateinit var recyclerView: RecyclerView

    private lateinit var errorText: TextView
    private lateinit var errorNotFound: ImageView
    private lateinit var errorWentWrong: ImageView
    private lateinit var refreshBt: Button


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
        errorText = findViewById(R.id.errorMessage)
        errorNotFound = findViewById(R.id.nothing_found)
        errorWentWrong = findViewById(R.id.something_went_wrong)
        refreshBt = findViewById(R.id.refreshButton)
        recyclerView = findViewById<RecyclerView>(R.id.trackList)
        recyclerView.adapter = tracksAdapter


        tvBack.setOnClickListener{ finish() } // возвращение на главный экран


        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // скрываем клавиатуру
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0) // скрываем клавиатуру
            inputEditText.clearFocus() // удаление фокуса с EditText
            tracks.clear() // очистка списка треков
            tracksAdapter.notifyDataSetChanged() // указываем адаптеру, что полученные ранее данные (список треков) изменились и следует перерисовать список на экране
        }


        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    clientRequest = inputEditText.text.toString()
                    Log.d("SearchActivity", "INPUT USER VALUE: $clientRequest")
                    search(clientRequest)
                }
                true
            }
            false
        }


        refreshBt.setOnClickListener { search(clientRequest) } // отправка повторного запроса, если что-то пошло не так


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


    private fun search(request: String) {
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
                    if (tracks.isEmpty()) {
                        showErrorMessage(getString(R.string.nothing_found), 1)
                    }
                    else {
                        showErrorMessage("", 2)
                    }
                }
                else {
                    showErrorMessage(getString(R.string.something_went_wrong), 2)
                }
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


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE // настройка видимости кнопки для удаления текста из строки поиска
    }


    override fun onSaveInstanceState(outState: Bundle) { // сохранение введенного текста из строки поиска (состояния) перед уничтожением активити
        outState.putString(SEARCH_KEY, searchText)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // восстановление введенного текста из строки поиска после создания активити
        searchText = savedInstanceState.getString(SEARCH_KEY).toString()
        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object { // для создания константной переменной мы используем companion object
        private const val SEARCH_KEY = "KEY_STRING" // ключ, по которому сохраняется и восстанавливается значение InstanceState
        private const val SEARCH_TEXT = "" // значение по умолчанию
    }

}