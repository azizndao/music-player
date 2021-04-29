package com.github.azizndao.musicplayer.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.github.azizndao.musicplayer.ui.screen.*
import com.github.azizndao.musicplayer.ui.theme.MusicPlayerTheme
import com.github.azizndao.musicplayer.ui.viewmodels.*
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

  private val songDetailViewModel by inject<SongDetailViewModel>()
  private val mainViewModel by inject<MainViewModel>()

  @ExperimentalMaterialApi
  @ExperimentalFoundationApi
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 2)
    setContent {
      val navController = rememberNavController()
      MusicPlayerTheme {
        mainViewModel.navigator = navController
        NavHost(navController = navController, startDestination = "homeScreen") {
          composable("homeScreen") { HomeScreen() }

          composable(
            "playlists/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
          ) { PlaylistItemScreen(id = it.arguments!!.getLong("id")) }

          composable(
            "albums/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
          ) {
            AlbumDetailScreen(id = it.arguments!!.getLong("id"))
          }

          composable(
            "artists/{name}",
            arguments = listOf(navArgument("name") { type = NavType.LongType })
          ) { ArtistItemScreen(name = it.arguments!!.getLong("name")) }

          composable(
            "search?q={query}",
            arguments = listOf(navArgument("query") { type = NavType.StringType })
          ) {
            SearchScreen(default = it.arguments!!.getString("query") ?: "")
          }
        }
      }
    }
  }
}
