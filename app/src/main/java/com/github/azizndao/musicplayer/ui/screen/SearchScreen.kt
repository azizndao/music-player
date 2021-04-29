package com.github.azizndao.musicplayer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.azizndao.musicplayer.ui.viewmodels.MainViewModel
import com.github.azizndao.musicplayer.utils.inject

@Composable
fun SearchScreen(default: String = "") {
  val mainViewModel by inject<MainViewModel>()
  Column {
    var query by remember { mutableStateOf(default) }
    Row {
      IconButton(onClick = { mainViewModel.popBackStack() }) {
        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
      }
      BasicTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier
          .height(45.dp)
          .fillMaxWidth()
      )
      IconButton(onClick = { }) {
        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
      }
    }
    Divider()
  }
}