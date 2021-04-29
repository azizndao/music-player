package com.github.azizndao.musicplayer.extensions

import android.support.v4.media.MediaDescriptionCompat
import com.github.azizndao.musicplayer.models.Song
import com.github.azizndao.musicplayer.utils.GeneralUtils
import com.github.azizndao.musicplayer.utils.GeneralUtils.getAlbumArtUri

fun Int.format(): String {
  return GeneralUtils.formatMilliseconds(this.toLong())
}

fun Int.fix(): Int {
  var value = this
  while (value >= 1000) {
    value -= 1000
  }
  return value
}

fun Float.fixPercentBounds(): Float {
  return if (this < 0F) 0F else if (this > 100F) 100F else this
}

fun Int.fixToStep(step: Int): Int {
  return this / step * step
}

fun Int.fixToPercent(total: Int): Float {
  return (this * 100) / total.toFloat()
}

fun Float.percentToMs(total: Int): Int {
  return (this * total / 100).toInt()
}

fun Song.toDescription(): MediaDescriptionCompat {
  return MediaDescriptionCompat.Builder()
    .setTitle(title)
    .setMediaId(id.toString())
    .setSubtitle(artist)
    .setDescription(album)
    .setIconUri(getAlbumArtUri(albumId)).build()
}