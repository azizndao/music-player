package com.github.azizndao.musicplayer.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.azizndao.musicplayer.R
import com.github.azizndao.musicplayer.ui.viewmodels.*
import com.github.azizndao.musicplayer.ui.views.AlbumsList
import com.github.azizndao.musicplayer.ui.views.ArtistsList
import com.github.azizndao.musicplayer.ui.views.SongsList
import com.github.azizndao.musicplayer.utils.GeneralUtils.getExtraBundle
import com.github.azizndao.musicplayer.utils.inject


@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun HomeScreen() {
  var selectedTabIndex by remember { mutableStateOf(1) }
  val scaffoldState = rememberBottomSheetScaffoldState()
  val mainViewModel by inject<MainViewModel>()
  BottomSheetScaffold(
    scaffoldState = scaffoldState,
    topBar = { TopBar(selectedTabIndex, viewModel = mainViewModel) { selectedTabIndex = it } },
    sheetContent = {
      if (scaffoldState.bottomSheetState.isCollapsed) {
        CollapsedBottomSheet()
      } else {
        ExpandedBottomSheet()
      }
    }
  ) {
    val context = LocalContext.current
    Crossfade(targetState = selectedTabIndex) { target ->
      when (target) {
        0 -> PlaylistsList()
        1 -> AlbumsList { album -> mainViewModel.navigate("albums/${album.id}") }
        2 -> ArtistsList { artist -> mainViewModel.navigate("artists/${artist.id}") }
        3 -> SongsList { song ->
        }
        else -> throw Exception()
      }
    }
  }
}

@Composable
fun ExpandedBottomSheet() {
  Column(
    modifier = Modifier
      .fillMaxSize()
  ) {

  }
}

@Composable
fun CollapsedBottomSheet() {
  Column(
    modifier = Modifier
      .fillMaxSize()
  ) {

  }
}

@Composable
private fun TopBar(
  selectedTabIndex: Int,
  viewModel: MainViewModel,
  onIndexChange: (Int) -> Unit
) {
  var showMorePopup by remember { mutableStateOf(false) }
  var showSorter by remember { mutableStateOf(false) }
  val titles = listOf(
    stringResource(R.string.playlists),
    stringResource(R.string.albums),
    stringResource(R.string.artists),
    stringResource(R.string.tracks)
  )
  Column {
    TopAppBar(
      title = { Text(stringResource(R.string.app_name)) },
      backgroundColor = MaterialTheme.colors.surface,
      actions = {
        IconButton(onClick = { viewModel.navigate("search") }) {
          Icon(painterResource(R.drawable.ic_search), contentDescription = null)
        }
        IconButton(onClick = { showSorter = true }) {
          Icon(painterResource(R.drawable.ic_sort), contentDescription = null)
          if (showSorter) SortDialog { showSorter = false }
        }
        IconButton(onClick = { showMorePopup = true }) {
          Icon(painterResource(R.drawable.ic_more_vert), contentDescription = null)
          MoreDropDownPopup(showMorePopup) { showMorePopup = false }
        }
      }
    )
    ScrollableTabRow(
      selectedTabIndex = selectedTabIndex,
      edgePadding = 0.dp,
      backgroundColor = MaterialTheme.colors.surface,
      indicator = { tabPositions ->
        TabRowDefaults.Indicator(
          Modifier
            .tabIndicatorOffset(tabPositions[selectedTabIndex])
            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
          height = 3.dp,
          color = MaterialTheme.colors.primary
        )
      }
    ) {
      for ((index, item) in titles.withIndex()) {
        Tab(
          selected = selectedTabIndex == index,
          selectedContentColor = MaterialTheme.colors.primary,
          unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
          onClick = { onIndexChange(index) },
          text = { Text(item) })
      }
    }
  }
}

@Composable
private fun MoreDropDownPopup(expanded: Boolean, onDismiss: () -> Unit) {
  DropdownMenu(expanded = expanded, onDismissRequest = { onDismiss() }) {
    DropdownMenuItem(onClick = { }) {
      Text(text = stringResource(R.string.timer))
    }
    DropdownMenuItem(onClick = { }) {
      Text(text = stringResource(R.string.equalizer))
    }
    DropdownMenuItem(onClick = { }) {
      Text(text = stringResource(R.string.settings))
    }
    DropdownMenuItem(onClick = { }) {
      Text(text = stringResource(R.string.tutorial))
    }
  }
}

@Composable
private fun SortDialog(onDismissRequest: () -> Unit) {
  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    title = { Text(stringResource(R.string.sort_by)) },
    text = {
      Column {
        RadioButton(selected = false, onClick = { }) {
          Text(stringResource(R.string.sort_by_name))
        }
        RadioButton(selected = false, onClick = { }) {
          Text(stringResource(R.string.sort_by_number_of_track))
        }
        RadioButton(selected = false, onClick = { }) {
          Text(stringResource(R.string.sort_by_number_of_track))
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        RadioButton(selected = false, onClick = { }) {
          Text(stringResource(R.string.increasing))
        }
        RadioButton(selected = false, onClick = { }) {
          Text(stringResource(R.string.decreading))
        }
      }
    },
    confirmButton = {
      TextButton(onClick = { onDismissRequest() }) {
        Text(stringResource(id = android.R.string.ok))
      }
    },
    dismissButton = {
      TextButton(onClick = { onDismissRequest() }) {
        Text(stringResource(id = android.R.string.cancel))
      }
    }
  )
}

@Composable
private fun RadioButton(
  selected: Boolean = false,
  onClick: () -> Unit,
  text: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.small)
      .clickable { onClick() }
      .padding(12.dp, 8.dp)
  ) {
    Row {
      RadioButton(selected = selected, onClick = { onClick() })
      Spacer(modifier = Modifier.width(8.dp))
      text()
    }
  }
}

@Composable
fun PlaylistsList() {

}

