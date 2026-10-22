package com.lumine.papers.theme

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import java.io.FileInputStream

/**
 * Seeds sampled from the wallpaper the launcher actually shows. Only used below
 * Android 12, where the system has no built-in dynamic colour engine.
 */
internal fun systemWallpaperPalette(context: Context): WallpaperPalette {
  val wallpaperManager = WallpaperManager.getInstance(context)
  val sampled =
    runCatching { wallpaperManager.fastDrawable }
      .getOrNull()
      ?.toSampledBitmap()
      ?.let { bitmap -> runCatching { bitmap.toWallpaperPalette() }.getOrNull() }
  if (sampled != null) return sampled
  // Live wallpapers expose no drawable: fall back to the colours they report.
  return systemWallpaperSeed(context) ?: WallpaperPalette.Default
}

/**
 * The primary/secondary/tertiary colours the system reports for the current
 * wallpaper (live wallpapers included). Null below API 27 or when unknown.
 */
internal fun systemWallpaperSeed(context: Context): WallpaperPalette? =
  if (Build.VERSION.SDK_INT >= 27) {
    WallpaperManager.getInstance(context).systemColors()
  } else {
    null
  }

/**
 * Center-cropped strip of the wallpaper the launcher is showing, sized for the
 * About hero card. Tries the drawable first, then the raw wallpaper file;
 * null when the wallpaper is a live one or cannot be rendered.
 */
fun systemWallpaperStrip(
  context: Context,
  targetWidth: Int = 960,
  targetHeight: Int = 414,
): Bitmap? {
  val wallpaperManager = WallpaperManager.getInstance(context)

  runCatching { wallpaperManager.fastDrawable }.getOrNull()?.let { drawable ->
    val stripped = drawable.toStrip(targetWidth, targetHeight)
    if (stripped != null) return stripped
  }

  return wallpaperManager.fileStrip(targetWidth, targetHeight)
}

private fun Drawable.toStrip(targetWidth: Int, targetHeight: Int): Bitmap? {
  val sourceWidth = intrinsicWidth
  val sourceHeight = intrinsicHeight
  if (sourceWidth <= 0 || sourceHeight <= 0) return null

  return try {
    val scale =
      maxOf(targetWidth.toFloat() / sourceWidth, targetHeight.toFloat() / sourceHeight)
    val scaledWidth = (sourceWidth * scale).toInt()
    val scaledHeight = (sourceHeight * scale).toInt()

    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.save()
    canvas.translate((targetWidth - scaledWidth) / 2f, (targetHeight - scaledHeight) / 2f)
    setBounds(0, 0, scaledWidth, scaledHeight)
    draw(canvas)
    canvas.restore()
    bitmap
  } catch (_: Exception) {
    null
  }
}

/** Decodes the wallpaper file itself and crops it to the strip size. */
private fun WallpaperManager.fileStrip(targetWidth: Int, targetHeight: Int): Bitmap? {
  val descriptor =
    runCatching { getWallpaperFile(WallpaperManager.FLAG_SYSTEM) }.getOrNull() ?: return null

  return try {
    descriptor.use { parcelDescriptor ->
      // Pass one: read the file bounds only.
      val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      FileInputStream(parcelDescriptor.fileDescriptor).use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
      }
      if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@use null

      // Pass two: a sample at least as wide as the strip needs.
      val options =
        BitmapFactory.Options().apply { inSampleSize = sampleForWidth(bounds.outWidth, targetWidth) }
      val decoded =
        FileInputStream(parcelDescriptor.fileDescriptor).use { stream ->
          BitmapFactory.decodeStream(stream, null, options)
        } ?: return@use null

      decoded.centerCropped(targetWidth, targetHeight)
    }
  } catch (_: Exception) {
    null
  }
}

/** Largest power-of-two sample that keeps the decoded width above the target. */
private fun sampleForWidth(sourceWidth: Int, targetWidth: Int): Int {
  var sample = 1
  while (sourceWidth / (sample * 2) >= targetWidth) sample *= 2
  return sample
}

private fun Bitmap.centerCropped(targetWidth: Int, targetHeight: Int): Bitmap? {
  val scale = maxOf(targetWidth.toFloat() / width, targetHeight.toFloat() / height)
  val scaledWidth = (width * scale).toInt()
  val scaledHeight = (height * scale).toInt()

  return try {
    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.save()
    canvas.translate((targetWidth - scaledWidth) / 2f, (targetHeight - scaledHeight) / 2f)
    canvas.scale(scale, scale)
    canvas.drawBitmap(this, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))
    canvas.restore()
    bitmap
  } catch (_: Exception) {
    null
  }
}

/** WallpaperColors of the current wallpaper, mapped onto our palette seeds. */
@RequiresApi(27)
private fun WallpaperManager.systemColors(): WallpaperPalette? {
  val colors =
    runCatching { getWallpaperColors(WallpaperManager.FLAG_SYSTEM) }.getOrNull() ?: return null
  val primary = colors.primaryColor.toArgb()
  val secondary = (colors.secondaryColor ?: colors.primaryColor).toArgb()
  val tertiary =
    if (Build.VERSION.SDK_INT >= 31) {
      (colors.tertiaryColor ?: colors.primaryColor).toArgb()
    } else {
      (colors.secondaryColor ?: colors.primaryColor).toArgb()
    }
  return WallpaperPalette(
    primary = Color(primary),
    secondary = Color(secondary),
    tertiary = Color(tertiary),
  )
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
