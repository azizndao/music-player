package com.github.azizndao.musicplayer.models

data class QueueInfo(
  val id: Long = 0,
  val name: String = "",
  val repeatMode: Int = 0,
  val shuffleMode: Int = 0,
  val seekPos: Long = 0,
  val state: Int = 0
)