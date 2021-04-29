package com.github.azizndao.musicplayer.playback.players

import android.app.Application
import android.app.PendingIntent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.crrl.beatplayer.extensions.*
import com.github.azizndao.musicplayer.playback.AudioFocusHelper
import com.github.azizndao.musicplayer.repository.SongsRepository
import com.github.azizndao.musicplayer.utils.BeatConstants.BY_UI_KEY
import com.github.azizndao.musicplayer.utils.BeatConstants.REPEAT_ALL
import com.github.azizndao.musicplayer.utils.BeatConstants.REPEAT_MODE
import com.github.azizndao.musicplayer.utils.BeatConstants.REPEAT_ONE
import com.github.azizndao.musicplayer.utils.BeatConstants.SHUFFLE_MODE
import com.github.azizndao.musicplayer.utils.BeatConstants.SONG_ID_DEFAULT
import com.github.azizndao.musicplayer.utils.QueueUtils
import com.github.azizndao.musicplayer.utils.SettingsUtility
import com.github.azizndao.musicplayer.R
import com.github.azizndao.musicplayer.alias.*
import com.github.azizndao.musicplayer.extensions.*
import com.github.azizndao.musicplayer.models.Song
import com.github.azizndao.musicplayer.utils.GeneralUtils.getAlbumArtBitmap
import com.github.azizndao.musicplayer.utils.GeneralUtils.getSongUri
import com.github.azizndao.musicplayer.utils.TagUtils


interface BeatPlayer {
  fun getSession(): MediaSessionCompat
  fun playSong(extras: Bundle = bundleOf(BY_UI_KEY to true))
  fun playSong(id: Long)
  fun playSong(song: Song)
  fun seekTo(position: Long)
  fun fastForward()
  fun rewind()
  fun pause(extras: Bundle = bundleOf(BY_UI_KEY to true))
  fun nextSong(): Long?
  fun repeatSong()
  fun repeatQueue()
  fun previousSong()
  fun playNext(id: Long)
  fun swapQueueSongs(from: Int, to: Int)
  fun removeFromQueue(id: Long)
  fun stop()
  fun release()
  fun onPlayingState(playing: OnIsPlaying)
  fun onPrepared(prepared: OnPrepared<BeatPlayer>)
  fun onError(error: OnError<BeatPlayer>)
  fun onCompletion(completion: OnCompletion<BeatPlayer>)
  fun onMetaDataChanged(metaDataChanged: OnMetaDataChanged)
  fun updatePlaybackState(applier: PlaybackStateCompat.Builder.() -> Unit)
  fun setPlaybackState(state: PlaybackStateCompat)
  fun updateData(list: LongArray = longArrayOf(), title: String = "")
  fun setData(list: LongArray = longArrayOf(), title: String = "")
  fun restoreQueueData()
  fun clearRandomSongPlayed()
  fun setCurrentSongId(songId: Long)
  fun shuffleQueue(isShuffle: Boolean)
}

