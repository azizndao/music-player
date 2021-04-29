/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crrl.beatplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.azizndao.musicplayer.models.SearchData
import com.github.azizndao.musicplayer.repository.AlbumsRepository
import com.github.azizndao.musicplayer.repository.ArtistsRepository
import com.github.azizndao.musicplayer.repository.SongsRepository
import com.github.azizndao.musicplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class SearchViewModel(
  private val songRepository: SongsRepository,
  private val albumRepository: AlbumsRepository,
  private val artistsRepository: ArtistsRepository
) : CoroutineViewModel(Main) {
  private val searchLiveData = MutableLiveData<SearchData>()
  private val searchData = SearchData()

  fun search(searchString: String) {
    if (searchString.isNotEmpty()) {
      launch {
        val songs = withContext(Dispatchers.IO) {
          songRepository.search(searchString, 10).toMutableList()
        }
        searchData.songList = songs
        searchLiveData.postValue(searchData)
      }

      launch {
        val albums = withContext(Dispatchers.IO) {
          albumRepository.search(searchString, 10).toMutableList()
        }
        searchData.albumList = albums
        searchLiveData.postValue(searchData)
      }

      launch {
        val artist = withContext(Dispatchers.IO) {
          artistsRepository.search(searchString, 10).toMutableList()
        }
        searchData.artistList = artist
        searchLiveData.postValue(searchData)
      }
    } else {
      searchLiveData.postValue(searchData.flush())
    }
  }

  fun searchLiveData(): LiveData<SearchData> {
    if (searchLiveData.value == null) searchLiveData.value = SearchData()
    return searchLiveData
  }
}