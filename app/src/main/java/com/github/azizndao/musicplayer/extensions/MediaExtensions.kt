package com.github.azizndao.musicplayer.extensions

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

fun MediaSessionCompat.position(): Long {
  return controller.playbackState.position
}

fun MediaSessionCompat.isPlaying(): Boolean {
  return controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING
}

inline val MediaSessionCompat.repeatMode
  get() = controller.repeatMode

inline val MediaSessionCompat.shuffleMode
  get() = controller.shuffleMode

inline val PlaybackStateCompat.isPrepared
  get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
      (state == PlaybackStateCompat.STATE_PLAYING) ||
      (state == PlaybackStateCompat.STATE_PAUSED)

inline val PlaybackStateCompat.isPlaying
  get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
      (state == PlaybackStateCompat.STATE_PLAYING)

inline val PlaybackStateCompat.isPlayEnabled
  get() = (actions and PlaybackStateCompat.ACTION_PLAY != 0L) ||
      ((actions and PlaybackStateCompat.ACTION_PLAY_PAUSE != 0L) &&
          (state == PlaybackStateCompat.STATE_PAUSED))

inline val MediaMetadataCompat.id: String get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.title: String get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline val MediaMetadataCompat.artist: String get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline val MediaMetadataCompat.duration get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

inline val MediaMetadataCompat.album: String get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)