class BeatPlayerImplementation(
  private val context: Application,
  private val musicPlayer: AudioPlayer,
  private val songsRepository: SongsRepository,
  private val settingsUtility: SettingsUtility,
  private val queueUtils: QueueUtils,
  private val audioFocusHelper: AudioFocusHelper
) : BeatPlayer {

  private var isInitialized: Boolean = false

  private var isPlayingCallback: OnIsPlaying = { _, _ -> }
  private var preparedCallback: OnPrepared<BeatPlayer> = {}
  private var errorCallback: OnError<BeatPlayer> = {}
  private var completionCallback: OnCompletion<BeatPlayer> = {}
  private var metaDataChangedCallback: OnMetaDataChanged = {}

  private val metadataBuilder = MediaMetadataCompat.Builder()
  private val stateBuilder = createDefaultPlaybackState()

  private var mediaSession =
    MediaSessionCompat(context, context.getString(R.string.app_name)).apply {
      setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
      setCallback(
        MediaSessionCallback(
          this,
          this@BeatPlayerImplementation,
          audioFocusHelper,
          songsRepository
        )
      )
      setPlaybackState(stateBuilder.build())

      val sessionIntent =
        context.packageManager.getLaunchIntentForPackage(context.packageName)
      val sessionActivityPendingIntent =
        PendingIntent.getActivity(context, 0, sessionIntent, 0)
      setSessionActivity(sessionActivityPendingIntent)
      isActive = true
    }

  init {
    queueUtils.setMediaSession(mediaSession)
    musicPlayer.onPrepared {
      preparedCallback(this@BeatPlayerImplementation)
      if (!mediaSession.isPlaying()) musicPlayer.seekTo(mediaSession.position())
      playSong()
    }

    musicPlayer.onCompletion {
      completionCallback(this@BeatPlayerImplementation)
      val controller = getSession().controller
      when (controller.repeatMode) {
        REPEAT_MODE_ONE -> {
          controller.transportControls.sendCustomAction(REPEAT_ONE, null)
        }
        REPEAT_MODE_ALL -> {
          controller.transportControls.sendCustomAction(REPEAT_ALL, null)
        }
        else -> if (nextSong() == null) goToStart()
      }
    }
  }

  override fun getSession(): MediaSessionCompat = mediaSession

  override fun playSong(extras: Bundle) {
    if (isInitialized) {
      updatePlaybackState {
        setState(STATE_PLAYING, mediaSession.position(), 1F)
        setExtras(
          extras + bundleOf(
            REPEAT_MODE to getSession().repeatMode,
            SHUFFLE_MODE to getSession().shuffleMode
          )
        )
      }
      musicPlayer.play()
      return
    }

    val path = if (queueUtils.currentSongId != SONG_ID_DEFAULT)
      getSongUri(queueUtils.currentSongId).toString()
    else queueUtils.currentSong.path
    val isSourceSet = if (path.startsWith("content://")) {
      musicPlayer.setSource(path.toUri())
    } else {
      musicPlayer.setSource(path = path)
    }
    if (isSourceSet) {
      isInitialized = true
      musicPlayer.prepare()
    }
  }

  override fun playSong(id: Long) {
    if (audioFocusHelper.requestPlayback()) {
      val song = songsRepository.getSongForId(id)
      playSong(song)
    }
  }

  override fun playSong(song: Song) {
    queueUtils.currentSongId = song.id
    queueUtils.currentSong = song
    isInitialized = false

    updatePlaybackState {
      setState(mediaSession.controller.playbackState.state, 0, 1F)
    }
    setMetaData(song)
    playSong()
  }

  override fun seekTo(position: Long) {
    if (isInitialized) {
      musicPlayer.seekTo(position)
      updatePlaybackState {
        setState(
          mediaSession.controller.playbackState.state,
          position,
          1F
        )
      }
    } else updatePlaybackState {
      setState(
        mediaSession.controller.playbackState.state,
        position,
        1F
      )
    }
  }

  override fun fastForward() {
    val forwardTo = mediaSession.position() + settingsUtility.forwardRewindTime
    val duration = queueUtils.currentSong.duration.toLong()
    if (forwardTo > duration) {
      seekTo(duration)
    } else {
      seekTo(forwardTo)
    }
  }

  override fun rewind() {
    val rewindTo = mediaSession.position() - settingsUtility.forwardRewindTime
    if (rewindTo < 0) {
      seekTo(0)
    } else {
      seekTo(rewindTo)
    }
  }

  override fun pause(extras: Bundle) {
    if (musicPlayer.isPlaying() && isInitialized) {
      musicPlayer.pause()
      updatePlaybackState {
        setState(STATE_PAUSED, mediaSession.position(), 1F)
        setExtras(
          extras + bundleOf(
            REPEAT_MODE to getSession().repeatMode,
            SHUFFLE_MODE to getSession().shuffleMode
          )
        )
      }
    }
  }

  override fun nextSong(): Long? {
    val id = queueUtils.nextSongId
    id?.let { playSong(it) }
    return id
  }

  override fun repeatSong() {
    playSong(queueUtils.currentSongId)
  }

  override fun repeatQueue() {
    if (queueUtils.currentSongId == queueUtils.queue.last())
      playSong(queueUtils.queue.first())
    else {
      nextSong()
    }
  }

  override fun previousSong() {
    queueUtils.previousSongId?.let {
      playSong(it)
    } ?: repeatSong()
  }

  override fun playNext(id: Long) {
    queueUtils.playNext(id)
  }

  override fun swapQueueSongs(from: Int, to: Int) {
    queueUtils.swap(from, to)
    setMetaData(queueUtils.currentSong)
  }

  override fun removeFromQueue(id: Long) {
    queueUtils.remove(id)
  }

  override fun stop() {
    musicPlayer.stop()
    updatePlaybackState {
      setState(STATE_NONE, 0, 1F)
    }
  }

  override fun release() {
    mediaSession.apply {
      isActive = false
      release()
    }
    musicPlayer.release()
    queueUtils.clear()
  }

  override fun onPlayingState(playing: OnIsPlaying) {
    this.isPlayingCallback = playing
  }

  override fun onPrepared(prepared: OnPrepared<BeatPlayer>) {
    this.preparedCallback = prepared
  }

  override fun onError(error: OnError<BeatPlayer>) {
    this.errorCallback = error
    musicPlayer.onError { throwable ->
      errorCallback(this@BeatPlayerImplementation, throwable)
    }
  }

  override fun onCompletion(completion: OnCompletion<BeatPlayer>) {
    this.completionCallback = completion
  }

  override fun onMetaDataChanged(metaDataChanged: OnMetaDataChanged) {
    this.metaDataChangedCallback = metaDataChanged
  }

  override fun updatePlaybackState(applier: PlaybackStateCompat.Builder.() -> Unit) {
    applier(stateBuilder)
    setPlaybackState(stateBuilder.build())
  }

  override fun setPlaybackState(state: PlaybackStateCompat) {
    mediaSession.setPlaybackState(state)
    state.extras?.let { bundle ->
      mediaSession.setRepeatMode(bundle.getInt(REPEAT_MODE))
      mediaSession.setShuffleMode(bundle.getInt(SHUFFLE_MODE))
    }
    if (state.isPlaying) {
      isPlayingCallback(this, true, state.extras?.getBoolean(BY_UI_KEY)!!)
    } else {
      isPlayingCallback(this, false, state.extras?.getBoolean(BY_UI_KEY)!!)
    }
  }

  override fun updateData(list: LongArray, title: String) {
    if (mediaSession.shuffleMode == SHUFFLE_MODE_NONE)
      if (title == queueUtils.queueTitle) {
        queueUtils.queue = list
        queueUtils.queueTitle = title
        setMetaData(queueUtils.currentSong)
      }
  }

  override fun setData(list: LongArray, title: String) {
    queueUtils.queue = list
    queueUtils.queueTitle = title
  }

  override fun restoreQueueData() {
    val queueData = settingsUtility.currentQueueInfo.toQueueInfo()
    val queueIds = settingsUtility.currentQueueList.toQueueList()

    queueUtils.currentSongId = queueData.id

    setData(queueIds, queueData.name)
    setMetaData(queueUtils.currentSong)

    val extras = bundleOf(
      REPEAT_MODE to queueData.repeatMode,
      SHUFFLE_MODE to queueData.shuffleMode
    )

    updatePlaybackState {
      setState(queueData.state, queueData.seekPos, 1F)
      setExtras(extras)
    }
  }

  override fun clearRandomSongPlayed() {
    queueUtils.clearPlayedSongs()
  }

  override fun setCurrentSongId(songId: Long) {
    queueUtils.currentSongId = songId
  }

  override fun shuffleQueue(isShuffle: Boolean) {
    queueUtils.shuffleQueue(isShuffle)
    setMetaData(queueUtils.currentSong)
  }

  private fun goToStart() {
    isInitialized = false

    stop()

    if (queueUtils.queue.isEmpty()) return

    queueUtils.currentSongId = queueUtils.queue.first()

    val song = songsRepository.getSongForId(queueUtils.currentSongId)
    Handler(Looper.getMainLooper()).postDelayed({
      setMetaData(song)
    }, 250)
  }

  private fun setMetaData(song: Song) {
    if (song.id in listOf<Long>(0, -1)) return

    val extraInfo = TagUtils.readExtraTags(song.path, queueUtils.queue())
    val artwork = getAlbumArtBitmap(context, song.albumId)
    val mediaMetadata = metadataBuilder.apply {
      putString(METADATA_KEY_ALBUM, song.album)
      putString(METADATA_KEY_ARTIST, song.artist)
      putString(METADATA_KEY_TITLE, song.title)
      putString(METADATA_KEY_ALBUM_ART_URI, song.albumId.toString())
      putString(METADATA_KEY_MEDIA_ID, song.id.toString())
      putString(METADATA_KEY_DISPLAY_DESCRIPTION, extraInfo.toString())
      putLong(METADATA_KEY_DURATION, song.duration.toLong())
      putBitmap(METADATA_KEY_ALBUM_ART, artwork)
    }.build()
    mediaSession.setMetadata(mediaMetadata)
    metaDataChangedCallback(this)
  }
}

private fun createDefaultPlaybackState(): PlaybackStateCompat.Builder {
  return PlaybackStateCompat.Builder().setActions(
    ACTION_PLAY
        or ACTION_PAUSE
        or ACTION_PLAY_FROM_SEARCH
        or ACTION_PLAY_FROM_MEDIA_ID
        or ACTION_PLAY_PAUSE
        or ACTION_SKIP_TO_NEXT
        or ACTION_SKIP_TO_PREVIOUS
        or ACTION_SET_SHUFFLE_MODE
        or ACTION_SET_REPEAT_MODE
        or ACTION_SEEK_TO
  )
}