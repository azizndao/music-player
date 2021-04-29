package com.github.azizndao.musicplayer.repository

import org.koin.dsl.bind
import org.koin.dsl.module

val repositoriesModule = module {
  factory { SongsRepositoryImplementation(get()) } bind SongsRepository::class
  factory { AlbumsRepositoryImplementation(get()) } bind AlbumsRepository::class
  factory { ArtistsRepositoryImplementation(get()) } bind ArtistsRepository::class
}