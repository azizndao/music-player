package com.github.azizndao.musicplayer.utils

import android.content.Context
import com.github.azizndao.musicplayer.models.ExtraInfo
import com.github.azizndao.musicplayer.models.Song
import com.github.azizndao.musicplayer.repository.SongsRepositoryImplementation
import com.github.azizndao.musicplayer.utils.BeatConstants.SONG_ID_DEFAULT
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.CannotWriteException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

object TagUtils {
  fun writeTag(path: String, key: FieldKey, value: String): Boolean {
    try {
      val audioFile = AudioFileIO.read(Objects.requireNonNull(File(path)))

      audioFile.tag.setField(key, value)
      audioFile.commit()
      return true
    } catch (ex: CannotReadException) {
      Timber.e(ex)
    } catch (ex: IOException) {
      Timber.e(ex)
    } catch (ex: TagException) {
      Timber.e(ex)
    } catch (ex: ReadOnlyFileException) {
      Timber.e(ex)
    } catch (ex: InvalidAudioFrameException) {
      Timber.e(ex)
    } catch (ex: IllegalArgumentException) {
      Timber.e(ex)
    } catch (ex: CannotWriteException) {
      Timber.e(ex)
    }
    return false
  }

  fun readTagsAsSong(context: Context, path: String): Song {
    try {
      val audioFile = AudioFileIO.read(Objects.requireNonNull(File(path)))

      val song = Song.createFromAudioFile(audioFile)
      val ids =
        SongsRepositoryImplementation(context).getAlbumIdArtistId(song.album, song.artist)
      song.artistId = ids[0]
      song.albumId = ids[1]
      if (song.title.isEmpty() || song.title.isBlank()) {
        return Song(
          song.id,
          title = audioFile.file.name,
          artist = "Unknown",
          album = "Unknown",
          duration = song.duration,
          path = song.path
        )
      }
      return song
    } catch (ex: CannotReadException) {
      Timber.e(ex)
    } catch (ex: IOException) {
      Timber.e(ex)
    } catch (ex: TagException) {
      Timber.e(ex)
    } catch (ex: ReadOnlyFileException) {
      Timber.e(ex)
    } catch (ex: InvalidAudioFrameException) {
      Timber.e(ex)
    } catch (ex: IllegalArgumentException) {
      Timber.e(ex)
    } catch (ex: CannotWriteException) {
      Timber.e(ex)
    }
    return Song(
      id = SONG_ID_DEFAULT,
      title = File(path).name,
      artist = "Unknown",
      album = "Unknown",
      path = path
    )
  }

  fun readExtraTags(path: String, queuePosition: String = ""): ExtraInfo {
    try {
      val audioFile = AudioFileIO.read(Objects.requireNonNull(File(path)))

      return ExtraInfo.createFromAudioFile(audioFile, queuePosition)
    } catch (ex: CannotReadException) {
      Timber.e(ex)
    } catch (ex: IOException) {
      Timber.e(ex)
    } catch (ex: TagException) {
      Timber.e(ex)
    } catch (ex: ReadOnlyFileException) {
      Timber.e(ex)
    } catch (ex: InvalidAudioFrameException) {
      Timber.e(ex)
    } catch (ex: IllegalArgumentException) {
      Timber.e(ex)
    } catch (ex: CannotWriteException) {
      Timber.e(ex)
    }
    return ExtraInfo("", File(path).extension, "", queuePosition)
  }
}