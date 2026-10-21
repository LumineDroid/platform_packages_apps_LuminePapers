package com.lumine.papers.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.data.WallpaperStore
import java.io.File
import java.util.Collections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val cache =
  Collections.synchronizedMap(
    object : LinkedHashMap<String, ImageBitmap>(48, 0.75f, true) {
      override fun removeEldestEntry(e: MutableMap.MutableEntry<String, ImageBitmap>) = size > 48
    }
  )

/** Decodes a wallpaper file down to [maxDim], caching by file and size. */
fun decodeWallpaperFile(file: File, maxDim: Int): ImageBitmap? {
  val key = "${file.path}@$maxDim"
  cache[key]?.let {
    cache[key] = it
    return it
  }
  val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
  BitmapFactory.decodeFile(file.path, bounds)

  var sample = 1
  val longest = maxOf(bounds.outWidth, bounds.outHeight)
  while (longest / (sample * 2) >= maxDim) sample *= 2

  val opts = BitmapFactory.Options().apply { inSampleSize = sample }
  return BitmapFactory.decodeFile(file.path, opts)
    ?.asImageBitmap()
    ?.also { cache[key] = it }
}

/** Renders a wallpaper, downloading it into the cache on first use. */
@Composable
fun WallpaperImage(
  wall: Wallpaper,
  modifier: Modifier = Modifier,
  maxDim: Int = 480,
  contentScale: ContentScale = ContentScale.Crop,
) {
  val context = LocalContext.current
  val bitmap by
    produceState<ImageBitmap?>(initialValue = null, key1 = wall.id, key2 = maxDim) {
      val file = WallpaperStore.fileFor(context, wall)
      if (file.exists()) {
        value = withContext(Dispatchers.IO) { decodeWallpaperFile(file, maxDim) }
      } else {
        val downloaded = WallpaperStore.ensure(context, wall) ?: return@produceState
        value = withContext(Dispatchers.IO) { decodeWallpaperFile(downloaded, maxDim) }
      }
    }

  if (bitmap == null) Box(modifier.background(Color(0xFF16161D)))
  else Image(bitmap!!, null, modifier, contentScale = contentScale)
}
