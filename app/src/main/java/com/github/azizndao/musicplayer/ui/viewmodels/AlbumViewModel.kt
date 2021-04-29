package com.github.azizndao.musicplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.azizndao.musicplayer.repository.AlbumsRepository
import com.github.azizndao.musicplayer.models.Album
import com.github.azizndao.musicplayer.models.Song
import com.github.azizndao.musicplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class AlbumViewModel(
  private val repository: AlbumsRepository
) : CoroutineViewModel(Main) {
  private val albumData = MutableLiveData<List<Album>>()
  private val songsByAlbum = MutableLiveData<List<Song>>()

  fun getAlbum(id: Long): Album {
    return repository.getAlbum(id)
  }

  fun getAlbums(): LiveData<List<Album>> {
    update()
    return albumData
  }

  fun getSongsByAlbum(id: Long): LiveData<List<Song>> {
    launch {
      val list = withContext(Dispatchers.IO) {
        repository.getSongsForAlbum(id)
      }
      songsByAlbum.postValue(list)
    }
    return songsByAlbum
  }

  fun update() {
    launch {
      val albums = withContext(Dispatchers.IO) {
        repository.getAlbums()
      }
      albumData.postValue(albums)
    }
  }
}