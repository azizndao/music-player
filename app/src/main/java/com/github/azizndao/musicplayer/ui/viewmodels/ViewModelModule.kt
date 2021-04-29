package com.github.azizndao.musicplayer.ui.viewmodels

import com.crrl.beatplayer.ui.viewmodels.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
  single { MainViewModel(get()) }
  viewModel { SongDetailViewModel(get(), get()) }
  viewModel { SettingsViewModel(get()) }
  viewModel { ArtistViewModel(get()) }
  viewModel { AlbumViewModel(get()) }
  viewModel { SearchViewModel(get(), get(), get()) }
  viewModel { SongViewModel(get()) }
}