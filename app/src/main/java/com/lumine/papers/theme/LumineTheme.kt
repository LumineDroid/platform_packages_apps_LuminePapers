package com.lumine.papers.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Colours always describe the device's real home wallpaper: Monet on Android 12+,
 * a palette sampled from that same wallpaper below 12. Picking a wallpaper inside
 * the app never affects the theme; applying one does, via [ThemeState.wallpaperTick].
 */
@Composable
fun LumineTheme(state: ThemeState, content: @Composable () -> Unit) {
  val context = LocalContext.current
  val dark = state.mode.isDark(systemDark = isSystemInDarkTheme())
  val scheme = remember(state.wallpaperTick, dark, context) { colorScheme(context, dark) }
  MaterialTheme(colorScheme = scheme, content = content)
}

private fun colorScheme(context: Context, dark: Boolean): ColorScheme =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    runCatching {
      if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }.getOrElse { WallpaperPalette.Default.scheme(dark) }
  } else {
    systemWallpaperPalette(context).scheme(dark)
  }
