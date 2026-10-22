package com.lumine.papers.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lumine.papers.data.Wallpaper

/** Plain rounded wallpaper tile, matching the reference grid. */
@Composable
fun WallCard(
  wall: Wallpaper,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  ratio: Float = 0.75f,
) {
  Box(
    modifier
      .fillMaxWidth()
      .aspectRatio(ratio)
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
  ) {
    WallpaperImage(wall, Modifier.fillMaxSize())
  }
}
