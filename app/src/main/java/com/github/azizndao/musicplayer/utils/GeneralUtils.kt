package com.github.azizndao.musicplayer.utils

import android.content.ComponentCallbacks
import android.content.ContentUris.withAppendedId
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.github.azizndao.musicplayer.utils.BeatConstants.ARTWORK_URI
import com.github.azizndao.musicplayer.utils.BeatConstants.SEEK_TO_POS
import com.github.azizndao.musicplayer.utils.BeatConstants.SONG_LIST_NAME
import com.github.azizndao.musicplayer.utils.BeatConstants.SONG_URI
import com.github.azizndao.musicplayer.utils.SettingsUtility.Companion.QUEUE_INFO_KEY
import com.github.azizndao.musicplayer.R
import com.github.azizndao.musicplayer.models.Song
import org.koin.android.ext.android.inject
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import timber.log.Timber
import java.io.FileNotFoundException


@Composable
inline fun <reified T : Any> inject(
  qualifier: Qualifier? = null,
  noinline parameters: ParametersDefinition? = null
): Lazy<T> {
  val componentCallbacks = LocalContext.current as ComponentCallbacks
  return componentCallbacks.inject(qualifier, parameters)
}

object GeneralUtils {

  fun formatMilliseconds(duration: Long): String {
    val seconds = (duration / 1000).toInt() % 60
    val minutes = (duration / (1000 * 60) % 60).toInt()
    val hours = (duration / (1000 * 60 * 60) % 24).toInt()
    "${timeAddZeros(hours, false)}:${timeAddZeros(minutes)}:${timeAddZeros(seconds)}".apply {
      return if (startsWith(":")) replaceFirst(":", "") else this
    }
  }

  private fun timeAddZeros(number: Int?, showIfIsZero: Boolean = true): String {
    return when (number) {
      0 -> if (showIfIsZero) "0${number}" else ""
      1, 2, 3, 4, 5, 6, 7, 8, 9 -> "0${number}"
      else -> number.toString()
    }
  }

  fun getTotalTime(songList: List<Song>): Long {
    var minutes = 0L
    var hours = 0L
    var seconds = 0L
    for (song in songList) {
      seconds += (song.duration / 1000 % 60).toLong()
      minutes += (song.duration / (1000 * 60) % 60).toLong()
      hours += (song.duration / (1000 * 60 * 60) % 24).toLong()
    }
    return hours * (1000 * 60 * 60) + minutes * (1000 * 60) + seconds * 1000
  }

  @Suppress("DEPRECATION")
  fun getAlbumArtBitmap(context: Context, albumId: Long): Bitmap {
    try {
      return when {
        SDK_INT >= P -> {
          val source = ImageDecoder.createSource(
            context.contentResolver,
            getAlbumArtUri(albumId)
          )
          ImageDecoder.decodeBitmap(source)
        }
        else -> MediaStore.Images.Media.getBitmap(
          context.contentResolver,
          getAlbumArtUri(albumId)
        )
      }
    } catch (e: FileNotFoundException) {
      Timber.e(e)
    } catch (e: UnsupportedOperationException) {
      Timber.e(e)
    }
    return ContextCompat.getDrawable(context, R.drawable.ic_music_note)!!.toBitmap()
//    return BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
  }

  fun getExtraBundle(queue: LongArray, title: String): Bundle {
    return getExtraBundle(queue, title, 0)
  }

  fun getExtraBundle(queue: LongArray, title: String, seekTo: Int?): Bundle {
    val bundle = Bundle()
    bundle.putLongArray(QUEUE_INFO_KEY, queue)
    bundle.putString(SONG_LIST_NAME, title)
    if (seekTo != null)
      bundle.putInt(SEEK_TO_POS, seekTo)
    else bundle.putInt(SEEK_TO_POS, 0)
    return bundle
  }

  fun isOreo() = SDK_INT >= O
  fun getAlbumArtUri(albumId: Long): Uri = withAppendedId(ARTWORK_URI, albumId)
  fun getSongUri(songId: Long): Uri = withAppendedId(SONG_URI, songId)
}
