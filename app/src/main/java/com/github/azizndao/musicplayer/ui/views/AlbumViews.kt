package com.github.azizndao.musicplayer.ui.views

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.azizndao.musicplayer.R
import com.github.azizndao.musicplayer.models.Album
import com.github.azizndao.musicplayer.ui.theme.MusicPlayerTheme
import com.github.azizndao.musicplayer.ui.viewmodels.AlbumViewModel
import com.github.azizndao.musicplayer.utils.GeneralUtils
import com.github.azizndao.musicplayer.utils.inject
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState


@ExperimentalFoundationApi
@Composable
fun AlbumsList(onItemClick: (Album) -> Unit) {
  val albumsViewModel by inject<AlbumViewModel>()
  val albums by albumsViewModel.getAlbums().observeAsState()
  when {
    albums == null -> {

    }
    albums!!.isEmpty() -> {
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = stringResource(id = R.string.there_are_nothing),
          style = MaterialTheme.typography.h5
        )
      }
    }
    else -> {
      LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 6.dp, top = 6.dp, end = 6.dp, bottom = 62.dp)
      ) {
        items(albums!!) { item ->
          AlbumCard(
            album = item,
            modifier = Modifier
              .padding(6.dp)
              .pointerInput(Unit) {
                detectTapGestures(
                  onTap = { onItemClick(item) },
                  onLongPress = {
                  }
                )
              }
          )
        }
      }
    }

  }
}

@Composable
fun AlbumCard(
  album: Album,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    AlbumCoverImage(
      GeneralUtils.getAlbumArtUri(album.id),
      modifier = Modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.medium)
    )
    Text(
      text = album.title,
      style = MaterialTheme.typography.caption,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      modifier = Modifier.padding(horizontal = 4.dp)
    )
    Text(
      text = album.title,
      style = MaterialTheme.typography.caption,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      modifier = Modifier.padding(horizontal = 4.dp)
    )
    Text(
      text = stringResource(id = R.string.songs_count, album.songCount),
      style = MaterialTheme.typography.caption,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.padding(horizontal = 4.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
  }
}

@Preview(showBackground = true)
@Composable
fun AlbumLargeCardPreview() {
  MusicPlayerTheme {
    AlbumLargeCard(
      album = Album(
        id = 1,
        title = "Quran Arabic - French",
        artist = "Mishary Rashid Al Afasy & Youssouf Leclerc",
        songCount = 114
      ),
      onPlayAllClick = {},
      onPlayShuffledClick = {  }
    )
  }
}


@Composable
fun AlbumLargeCard(
  album: Album,
  modifier: Modifier = Modifier,
  onPlayAllClick: () -> Unit,
  onPlayShuffledClick: () -> Unit
) {
  Column(modifier = modifier) {
    Row {
      Surface(
        elevation = 12.dp, modifier = Modifier
          .clip(MaterialTheme.shapes.medium)
          .fillMaxWidth(0.4f)
          .aspectRatio(1f)
      ) {
        AlbumCoverImage(
          uri = GeneralUtils.getAlbumArtUri(album.id)
        )
      }
      Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(start = 12.dp)
      ) {
        Text(
          album.title,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          album.artist,
          overflow = TextOverflow.Ellipsis,
          maxLines = 2,
          style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          stringResource(R.string.tracks_count, album.songCount),
          style = MaterialTheme.typography.caption
        )

      }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
      Button(onClick = { onPlayAllClick() }) {
        Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
        Text(stringResource(id = R.string.play_all))
      }
      Spacer(modifier = Modifier.width(16.dp))
      OutlinedButton(onClick = { onPlayShuffledClick() }) {
        Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
        Text(stringResource(id = R.string.random))
      }
    }
  }
}

@Composable
fun AlbumCoverImage(uri: Uri?, modifier: Modifier = Modifier) {
  val painter = rememberCoilPainter(uri)
  Box(
    modifier = modifier
      .background(MaterialTheme.colors.onSurface.copy(0.12f))
      .aspectRatio(1f)
  ) {
    when (painter.loadState) {
      is ImageLoadState.Success -> Image(
        painter = painter,
        contentDescription = stringResource(R.string.album_cover),
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
      )
      else -> Icon(
        painter = painterResource(R.drawable.ic_baseline_album_24),
        tint = MaterialTheme.colors.onSurface.copy(0.12f),
        contentDescription = null,
        modifier = Modifier
          .align(Alignment.Center)
          .aspectRatio(0.7f)
      )
    }
  }
}