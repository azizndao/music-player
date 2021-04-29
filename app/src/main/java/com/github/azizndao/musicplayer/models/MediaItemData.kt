package com.github.azizndao.musicplayer.models

import android.support.v4.media.MediaMetadataCompat

class MediaItemData(
  val id: Long = 0L,
  val title: String = "",
  val album: String = "",
  val artist: String = "",
  val albumId: Long = 0,
  val duration: Int = 0,
  val description: String = ""

) {
  companion object {
    fun pullMediaMetadata(metaData: MediaMetadataCompat?): MediaItemData? {
      metaData ?: return null
      val mediaId = metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
      val data = MediaItemData(
        id = if (mediaId == "") -1 else mediaId.toLong(),
        title = metaData.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
        album = metaData.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: "",
        artist = metaData.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "",
        duration = metaData.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt(),
        albumId = metaData.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
          ?.toLong() ?: 0,
        description = metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
          ?: ""
      )
      return if (data.id != -1L) data else null
    }
  }
}