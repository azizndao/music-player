package com.github.azizndao.musicplayer.models

import com.github.azizndao.musicplayer.extensions.addIfNotEmpty
import com.github.azizndao.musicplayer.extensions.fromJson
import com.github.azizndao.musicplayer.extensions.khz
import com.google.gson.Gson
import org.jaudiotagger.audio.AudioFile

data class ExtraInfo(
  val bitRate: String,
  val fileType: String,
  val frequency: String,
  val queuePosition: String
) {
  companion object {
    fun fromString(info: String): ExtraInfo {
      return Gson().fromJson(info)
    }

    fun createFromAudioFile(audioFile: AudioFile, queuePosition: String): ExtraInfo {
      return ExtraInfo(
        audioFile.audioHeader.bitRate.addIfNotEmpty("kbps"),
        audioFile.file.extension,
        audioFile.audioHeader.sampleRate.khz().addIfNotEmpty("khz"),
        queuePosition
      )
    }
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }
}
