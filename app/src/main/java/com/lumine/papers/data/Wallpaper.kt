package com.lumine.papers.data

/** A single wallpaper entry, parsed from assets/wallpapers.json. */
data class Wallpaper(
  val name: String,
  val file: String,
  val category: String,
  val author: String,
) {
  val id: String = file.substringBeforeLast('.')

  val assetPath: String = "wallpapers/$file"
}
