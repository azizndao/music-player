package com.github.azizndao.musicplayer.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.azizndao.musicplayer.models.Song
import com.github.azizndao.musicplayer.ui.viewmodels.SongViewModel
import com.github.azizndao.musicplayer.utils.GeneralUtils
import com.github.azizndao.musicplayer.utils.inject

@ExperimentalMaterialApi
@Composable
fun SongsList(onItemClick: (Song) -> Unit) {
  val viewModel by inject<SongViewModel>()
  val songs by viewModel.getSongList().observeAsState(listOf())
  LazyColumn(contentPadding = PaddingValues(bottom = 62.dp)) {
    items(songs) { song ->
      SongCard(song = song, onClick = { onItemClick(song) })
    }
  }
}

@ExperimentalMaterialApi
@Composable
fun SongCard(song: Song, modifier: Modifier = Modifier, onClick: () -> Unit) {
  ListItem(
    modifier = modifier
      .background(color = MaterialTheme.colors.onSurface.copy(.04f))
      .padding(8.dp, 4.dp)
      .clip(MaterialTheme.shapes.medium)
      .background(color = MaterialTheme.colors.surface)
      .clickable { onClick() },
    icon = {
      AlbumCoverImage(
        uri = GeneralUtils.getAlbumArtUri(song.albumId),
        modifier = Modifier
          .size(48.dp)
          .clip(MaterialTheme.shapes.medium)
          ,
      )
    },
    text = { Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    secondaryText = { Text(song.artist, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    trailing = { Text(GeneralUtils.formatMilliseconds(song.duration.toLong())) }
  )
}