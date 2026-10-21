package com.lumine.papers.ui.preview

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.data.WallpaperStore
import com.lumine.papers.ui.components.WallpaperImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PreviewScreen(
  walls: List<Wallpaper>,
  startId: String,
  onSelect: (Wallpaper) -> Unit,
  onBack: () -> Unit,
) {
  val start = walls.indexOfFirst { it.id == startId }.coerceAtLeast(0)
  val pagerState = rememberPagerState(initialPage = start, pageCount = { walls.size })
  var homeMock by remember { mutableStateOf(false) }

  val current = walls.getOrNull(pagerState.currentPage)
  LaunchedEffect(current?.id) { current?.let(onSelect) }

  Box(Modifier.fillMaxSize().background(Color.Black)) {
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
      WallpaperImage(walls[page], Modifier.fillMaxSize(), maxDim = 1400)
      if (homeMock) HomeMock(Modifier.fillMaxSize())
    }

    Row(
      Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(onClick = onBack) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
      }
      Text(
        text = current?.name.orEmpty(),
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.weight(1f),
      )
      IconButton(onClick = { homeMock = !homeMock }) {
        Icon(
          Icons.Rounded.PhoneAndroid,
          contentDescription = "Toggle home screen preview",
          tint = if (homeMock) MaterialTheme.colorScheme.primary else Color.White,
        )
      }
    }

    Surface(
      Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
    ) {
      Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = if (homeMock) "Shown on your home screen" else "Tap the phone icon to preview",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.weight(1f).padding(start = 8.dp),
        )
        ApplyButton(current)
      }
    }
  }
}

@Composable
private fun ApplyButton(wall: Wallpaper?) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  Button(
    enabled = wall != null,
    onClick = {
      val target = wall ?: return@Button
      scope.launch { apply(context, target) }
    },
  ) {
    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
    Text("Apply", modifier = Modifier.padding(start = 6.dp))
  }
}

/** Fakes a home screen so the wallpaper can be judged behind a clock and icons. */
@Composable
private fun HomeMock(modifier: Modifier = Modifier) {
  Column(
    modifier.padding(top = 110.dp, bottom = 200.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("09:41", color = Color.White, style = MaterialTheme.typography.displayLarge)
    Text("Tuesday, 22 October", color = Color(0xE6FFFFFF), style = MaterialTheme.typography.titleMedium)

    repeat(2) { row ->
      Row(
        Modifier.padding(top = 26.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
      ) {
        repeat(4) {
          Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(Color(0x33FFFFFF)))
        }
      }
    }
  }
}

private suspend fun apply(context: Context, wall: Wallpaper) {
  val result =
    withContext(Dispatchers.IO) {
      val file = WallpaperStore.ensure(context, wall)
      val bitmap = file?.let { BitmapFactory.decodeFile(it.path) }
      bitmap?.let { WallpaperManager.getInstance(context).setBitmap(it) != null } ?: false
    }
  Toast.makeText(context, if (result) "Wallpaper applied" else "Could not apply", Toast.LENGTH_SHORT)
    .show()
}
