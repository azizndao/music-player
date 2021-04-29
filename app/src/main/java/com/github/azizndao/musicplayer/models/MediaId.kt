package com.github.azizndao.musicplayer.models

import android.support.v4.media.MediaBrowserCompat

class MediaId(
  val type: String? = "0",
  val mediaId: String? = "0",
  val caller: String? = ""
) {

  companion object {
    const val CALLER_SELF = "self"
    const val CALLER_OTHER = "other"
  }

  var mediaItem: MediaBrowserCompat.MediaItem? = null

  override fun toString(): String {
    return "$type | $mediaId | $caller"
  }
}