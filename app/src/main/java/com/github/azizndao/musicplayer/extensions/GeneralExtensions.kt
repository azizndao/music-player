package com.github.azizndao.musicplayer.extensions

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import com.github.azizndao.musicplayer.utils.BeatConstants.READ_ONLY_MODE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException

const val BYTE_OFFSET = 44;

inline fun <reified T> Gson.fromJson(json: String): T =
  this.fromJson(json, object : TypeToken<T>() {}.type)

fun Uri.toFileDescriptor(context: Context): ParcelFileDescriptor? {
  return try {
    context.contentResolver.openFileDescriptor(this, READ_ONLY_MODE, null)
  } catch (ex: FileNotFoundException) {
    null
  }
}

fun ByteArray.optimize(): ByteArray {
  val toIndex = (size / 2) - 1
  return if (toIndex > BYTE_OFFSET) copyOfRange(BYTE_OFFSET, toIndex) else this
}

operator fun Bundle.plus(other: Bundle) = this.apply { putAll(other) }