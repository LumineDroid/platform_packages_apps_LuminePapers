package com.lumine.papers.theme

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable

/**
 * Seeds sampled from the wallpaper the launcher actually shows. Only used below
 * Android 12, where the system has no built-in dynamic colour engine.
 */
internal fun systemWallpaperPalette(context: Context): WallpaperPalette {
  val drawable = runCatching { WallpaperManager.getInstance(context).fastDrawable }.getOrNull()
  val bitmap = drawable?.toSampledBitmap() ?: return WallpaperPalette.Default
  return runCatching { bitmap.toWallpaperPalette() }.getOrElse { WallpaperPalette.Default }
}

private fun Drawable.toSampledBitmap(maxDim: Int = 320): Bitmap? {
  val sourceWidth = intrinsicWidth
  val sourceHeight = intrinsicHeight
  if (sourceWidth <= 0 || sourceHeight <= 0) return null

  val scale = minOf(1f, maxDim.toFloat() / maxOf(sourceWidth, sourceHeight))
  val width = (sourceWidth * scale).toInt().coerceAtLeast(1)
  val height = (sourceHeight * scale).toInt().coerceAtLeast(1)

  return try {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, width, height)
    draw(canvas)
    bitmap
  } catch (_: Exception) {
    null
  }
}
