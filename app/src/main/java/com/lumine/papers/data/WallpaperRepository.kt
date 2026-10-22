package com.lumine.papers.data

import android.content.Context
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray

object WallpaperRepository {

  private const val MANIFEST =
    "https://raw.githubusercontent.com/LumineDroid/LuminePapers-assets/main/wallpapers.json"
  private const val CACHE_FILE = "wallpapers.json"
  private const val CONNECT_TIMEOUT_MS = 8_000
  private const val READ_TIMEOUT_MS = 12_000

  private val mutex = Mutex()

  @Volatile private var cache: List<Wallpaper> = emptyList()

  suspend fun load(context: Context): List<Wallpaper> = mutex.withLock {
    if (cache.isNotEmpty()) return@withLock cache
    withContext(Dispatchers.IO) {
      val remote = fetchManifest()
      val parsedRemote = parseOrNull(remote)
      if (remote != null && parsedRemote != null) {
        // Only ever persist a manifest we could actually parse, so a bad
        // response can never poison the offline cache.
        runCatching { File(context.cacheDir, CACHE_FILE).writeText(remote) }
        cache = parsedRemote
      } else {
        cache = parseOrNull(readCached(context)) ?: emptyList()
      }
      cache
    }
  }

  fun categories(walls: List<Wallpaper>): List<WallCategory> =
    walls
      .groupBy { it.category }
      .map { (name, items) -> WallCategory(id = name.lowercase(), name = name, walls = items) }

  fun byId(walls: List<Wallpaper>, id: String): Wallpaper? = walls.firstOrNull { it.id == id }

  private fun fetchManifest(): String? {
    val conn = URL(MANIFEST).openConnection() as? HttpURLConnection ?: return null
    return try {
      conn.connectTimeout = CONNECT_TIMEOUT_MS
      conn.readTimeout = READ_TIMEOUT_MS
      if (conn.responseCode != HttpURLConnection.HTTP_OK) null
      else conn.inputStream.bufferedReader().use { it.readText() }
    } catch (_: Exception) {
      null
    } finally {
      conn.disconnect()
    }
  }

  private fun readCached(context: Context): String? =
    runCatching {
      val file = File(context.cacheDir, CACHE_FILE)
      if (file.exists()) file.readText() else null
    }.getOrNull()

  private fun parseOrNull(raw: String?): List<Wallpaper>? {
    if (raw.isNullOrBlank()) return null
    return try {
      val array = JSONArray(raw)
      buildList {
        for (i in 0 until array.length()) {
          val item = array.getJSONObject(i)
          add(
            Wallpaper(
              name = item.getString("name"),
              file = item.getString("file"),
              category = item.getString("category"),
              author = item.optString("author", "Unknown"),
            )
          )
        }
      }
      // Duplicate file names would collide on list keys and lookup ids.
        .distinctBy { it.id }
    } catch (_: Exception) {
      null
    }
  }
}
