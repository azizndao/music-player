package com.github.azizndao.musicplayer

import android.content.ComponentName
import com.github.azizndao.musicplayer.playback.services.BeatPlayerService
import com.github.azizndao.musicplayer.playback.PlaybackConnection
import com.github.azizndao.musicplayer.playback.PlaybackConnectionImplementation
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModel = module {
  single {
    val component = ComponentName(get(), BeatPlayerService::class.java)
    PlaybackConnectionImplementation(get(), component)
  } bind PlaybackConnection::class
}