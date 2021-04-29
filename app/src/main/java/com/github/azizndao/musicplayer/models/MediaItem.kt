
package com.github.azizndao.musicplayer.models

open class MediaItem(var _id: Long = 0) {

  open fun compare(other: MediaItem): Boolean {
    return this._id == other._id
  }

}