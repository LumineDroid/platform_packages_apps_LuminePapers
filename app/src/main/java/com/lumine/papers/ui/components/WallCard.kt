package com.lumine.papers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lumine.papers.data.Wallpaper

@Composable
fun WallCard(wall: Wallpaper, ratio: Float, onClick: () -> Unit) {
  Box(
    Modifier.fillMaxWidth().aspectRatio(ratio).clip(RoundedCornerShape(22.dp))
      .clickable(onClick = onClick)
  ) {
    WallpaperImage(wall, Modifier.fillMaxSize())

    Box(
      Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xD906060A))))
        .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
      Text(
        text = wall.name,
        color = Color.White,
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}
