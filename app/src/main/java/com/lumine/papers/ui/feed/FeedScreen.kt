package com.lumine.papers.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumine.papers.data.WallCategory
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.ui.components.WallCard

private const val CARD_RATIO = 0.62f

@Composable
fun FeedScreen(
  vm: FeedViewModel,
  onOpen: (Wallpaper) -> Unit,
  onShuffle: () -> Unit,
  onSettings: () -> Unit,
) {
  if (vm.loading) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    return
  }

  Column(Modifier.fillMaxSize().statusBarsPadding()) {
    FeedHeader(vm, onShuffle, onSettings)

    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 150.dp),
      modifier = Modifier.fillMaxSize().navigationBarsPadding(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(vm.visible, key = { it.id }) { wall ->
        WallCard(wall, ratio = CARD_RATIO, onClick = { onOpen(wall) })
      }
    }
  }
}

@Composable
private fun FeedHeader(vm: FeedViewModel, onShuffle: () -> Unit, onSettings: () -> Unit) {
  Row(
    Modifier.fillMaxWidth().padding(start = 20.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = "LuminePapers",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.weight(1f),
    )
    IconButton(onClick = onShuffle) {
      Icon(Icons.Rounded.Shuffle, contentDescription = "Shuffle", tint = MaterialTheme.colorScheme.primary)
    }
    IconButton(onClick = onSettings) {
      Icon(Icons.Rounded.Settings, contentDescription = "Settings")
    }
  }

  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
  ) {
    items(vm.categories, key = { it.id }) { category ->
      CategoryChip(category, selected = vm.selected == category.id) { vm.toggleCategory(category.id) }
    }
  }
}

@Composable
private fun CategoryChip(category: WallCategory, selected: Boolean, onClick: () -> Unit) {
  val scheme = MaterialTheme.colorScheme
  Box(
    Modifier.clip(RoundedCornerShape(50))
      .background(if (selected) scheme.primary else scheme.surfaceVariant)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Text(
      text = category.name,
      style = MaterialTheme.typography.labelLarge,
      color = if (selected) scheme.onPrimary else scheme.onSurfaceVariant,
    )
  }
}
