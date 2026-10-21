package com.lumine.papers.theme

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.data.WallpaperRepository
import com.lumine.papers.data.WallpaperStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ThemeState(
  val wallpaper: Wallpaper? = null,
  val palette: WallpaperPalette = WallpaperPalette.Default,
  val darkMode: Boolean = true,
)

/** Holds the live theme and reseeds it whenever another wallpaper is picked. */
class ThemeController(app: Application) : AndroidViewModel(app) {

  private val prefs = app.getSharedPreferences("theme", 0)

  var state by mutableStateOf(ThemeState())
    private set

  init {
    state = ThemeState(darkMode = prefs.getBoolean(KEY_DARK, true))
    viewModelScope.launch {
      val walls = WallpaperRepository.load(getApplication())
      val saved = WallpaperRepository.byId(walls, prefs.getString(KEY_WALL, null).orEmpty())
      select(saved ?: walls.firstOrNull(), persist = false)
    }
  }

  fun toggleDarkMode() {
    state = state.copy(darkMode = !state.darkMode)
    prefs.edit().putBoolean(KEY_DARK, state.darkMode).apply()
  }

  fun select(wall: Wallpaper?, persist: Boolean = true) {
    if (wall == null) return
    viewModelScope.launch {
      val palette =
        withContext(Dispatchers.IO) {
          val file = WallpaperStore.ensure(getApplication(), wall)
          file?.let { decodeWallpaperPalette(it) } ?: WallpaperPalette.Default
        }
      state = state.copy(wallpaper = wall, palette = palette)
      if (persist) prefs.edit().putString(KEY_WALL, wall.id).apply()
    }
  }

  private companion object {
    const val KEY_WALL = "wallpaper"
    const val KEY_DARK = "dark"
  }
}
