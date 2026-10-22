package com.lumine.papers.theme

import android.app.Application
import android.app.WallpaperManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

/** User-facing theme choice. */
enum class ThemeMode {
  Light,
  Dark,
  System,
}

fun ThemeMode.isDark(systemDark: Boolean): Boolean =
  when (this) {
    ThemeMode.Dark -> true
    ThemeMode.Light -> false
    ThemeMode.System -> systemDark
  }

data class ThemeState(
  val mode: ThemeMode = ThemeMode.System,
  /** Bumped whenever the system wallpaper changes so colours get re-read. */
  val wallpaperTick: Int = 0,
)

/** Tracks the theme choice and pings the UI when the system wallpaper changes. */
class ThemeController(app: Application) : AndroidViewModel(app) {

  private val prefs = app.getSharedPreferences("theme", 0)
  private val wallpaperManager = WallpaperManager.getInstance(app)

  var state by mutableStateOf(ThemeState())
    private set

  private val colorsListener =
    WallpaperManager.OnColorsChangedListener { _, _ -> refreshWallpaperColors() }

  init {
    state = ThemeState(mode = readMode())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      wallpaperManager.addOnColorsChangedListener(colorsListener, Handler(Looper.getMainLooper()))
    }
  }

  fun setMode(mode: ThemeMode) {
    state = state.copy(mode = mode)
    prefs.edit().putString(KEY_MODE, mode.name).apply()
  }

  /** Forces a re-read of the wallpaper colours (SET AS and system changes). */
  fun refreshWallpaperColors() {
    state = state.copy(wallpaperTick = state.wallpaperTick + 1)
  }

  override fun onCleared() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      wallpaperManager.removeOnColorsChangedListener(colorsListener)
    }
    super.onCleared()
  }

  private fun readMode(): ThemeMode =
    when (prefs.getString(KEY_MODE, null)) {
      ThemeMode.Light.name -> ThemeMode.Light
      ThemeMode.Dark.name -> ThemeMode.Dark
      ThemeMode.System.name -> ThemeMode.System
      // Installs that predate the three-way choice only stored a dark boolean.
      null ->
        if (prefs.contains(KEY_DARK)) {
          if (prefs.getBoolean(KEY_DARK, true)) ThemeMode.Dark else ThemeMode.Light
        } else ThemeMode.System
      else -> ThemeMode.System
    }

  private companion object {
    const val KEY_MODE = "mode"
    const val KEY_DARK = "dark"
  }
}
