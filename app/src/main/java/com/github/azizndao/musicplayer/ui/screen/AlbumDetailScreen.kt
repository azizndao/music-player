package com.github.azizndao.musicplayer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.github.azizndao.musicplayer.R
import com.github.azizndao.musicplayer.extensions.toIDList
import com.github.azizndao.musicplayer.models.Album
import com.github.azizndao.musicplayer.models.Song
import com.github.azizndao.musicplayer.ui.viewmodels.AlbumViewModel
import com.github.azizndao.musicplayer.ui.viewmodels.MainViewModel
import com.github.azizndao.musicplayer.ui.viewmodels.SongDetailViewModel
import com.github.azizndao.musicplayer.ui.views.AlbumLargeCard
import com.github.azizndao.musicplayer.ui.views.SongCard
import com.github.azizndao.musicplayer.utils.BeatConstants
import com.github.azizndao.musicplayer.utils.GeneralUtils
import com.github.azizndao.musicplayer.utils.GeneralUtils.getExtraBundle
import com.github.azizndao.musicplayer.utils.inject
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun AlbumDetailScreen(id: Long) {
  val songsDetailViewModel by inject<SongDetailViewModel>()
  val albumViewModel by inject<AlbumViewModel>()
  val mainViewModel by inject<MainViewModel>()
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      Column {
        TopAppBar(
          backgroundColor = MaterialTheme.colors.surface,
          elevation = 0.dp,
          navigationIcon = {
            IconButton(onClick = { mainViewModel.popBackStack() }) {
              Icon(
                painterResource(id = R.drawable.ic_round_arrow_back_24),
                contentDescription = null
              )
            }
          },
          title = { Text(stringResource(id = R.string.app_name)) }
        )
        Divider()
      }
    },
    content = {
      BodyContent(
        album = albumViewModel.getAlbum(id),
        data = albumViewModel.getSongsByAlbum(id),
        listState = listState
      )
    },
    floatingActionButton = {
      if (listState.firstVisibleItemIndex > 0) FloatingActionButton(
        onClick = { scope.launch { listState.animateScrollToItem(0) } },
        backgroundColor = MaterialTheme.colors.primary
      ) {
        Icon(
          painterResource(id = R.drawable.ic_arrow_upward),
          contentDescription = null
        )
      }
    }
  )
}

@ExperimentalMaterialApi
@Composable
private fun BodyContent(
  album: Album,
  data: LiveData<List<Song>>,
  listState: LazyListState
) {
  val songs by data.observeAsState(initial = listOf())
  val mainViewModel by inject<MainViewModel>()
  LazyColumn(state = listState) {
    item {
      AlbumLargeCard(
        album = album,
        modifier = Modifier.padding(12.dp),
        onPlayAllClick = {
          val extras = getExtraBundle(songs.toIDList(), BeatConstants.ALBUM_KEY)
          mainViewModel.mediaItemClicked(songs.first().toMediaItem(), extras)
        },
        onPlayShuffledClick = {})
    }
    items(songs) { song ->
      SongCard(
        song = song,
        onClick = {
          val extras = GeneralUtils.getExtraBundle(songs.toIDList(), album.title)
          mainViewModel.mediaItemClicked(song.toMediaItem(), extras)
        }
      )
    }
  }
}
