package com.lumine.papers.data

/** A group of wallpapers sharing the same category. */
data class WallCategory(
  val id: String,
  val name: String,
  val walls: List<Wallpaper>,
)
