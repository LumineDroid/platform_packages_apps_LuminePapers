package com.lumine.papers.data

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Pulls wallpapers out of the assets repo into the local cache. */
object WallpaperStore {

  private const val BASE =
    "https://raw.githubusercontent.com/LumineDroid/LuminePapers-assets/main/wallpapers/"

  private const val CONNECT_TIMEOUT_MS = 8_000
  private const val READ_TIMEOUT_MS = 20_000

  /** One lock per file so two screens never write the same temp file. */
  private val locks = ConcurrentHashMap<String, Mutex>()

  fun fileFor(context: Context, wall: Wallpaper): File =
    File(context.cacheDir, "wallpapers/${wall.file}")

  /** Local file for a wallpaper, downloading it on first use. Null when offline and uncached. */
  suspend fun ensure(context: Context, wall: Wallpaper): File? = withContext(Dispatchers.IO) {
    val target = fileFor(context, wall)
    if (target.exists()) return@withContext target

    val lock = locks.computeIfAbsent(wall.file) { Mutex() }
    lock.withLock {
      if (target.exists()) return@withLock target

      val tmp = File(target.parentFile, "${wall.file}.download")
      try {
        target.parentFile?.mkdirs()
        tmp.delete()

        val conn = URL(BASE + wall.file).openConnection() as HttpURLConnection
        conn.connectTimeout = CONNECT_TIMEOUT_MS
        conn.readTimeout = READ_TIMEOUT_MS
        try {
          // A 404 body must never be cached as an image.
          if (conn.responseCode != HttpURLConnection.HTTP_OK) return@withLock null
          conn.inputStream.use { input ->
            FileOutputStream(tmp).use { output -> input.copyTo(output) }
          }
        } finally {
          conn.disconnect()
        }

        if (tmp.length() > 0L && tmp.renameTo(target)) target else {
          tmp.delete()
          null
        }
      } catch (_: Exception) {
        tmp.delete()
        null
      }
    }
  }
}
