package com.github.azizndao.musicplayer.ui.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.azizndao.musicplayer.R
import com.github.azizndao.musicplayer.models.Artist
import com.github.azizndao.musicplayer.ui.viewmodels.ArtistViewModel
import com.github.azizndao.musicplayer.utils.inject

@Composable
fun ArtistCard(artist: Artist, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
    ) {
    }
    Text(
      artist.name,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.padding(4.dp, 4.dp, 4.dp, 0.dp),
      maxLines = 1
    )
    Text(
      stringResource(id = R.string.albums_count, artist.albumCount),
      style = MaterialTheme.typography.caption,
      modifier = Modifier.padding(4.dp, 0.dp)
    )
    Text(
      stringResource(id = R.string.tracks_count, artist.songCount),
      style = MaterialTheme.typography.caption,
      modifier = Modifier.padding(4.dp, 0.dp, 4.dp, 4.dp)
    )
  }
}


@ExperimentalFoundationApi
@Composable
fun ArtistsList(onItemClick: (Artist) -> Unit) {
  val viewModel by inject<ArtistViewModel>()
  val artists by viewModel.getArtists().observeAsState(listOf())
  LazyVerticalGrid(
    cells = GridCells.Fixed(2),
    contentPadding = PaddingValues(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 62.dp),
  ) {
    items(artists) { item ->
      ArtistCard(
        artist = item,
        modifier = Modifier
          .padding(6.dp)
          .pointerInput(Unit) {
            detectTapGestures(
              onTap = { onItemClick(item) },
              onLongPress = {}
            )
          }
      )
    }
  }
}
