package com.github.azizndao.musicplayer

import android.app.Application
import com.github.azizndao.musicplayer.notifications.notificationModule
import com.github.azizndao.musicplayer.playback.playbackModule
import com.github.azizndao.musicplayer.utils.utilsModule
import com.github.azizndao.musicplayer.repository.repositoriesModule
import com.github.azizndao.musicplayer.ui.viewmodels.viewModelModule
import org.jaudiotagger.tag.TagOptionSingleton
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

  override fun onCreate() {
    super.onCreate()

    TagOptionSingleton.getInstance().isAndroid = true

    val modules = listOf(
      mainModel,
      notificationModule,
      playbackModule,
      repositoriesModule,
      viewModelModule,
      utilsModule
    )
    startKoin {
      androidContext(this@App)
      modules(modules)
    }
  }
}