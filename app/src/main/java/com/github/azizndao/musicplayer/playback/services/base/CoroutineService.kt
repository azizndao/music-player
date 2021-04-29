package com.github.azizndao.musicplayer.playback.services.base

import androidx.media.MediaBrowserServiceCompat
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class CoroutineService(
  private val mainDispatcher: CoroutineDispatcher
) : MediaBrowserServiceCompat() {

  private val job = Job()
  protected val scope = CoroutineScope(job + mainDispatcher)

  protected fun launch(
    context: CoroutineContext = mainDispatcher,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ) = scope.launch(context, start, block)

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }
}