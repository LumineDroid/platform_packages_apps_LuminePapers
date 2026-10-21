package com.lumine.papers.data

import android.content.Context
import java.io.File
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray

object WallpaperRepository {

  private const val MANIFEST =
    "https://raw.githubusercontent.com/LumineDroid/LuminePapers-assets/main/wallpapers.json"

  private val mutex = Mutex()

  @Volatile private var cache: List<Wallpaper> = emptyList()

  suspend fun load(context: Context): List<Wallpaper> = mutex.withLock {
    if (cache.isNotEmpty()) return@withLock cache
    withContext(Dispatchers.IO) {
      val raw = readManifest(context)
      if (raw != null) cache = parse(raw)
      cache
    }
  }

  fun categories(walls: List<Wallpaper>): List<WallCategory> =
    walls
      .groupBy { it.category }
      .map { (name, items) -> WallCategory(id = name.lowercase(), name = name, walls = items) }

  fun byId(walls: List<Wallpaper>, id: String): Wallpaper? = walls.firstOrNull { it.id == id }

  // Network first, last good manifest as the offline fallback.
  private fun readManifest(context: Context): String? {
    val local = File(context.cacheDir, "wallpapers.json")
    return try {
      URL(MANIFEST).readText().also { local.writeText(it) }
    } catch (_: Exception) {
      if (local.exists()) local.readText() else null
    }
  }

  private fun parse(raw: String): List<Wallpaper> {
    val array = JSONArray(raw)
    return buildList {
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
  }
}
