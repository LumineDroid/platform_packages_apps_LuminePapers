package com.lumine.papers.ui

import com.lumine.papers.data.Wallpaper

sealed interface Screen {
  data object Feed : Screen

  data class Preview(val wall: Wallpaper) : Screen

  data object Settings : Screen
}
