package com.lumine.papers.theme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import java.io.File

/** Seed colours pulled out of a wallpaper. Drives the whole Material scheme. */
data class WallpaperPalette(val primary: Color, val secondary: Color, val tertiary: Color) {
  companion object {
    val Default = WallpaperPalette(Color(0xFFB388FF), Color(0xFF80CBC4), Color(0xFFFFAB91))
  }
}

fun Color.mix(other: Color, amount: Float): Color =
  Color(
    red = red + (other.red - red) * amount,
    green = green + (other.green - green) * amount,
    blue = blue + (other.blue - blue) * amount,
  )

fun WallpaperPalette.scheme(isDark: Boolean): ColorScheme =
  if (isDark) {
    val base = Color(0xFF0A0A0E)
    darkColorScheme(
      primary = primary,
      secondary = secondary,
      tertiary = tertiary,
      background = base.mix(primary, 0.10f),
      surface = base.mix(primary, 0.18f),
      surfaceVariant = base.mix(secondary, 0.28f),
    )
  } else {
    val base = Color(0xFFFCFAFF)
    lightColorScheme(
      primary = primary.mix(Color(0xFF1A1030), 0.42f),
      secondary = secondary.mix(Color(0xFF1A1030), 0.42f),
      tertiary = tertiary.mix(Color(0xFF1A1030), 0.42f),
      background = base.mix(primary, 0.06f),
      surface = base.mix(primary, 0.11f),
      surfaceVariant = base.mix(secondary, 0.16f),
    )
  }

fun Bitmap.toWallpaperPalette(): WallpaperPalette {
  val palette = Palette.from(this).generate()
  fun best(vararg s: Palette.Swatch?) = s.firstNotNullOfOrNull { it } ?: palette.dominantSwatch

  val primary = best(palette.vibrantSwatch, palette.dominantSwatch)
  val secondary = best(palette.mutedSwatch, palette.dominantSwatch)
  val tertiary = best(palette.lightVibrantSwatch, palette.darkVibrantSwatch, primary)

  return WallpaperPalette(
    primary = Color(primary?.rgb ?: 0xFFB388FF.toInt()),
    secondary = Color(secondary?.rgb ?: 0xFF80CBC4.toInt()),
    tertiary = Color(tertiary?.rgb ?: 0xFFFFAB91.toInt()),
  )
}

/** Decodes a wallpaper down to [maxDim] so palette extraction stays cheap. */
fun decodeWallpaperPalette(file: File, maxDim: Int = 160): WallpaperPalette? {
  val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
  BitmapFactory.decodeFile(file.path, bounds)

  var sample = 1
  val longest = maxOf(bounds.outWidth, bounds.outHeight)
  while (longest / (sample * 2) >= maxDim) sample *= 2

  val opts = BitmapFactory.Options().apply { inSampleSize = sample }
  return BitmapFactory.decodeFile(file.path, opts)?.toWallpaperPalette()
}
