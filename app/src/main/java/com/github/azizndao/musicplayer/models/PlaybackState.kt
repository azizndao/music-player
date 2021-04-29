package com.github.azizndao.musicplayer.models

import android.support.v4.media.session.PlaybackStateCompat
import com.github.azizndao.musicplayer.utils.BeatConstants

class PlaybackState(
  val position: Int = 0,
  val shuffleMode: Int = 0,
  val repeatMode: Int = 0,
  val state: Int = 0
) {

  companion object {
    fun pullPlaybackState(playbackState: PlaybackStateCompat): PlaybackState {
      with(playbackState.extras) {
        return PlaybackState(
          position = playbackState.position.toInt(),
          state = playbackState.state,
          repeatMode = this?.getInt(BeatConstants.REPEAT_MODE)
            ?: PlaybackStateCompat.REPEAT_MODE_ONE,
          shuffleMode = this?.getInt(BeatConstants.SHUFFLE_MODE)
            ?: PlaybackStateCompat.REPEAT_MODE_ALL
        )
      }
    }
  }
}