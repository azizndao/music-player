package com.github.azizndao.musicplayer.utils

import org.koin.dsl.bind
import org.koin.dsl.module

val utilsModule = module {
  factory { SettingsUtility(get()) }
  factory { QueueUtilsImplementation(get(), get(), get()) } bind QueueUtils::class
}