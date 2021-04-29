package com.github.azizndao.musicplayer.playback

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import com.github.azizndao.musicplayer.models.Queue

val NONE_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
  .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
  .build()

val NONE_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
  .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
  .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
  .build()


interface PlaybackConnection {
  val isConnected: MutableLiveData<Boolean>
  val playbackState: MutableLiveData<PlaybackStateCompat>
  val nowPlaying: MutableLiveData<MediaMetadataCompat>
  val transportControls: MediaControllerCompat.TransportControls?
  val queueLiveData: MutableLiveData<Queue>
  var mediaController: MediaControllerCompat?
}

class PlaybackConnectionImplementation(
  context: Context,
  serviceComponent: ComponentName
) : PlaybackConnection {

  override val isConnected = MutableLiveData<Boolean>()
  override val playbackState = MutableLiveData<PlaybackStateCompat>()
  override val nowPlaying = MutableLiveData<MediaMetadataCompat>()
  override val queueLiveData = MutableLiveData<Queue>()
  override var mediaController: MediaControllerCompat? = null

  override val transportControls: MediaControllerCompat.TransportControls?
    get() = mediaController?.transportControls

  private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
  private val mediaBrowser = MediaBrowserCompat(
    context,
    serviceComponent,
    mediaBrowserConnectionCallback, null
  ).apply { connect() }

  private inner class MediaBrowserConnectionCallback(private val context: Context) :
    MediaBrowserCompat.ConnectionCallback() {
    override fun onConnected() {
      mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
        registerCallback(MediaControllerCallback())
      }

      isConnected.postValue(true)
    }

    override fun onConnectionSuspended() {
      isConnected.postValue(false)
    }

    override fun onConnectionFailed() {
      isConnected.postValue(false)
    }
  }

  private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
      playbackState.postValue(state ?: NONE_PLAYBACK_STATE)
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
      metadata ?: return
      nowPlaying.postValue(metadata)
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
      mediaController ?: return
      queueLiveData.postValue(Queue.fromMediaController(mediaController!!))
    }

    override fun onSessionDestroyed() {
      mediaBrowserConnectionCallback.onConnectionSuspended()
    }
  }
}