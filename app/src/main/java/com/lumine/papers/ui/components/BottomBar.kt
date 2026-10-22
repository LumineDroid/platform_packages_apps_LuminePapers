package com.lumine.papers.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.lumine.papers.ui.Screen

/** Three-tab bar: Home, Categories, Favorites. */
@Composable
fun LumineBottomBar(current: Screen, onSelect: (Screen) -> Unit) {
  Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
    Row(
      Modifier.fillMaxWidth().navigationBarsPadding().height(58.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      BarItem(
        tab = Screen.Feed,
        label = "Home",
        activeIcon = Icons.Rounded.Home,
        inactiveIcon = Icons.Outlined.Home,
        current = current,
        onSelect = onSelect,
      )
      BarItem(
        tab = Screen.Categories,
        label = "Categories",
        activeIcon = Icons.Rounded.Dashboard,
        inactiveIcon = Icons.Outlined.Dashboard,
        current = current,
        onSelect = onSelect,
      )
      BarItem(
        tab = Screen.Favorites,
        label = "Favorites",
        activeIcon = Icons.Rounded.Favorite,
        inactiveIcon = Icons.Outlined.FavoriteBorder,
        current = current,
        onSelect = onSelect,
      )
    }
  }
}

@Composable
private fun BarItem(
  tab: Screen,
  label: String,
  activeIcon: ImageVector,
  inactiveIcon: ImageVector,
  current: Screen,
  onSelect: (Screen) -> Unit,
) {
  val active = current == tab
  val tint =
    if (active) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)

  Box(
    Modifier.width(76.dp).height(58.dp).clickable(role = Role.Tab) { onSelect(tab) },
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = if (active) activeIcon else inactiveIcon,
      contentDescription = label,
      tint = tint,
    )
  }
}
