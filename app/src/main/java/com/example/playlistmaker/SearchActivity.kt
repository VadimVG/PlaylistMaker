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

    private lateinit var errorText: TextView
    private lateinit var errorNotFound: ImageView
    private lateinit var errorWentWrong: ImageView
    private lateinit var refreshBt: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val tvBack: TextView = findViewById<TextView>(R.id.settingsBack)
        tvBack.setOnClickListener{
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        errorText = findViewById(R.id.errorMessage)
        errorNotFound = findViewById(R.id.nothing_found)
        errorWentWrong = findViewById(R.id.something_went_wrong)
        refreshBt = findViewById(R.id.refreshButton)
        val recyclerView = findViewById<RecyclerView>(R.id.trackList)
        recyclerView.adapter = tracksAdapter


        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
        }

        refreshBt.setOnClickListener {
            search(clientRequest)

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

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

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

    // настройка видимости кнопки для удаления текста из строки поиска
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_KEY, searchText)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        searchText = savedInstanceState.getString(SEARCH_KEY).toString()
        super.onRestoreInstanceState(savedInstanceState)
    }


    companion object {
        private const val SEARCH_KEY = "KEY_STRING"
        private const val SEARCH_TEXT = ""
    }

}