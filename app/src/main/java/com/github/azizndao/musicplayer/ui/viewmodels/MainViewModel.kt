package com.github.azizndao.musicplayer.ui.viewmodels

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.navigate
import com.github.azizndao.musicplayer.R
import com.github.azizndao.musicplayer.extensions.filter
import com.github.azizndao.musicplayer.models.Song
import com.github.azizndao.musicplayer.playback.PlaybackConnection
import com.github.azizndao.musicplayer.ui.viewmodels.base.CoroutineViewModel
import com.github.azizndao.musicplayer.utils.BeatConstants.PLAY_SONG_FROM_INTENT
import com.github.azizndao.musicplayer.utils.BeatConstants.QUEUE_LIST_TYPE_KEY
import com.github.azizndao.musicplayer.utils.BeatConstants.SONG_KEY
import com.github.azizndao.musicplayer.utils.BeatConstants.UPDATE_QUEUE
import com.github.azizndao.musicplayer.utils.SettingsUtility.Companion.QUEUE_INFO_KEY
import com.github.azizndao.musicplayer.utils.SettingsUtility.Companion.QUEUE_LIST_KEY
import kotlinx.coroutines.Dispatchers

class MainViewModel(
  private val playbackConnection: PlaybackConnection,
) : CoroutineViewModel(Dispatchers.Main) {

  lateinit var navigator: NavHostController

  fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit = { }) {
    navigator.navigate(route, builder)
  }

  fun popBackStack() {
    navigator.popBackStack()
  }
  fun mediaItemClicked(mediaItem: MediaBrowserCompat.MediaItem, extras: Bundle? = null) {
    transportControls()?.playFromMediaId(mediaItem.mediaId, extras)
  }

  fun mediaItemClickFromIntent(context: Context, song: Song) {
    transportControls() ?: playbackConnection.isConnected.filter { it }.observeForever {
      mediaItemClickFromIntent(context, song)
    }
    transportControls()?.sendCustomAction(
      PLAY_SONG_FROM_INTENT,
      bundleOf(
        SONG_KEY to song.toString(),
        QUEUE_INFO_KEY to context.getString(R.string.others)
      )
    )
  }

  fun transportControls() = playbackConnection.transportControls

  fun reloadQueueIds(ids: LongArray, type: String) {
    transportControls()?.sendCustomAction(
      UPDATE_QUEUE,
      bundleOf(QUEUE_LIST_KEY to ids, QUEUE_LIST_TYPE_KEY to type)
    )
  }

}

