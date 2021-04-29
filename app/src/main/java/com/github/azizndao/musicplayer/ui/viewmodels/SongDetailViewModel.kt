package com.github.azizndao.musicplayer.ui.viewmodels

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.azizndao.musicplayer.models.MediaItemData
import com.github.azizndao.musicplayer.models.PlaybackState
import com.github.azizndao.musicplayer.models.Queue
import com.github.azizndao.musicplayer.playback.PlaybackConnection
import com.github.azizndao.musicplayer.ui.viewmodels.base.CoroutineViewModel
import com.github.azizndao.musicplayer.utils.BeatConstants.BIND_STATE_BOUND
import com.github.azizndao.musicplayer.utils.BeatConstants.BIND_STATE_CANCELED
import com.github.azizndao.musicplayer.utils.BeatConstants.SET_MEDIA_STATE
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongDetailViewModel(
  mediaPlaybackConnection: PlaybackConnection,
  private val context: Context
) : CoroutineViewModel(Main) {

  private var state = BIND_STATE_CANCELED

  private val currentDataBase = MutableLiveData<MediaItemData>()
  val currentData: LiveData<MediaItemData> = currentDataBase

  private val currentStateBase = MutableLiveData<PlaybackState>()
  val currentState: LiveData<PlaybackState> = currentStateBase

  private val queueDataBase = MutableLiveData<Queue>()
  val queueData: LiveData<Queue> = queueDataBase

  private val timeLiveData = MutableLiveData<Int>()
  val time: LiveData<Int> = timeLiveData

  private val rawData = MutableLiveData<ByteArray>().apply { value = byteArrayOf() }
  val raw: LiveData<ByteArray> = rawData

  private val isNavBarShownData = MutableLiveData<Boolean>()

  private val isSongFavLiveData = MutableLiveData<Boolean>()
  private val lyrics: MutableLiveData<String> = MutableLiveData()

  private val playbackStateObserver = Observer<PlaybackStateCompat> { playbackState ->
    playbackState?.let {
      currentStateBase.postValue(PlaybackState.pullPlaybackState(it))
    }
  }

  private val nowMediaMetadataObserver = Observer<MediaMetadataCompat> { mediaMetaData ->
    mediaMetaData?.let {
      currentDataBase.postValue(MediaItemData.pullMediaMetadata(it) ?: return@let)
    }
  }

  private val queueDataObserver = Observer<Queue> { queueData ->
    queueData?.let {
      queueDataBase.postValue(queueData)
    }
  }

  private val mediaMediaConnection = mediaPlaybackConnection.also {
    it.playbackState.observeForever(playbackStateObserver)
    it.nowPlaying.observeForever(nowMediaMetadataObserver)
    it.queueLiveData.observeForever(queueDataObserver)

    it.isConnected.observeForever { connected ->
      if (connected) {
        it.transportControls?.sendCustomAction(SET_MEDIA_STATE, null)
      }
    }
  }

  fun update(newTime: Int) {
    timeLiveData.postValue(newTime)
  }

  fun update(raw: ByteArray) {
    if (rawData.value == null) {
      rawData.postValue(raw)
    } else if (!rawData.value!!.contentEquals(raw)) rawData.postValue(raw)
  }

  fun update(bindState: String = BIND_STATE_CANCELED) {
    state = bindState
    if (state == BIND_STATE_BOUND) bindTime()
  }

  private fun bindTime() {
    GlobalScope.launch {
      while (true) {
        delay(100)
        mediaMediaConnection.mediaController ?: continue
        val newTime = mediaMediaConnection.mediaController?.playbackState!!.position
        if (state == BIND_STATE_BOUND) update(newTime.toInt())
        if (state == BIND_STATE_CANCELED) break
      }
    }
  }


  fun getLyrics(): LiveData<String> {
    return lyrics
  }

  fun updateLyrics(lyric: String? = null) {
    lyrics.postValue(lyric)
  }

  override fun onCleared() {
    super.onCleared()
    mediaMediaConnection.playbackState.removeObserver(playbackStateObserver)
    mediaMediaConnection.nowPlaying.removeObserver(nowMediaMetadataObserver)
    mediaMediaConnection.queueLiveData.removeObserver(queueDataObserver)
  }
}