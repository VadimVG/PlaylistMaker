package com.example.playlistmaker

object ITunesApiResponseStatuses {
    const val NETWORK_ERROR = -1
    const val SUCCESS_REQUEST = 200
    const val BAD_REQUEST = 400
}

object ThemeSwitcher {
    const val APP_THEME_PREFERENCES = "app_theme_preferences"
    const val DARK_THEME = "dark_theme"
}

object SearchHistoryList {
    const val PREFERENCES_KEY = "search_history_preferences"
    const val ARRAY_LIST_KEY = "search_history_edit_text"
}

object AudioPlayerCurrentTrack {
    var CURRENT_TRACK  = "CURRENT_TRACK"
}

object InputSearchText {
    const val SEARCH_KEY = "KEY_STRING" // ключ, по которому сохраняется и восстанавливается значение InstanceState
    const val SEARCH_TEXT = "" // значение по умолчанию
}

object DebounceSearchTime {
    const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
}


