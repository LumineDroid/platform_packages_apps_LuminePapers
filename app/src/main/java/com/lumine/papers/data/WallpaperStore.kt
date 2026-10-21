package com.lumine.papers.data

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Pulls wallpapers out of the assets repo into the local cache. */
object WallpaperStore {

  private const val BASE =
    "https://raw.githubusercontent.com/LumineDroid/LuminePapers-assets/main/wallpapers/"

  fun fileFor(context: Context, wall: Wallpaper): File =
    File(context.cacheDir, "wallpapers/${wall.file}")

  /** Local file for a wallpaper, downloading it on first use. Null when offline and uncached. */
  suspend fun ensure(context: Context, wall: Wallpaper): File? = withContext(Dispatchers.IO) {
    val target = fileFor(context, wall)
    if (target.exists()) return@withContext target
    try {
      target.parentFile?.mkdirs()
      val tmp = File(target.parentFile, "${wall.file}.tmp")
      URL(BASE + wall.file).openStream().use { input ->
        FileOutputStream(tmp).use { output -> input.copyTo(output) }
      }
      tmp.renameTo(target)
      target
    } catch (_: Exception) {
      null
    }
  }
}
