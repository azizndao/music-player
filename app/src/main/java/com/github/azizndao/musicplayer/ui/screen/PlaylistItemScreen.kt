package com.github.azizndao.musicplayer.ui.screen

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.azizndao.musicplayer.R


@Composable
fun PlaylistItemScreen(id: Long) {
  Scaffold(
    topBar = { TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }) },
    content = { }
  )
}