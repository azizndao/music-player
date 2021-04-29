package com.github.azizndao.musicplayer.playback.players

import android.app.Application
import android.net.Uri
import com.github.azizndao.musicplayer.alias.OnCompletion
import com.github.azizndao.musicplayer.alias.OnError
import com.github.azizndao.musicplayer.alias.OnPrepared
import com.github.azizndao.musicplayer.interfaces.LoadEventController
import com.github.azizndao.musicplayer.utils.LoadController
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.util.PriorityTaskManager
import java.io.File

interface AudioPlayer {
  fun play(startAtPosition: Long? = null)
  fun setSource(uri: Uri? = null, path: String? = null): Boolean
  fun prepare()
  fun seekTo(position: Long)
  fun duration(): Long
  fun isPrepared(): Boolean
  fun isPlaying(): Boolean
  fun position(): Long
  fun pause()
  fun stop()
  fun release()
  fun onPrepared(prepared: OnPrepared<AudioPlayer>)
  fun onError(error: OnError<AudioPlayer>)
  fun onCompletion(completion: OnCompletion<AudioPlayer>)
}

class AudioPlayerImplementation(
  internal val context: Application
) : AudioPlayer,
  Player.EventListener,
  LoadEventController {

  private var playerBase: ExoPlayer? = null
  private val player: ExoPlayer
    get() {
      if (playerBase == null) {
        playerBase = createPlayer(this)
      }
      return playerBase ?: throw IllegalStateException("Could not create an audio player")
    }

  private var isPrepared = false
  private var onPrepared: OnPrepared<AudioPlayer> = {}
  private var onError: OnError<AudioPlayer> = {}
  private var onCompletion: OnCompletion<AudioPlayer> = {}

  override fun play(startAtPosition: Long?) {
    startAtPosition ?: return player.play()
    player.seekTo(startAtPosition)
    player.play()
  }

  override fun setSource(uri: Uri?, path: String?): Boolean {
    return try {
      uri?.let {
        player.setMediaItem(MediaItem.fromUri(it), true)
      }
      path?.let {
        player.setMediaItem(MediaItem.fromUri(Uri.fromFile(File(it))), true)
      }
      true
    } catch (ex: Exception) {
      onError(this, ex)
      false
    }
  }

  override fun prepare() {
    player.prepare()
  }

  override fun seekTo(position: Long) {
    player.seekTo(position)
  }

  override fun duration() = player.duration

  override fun isPrepared() = isPrepared

  override fun isPlaying() = player.isPlaying

  override fun position() = player.currentPosition

  override fun pause() {
    player.pause()
  }

  override fun stop() {
    player.stop()
  }

  override fun release() {
    player.release()
  }

  override fun onPrepared(prepared: OnPrepared<AudioPlayer>) {
    this.onPrepared = prepared
  }

  override fun onError(error: OnError<AudioPlayer>) {
    this.onError = error
  }

  override fun onCompletion(completion: OnCompletion<AudioPlayer>) {
    this.onCompletion = completion
  }

  override fun onPlaybackStateChanged(state: Int) {
    super.onPlaybackStateChanged(state)
    if (state == Player.STATE_ENDED) {
      onCompletion(this)
    }
  }

  override fun onPrepared() {
    isPrepared = true
    onPrepared(this)
  }

  override fun onPlayerError(error: ExoPlaybackException) {
    isPrepared = false
    onError(this, error)
  }

  private fun createPlayer(owner: AudioPlayerImplementation): ExoPlayer {
    return SimpleExoPlayer.Builder(context)
      .setLoadControl(LoadController().apply {
        eventController = owner
      })
      .build().apply {
        val attr = AudioAttributes.Builder().apply {
          setContentType(C.CONTENT_TYPE_MUSIC)
          setUsage(C.USAGE_MEDIA)
        }.build()

        setAudioAttributes(attr, false)
        setPriorityTaskManager(PriorityTaskManager())
        addListener(owner)
      }
  }
}