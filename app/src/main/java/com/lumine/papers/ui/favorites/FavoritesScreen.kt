package com.lumine.papers.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.ui.components.AppTopBar
import com.lumine.papers.ui.components.StateMessage
import com.lumine.papers.ui.components.WallCard

/** Hearted wallpapers. [walls] is already filtered to favorites. */
@Composable
fun FavoritesScreen(
  walls: List<Wallpaper>,
  loading: Boolean,
  onOpen: (Wallpaper) -> Unit,
  onSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier.fillMaxSize()) {
    AppTopBar(title = "Favorites", onSettings = onSettings)

    Box(Modifier.weight(1f).fillMaxWidth()) {
      when {
        loading ->
          CircularProgressIndicator(Modifier.align(Alignment.Center))

        walls.isEmpty() ->
          StateMessage(
            icon = Icons.Rounded.FavoriteBorder,
            title = "No favorites yet",
            subtitle = "Open a wallpaper and tap the heart to keep it here.",
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
            items(walls, key = { it.id }) { wall ->
              WallCard(wall, onClick = { onOpen(wall) })
            }
          }
      }
    }
  }
}
