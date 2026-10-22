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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Heap budget for decoded wallpapers; count-based limits blow up on full-res pages. */
private const val CACHE_BYTE_LIMIT = 48L * 1024 * 1024

private val cache = LinkedHashMap<String, ImageBitmap>(8, 0.75f, true)
private var cacheBytes = 0L

private fun ImageBitmap.byteSize(): Long = width.toLong() * height * 4L

private fun cachedBitmap(key: String): ImageBitmap? = synchronized(cache) { cache[key] }

private fun storeBitmap(key: String, bitmap: ImageBitmap) {
  synchronized(cache) {
    cache[key]?.let { cacheBytes -= it.byteSize() }
    cache[key] = bitmap
    cacheBytes += bitmap.byteSize()

    val iterator = cache.entries.iterator()
    while (cacheBytes > CACHE_BYTE_LIMIT && iterator.hasNext()) {
      val eldest = iterator.next()
      cacheBytes -= eldest.value.byteSize()
      iterator.remove()
    }
  }
}

/** Decodes a wallpaper file down to [maxDim], caching by file and size. */
fun decodeWallpaperFile(file: File, maxDim: Int): ImageBitmap? {
  val key = "${file.path}@$maxDim"
  cachedBitmap(key)?.let { return it }

  val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
  BitmapFactory.decodeFile(file.path, bounds)

  var sample = 1
  val longest = maxOf(bounds.outWidth, bounds.outHeight)
  while (longest / (sample * 2) >= maxDim) sample *= 2

  val opts = BitmapFactory.Options().apply { inSampleSize = sample }
  return BitmapFactory.decodeFile(file.path, opts)
    ?.asImageBitmap()
    ?.also { storeBitmap(key, it) }
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
      val source =
        if (file.exists()) file else WallpaperStore.ensure(context, wall) ?: return@produceState
      value = withContext(Dispatchers.IO) { decodeWallpaperFile(source, maxDim) }
    }

  if (bitmap == null) Box(modifier.background(Color(0xFF16161D)))
  else Image(bitmap!!, null, modifier, contentScale = contentScale)
}
