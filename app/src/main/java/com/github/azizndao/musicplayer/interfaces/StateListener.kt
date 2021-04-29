package com.github.azizndao.musicplayer.interfaces

import com.github.azizndao.musicplayer.enums.State

interface StateListener {
  fun onStateChanged(state: State, seconds: Int)
}