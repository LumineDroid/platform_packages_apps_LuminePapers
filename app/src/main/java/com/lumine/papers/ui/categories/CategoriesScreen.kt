package com.lumine.papers.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumine.papers.data.WallCategory
import com.lumine.papers.ui.components.AppTopBar
import com.lumine.papers.ui.components.StateMessage
import com.lumine.papers.ui.components.WallpaperImage

/** Grid of category cards; tapping one filters the feed. */
@Composable
fun CategoriesScreen(
  categories: List<WallCategory>,
  loading: Boolean,
  onOpen: (WallCategory) -> Unit,
  onSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier.fillMaxSize()) {
    AppTopBar(title = "Categories", onSettings = onSettings)

    Box(Modifier.weight(1f).fillMaxWidth()) {
      when {
        loading ->
          CircularProgressIndicator(Modifier.align(Alignment.Center))

        categories.isEmpty() ->
          StateMessage(
            icon = Icons.Rounded.FolderOpen,
            title = "No categories yet",
            subtitle = "Wallpaper categories will appear once the list loads.",
            modifier = Modifier.align(Alignment.Center),
          )

        else ->
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            items(categories, key = { it.id }) { category ->
              CategoryCard(category, onClick = { onOpen(category) })
            }
          }
      }
    }
  }
}

@Composable
private fun CategoryCard(category: WallCategory, onClick: () -> Unit) {
  val scheme = MaterialTheme.colorScheme
  Box(
    Modifier.fillMaxWidth()
      .aspectRatio(0.75f)
      .clip(RoundedCornerShape(18.dp))
      .background(scheme.surfaceVariant)
      .clickable(onClick = onClick)
  ) {
    val cover = category.walls.firstOrNull()
    if (cover != null) WallpaperImage(cover, Modifier.fillMaxSize())

    Box(
      Modifier.align(Alignment.BottomCenter)
        .fillMaxWidth()
        .height(90.dp)
        .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xE606060A))))
    )

    Column(
      Modifier.align(Alignment.BottomStart).padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
    ) {
      Text(
        text = category.name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        maxLines = 1,
      )
      Spacer(Modifier.height(2.dp))
      Text(
        text = "${category.walls.size} wallpapers",
        style = MaterialTheme.typography.labelMedium,
        color = Color.White.copy(alpha = 0.8f),
      )
    }
  }
}
