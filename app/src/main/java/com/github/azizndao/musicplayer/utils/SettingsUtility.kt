package com.github.azizndao.musicplayer.utils

import android.content.Context
import com.github.azizndao.musicplayer.utils.BeatConstants.AUTO_THEME
import com.github.azizndao.musicplayer.utils.BeatConstants.NORMAL_TRANSFORMER
import com.github.azizndao.musicplayer.utils.SortModes.AlbumModes.Companion.ALBUM_A_Z
import com.github.azizndao.musicplayer.utils.SortModes.ArtistModes.Companion.ARTIST_A_Z
import com.github.azizndao.musicplayer.utils.SortModes.SongModes.Companion.SONG_A_Z
import com.github.azizndao.musicplayer.utils.SortModes.SongModes.Companion.SONG_TRACK

class SettingsUtility(context: Context) {

  private val sPreferences = context.getSharedPreferences(
    SHARED_PREFERENCES_FILE_NAME,
    Context.MODE_PRIVATE
  )

  var startPageIndexSelected: Int
    get() = sPreferences.getInt(LAST_OPTION_SELECTED_KEY, 2)
    set(value) {
      setPreference(LAST_OPTION_SELECTED_KEY, value)
    }

  var songSortOrder: String
    get() = sPreferences.getString(SONG_SORT_ORDER_KEY, SONG_A_Z) ?: SONG_A_Z
    set(value) {
      setPreference(SONG_SORT_ORDER_KEY, value)
    }


  var albumSortOrder: String
    get() = sPreferences.getString(ALBUM_SORT_ORDER_KEY, ALBUM_A_Z) ?: ALBUM_A_Z
    set(value) {
      setPreference(ALBUM_SORT_ORDER_KEY, value)
    }

  var currentTheme: String
    get() = sPreferences.getString(CURRENT_THEME_KEY, AUTO_THEME) ?: AUTO_THEME
    set(value) {
      setPreference(CURRENT_THEME_KEY, value)
    }

  var albumSongSortOrder: String
    get() = sPreferences.getString(ALBUM_SONG_SORT_ORDER_KEY, SONG_TRACK) ?: SONG_TRACK
    set(value) {
      setPreference(ALBUM_SONG_SORT_ORDER_KEY, value)
    }

  var currentQueueInfo: String
    get() = sPreferences.getString(QUEUE_INFO_KEY, null) ?: "{}"
    set(value) {
      setPreference(QUEUE_INFO_KEY, value)
    }

  var currentQueueList: String
    get() = sPreferences.getString(QUEUE_LIST_KEY, null) ?: "[]"
    set(value) {
      setPreference(QUEUE_LIST_KEY, value)
    }

  var artistSortOrder: String
    get() = sPreferences.getString(ARTIST_SORT_ORDER_KEY, ARTIST_A_Z) ?: ARTIST_A_Z
    set(value) {
      setPreference(ARTIST_SORT_ORDER_KEY, value)
    }

  var didStop: Boolean
    get() = sPreferences.getBoolean(DID_STOP_KEY, false)
    set(value) {
      setPreference(DID_STOP_KEY, value)
    }

  var intentPath: String
    get() = sPreferences.getString(INTENT_PATH_KEY, null) ?: ""
    set(value) {
      setPreference(INTENT_PATH_KEY, value)
    }

  var originalQueueList: String
    get() = sPreferences.getString(ORIGINAL_QUEUE_LIST, null) ?: "[]"
    set(value) {
      setPreference(ORIGINAL_QUEUE_LIST, value)
    }

  var currentItemTransformer: String
    get() = sPreferences.getString(CURRENT_TRANSFORMER_KEY, null) ?: NORMAL_TRANSFORMER
    set(value) {
      setPreference(CURRENT_TRANSFORMER_KEY, value)
    }

  var isExtraAction: Boolean
    get() = sPreferences.getBoolean(EXTRA_ACTION_KEY, false)
    set(value) {
      setPreference(EXTRA_ACTION_KEY, value)
    }

  var isExtraInfo: Boolean
    get() = sPreferences.getBoolean(EXTRA_INFO_KEY, false)
    set(value) {
      setPreference(EXTRA_INFO_KEY, value)
    }

  var forwardRewindTime: Int
    get() = sPreferences.getInt(FORWARD_REWIND_TIME_KEY, 10 * 1000)
    set(value) {
      setPreference(FORWARD_REWIND_TIME_KEY, value)
    }

  private fun setPreference(key: String, value: String) {
    val editor = sPreferences.edit()
    editor.putString(key, value)
    editor.apply()
  }

  private fun setPreference(key: String, value: Int) {
    val editor = sPreferences.edit()
    editor.putInt(key, value)
    editor.apply()
  }

  private fun setPreference(key: String, state: Boolean) {
    val editor = sPreferences.edit()
    editor.putBoolean(key, state)
    editor.apply()
  }

  companion object {
    private const val SHARED_PREFERENCES_FILE_NAME = "configs"
    private const val SONG_SORT_ORDER_KEY = "song_sort_order"
    private const val ALBUM_SORT_ORDER_KEY = "album_sort_order"
    private const val ALBUM_SONG_SORT_ORDER_KEY = "album_song_sort_order"
    private const val ARTIST_SORT_ORDER_KEY = "artist_sort_order"
    private const val LAST_OPTION_SELECTED_KEY = "last_option_selected"
    private const val CURRENT_THEME_KEY = "current_theme"
    private const val DID_STOP_KEY = "did_stop_key"
    private const val INTENT_PATH_KEY = "intent_path_key"
    private const val ORIGINAL_QUEUE_LIST = "original_queue_list"
    private const val CURRENT_TRANSFORMER_KEY = "current_transformer_key"
    private const val EXTRA_INFO_KEY = "extra_info_key"
    private const val EXTRA_ACTION_KEY = "extra_action_key"
    private const val FORWARD_REWIND_TIME_KEY = "forward_rewind_time_key"

    const val QUEUE_INFO_KEY = "queue_info_key"
    const val QUEUE_LIST_KEY = "queue_list_key"
  }

}