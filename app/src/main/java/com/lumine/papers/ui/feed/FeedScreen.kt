package com.lumine.papers.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.ui.components.StateMessage
import com.lumine.papers.ui.components.WallCard

private const val CARD_RATIO = 0.75f

@Composable
fun FeedScreen(
  vm: FeedViewModel,
  onOpen: (Wallpaper) -> Unit,
  onSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val searchFocus = remember { FocusRequester() }

  LaunchedEffect(vm.searchOpen) {
    if (vm.searchOpen) searchFocus.requestFocus()
  }

  Column(modifier.fillMaxSize()) {
    FeedTopBar(vm, searchFocus, onSettings)
    CategoryTabs(vm)

    Box(Modifier.weight(1f).fillMaxWidth()) {
      when {
        vm.loading ->
          CircularProgressIndicator(Modifier.align(Alignment.Center))

        vm.failed ->
          StateMessage(
            icon = Icons.Rounded.CloudOff,
            title = "Couldn't load wallpapers",
            subtitle = "Check your connection, then try again.",
            actionLabel = "Retry",
            onAction = vm::reload,
            modifier = Modifier.align(Alignment.Center),
          )

        vm.visible.isEmpty() ->
          StateMessage(
            icon = Icons.Rounded.SearchOff,
            title = "Nothing here yet",
            subtitle =
              if (vm.query.isBlank()) "No wallpapers in this category."
              else "No wallpaper matches \"${vm.query.trim()}\".",
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
            items(vm.visible, key = { it.id }) { wall ->
              WallCard(wall, ratio = CARD_RATIO, onClick = { onOpen(wall) })
            }
          }
      }
    }
  }
}

@Composable
private fun FeedTopBar(
  vm: FeedViewModel,
  searchFocus: FocusRequester,
  onSettings: () -> Unit,
) {
  val scheme = MaterialTheme.colorScheme
  Row(
    Modifier.fillMaxWidth()
      .statusBarsPadding()
      .padding(start = 4.dp, end = 4.dp, top = 4.dp)
      .height(58.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (vm.searchOpen) {
      SearchField(vm, searchFocus, Modifier.weight(1f).padding(start = 16.dp, end = 4.dp))
      IconButton(onClick = { vm.setSearch(false) }) {
        Icon(Icons.Rounded.Close, contentDescription = "Close search", tint = scheme.onBackground)
      }
    } else {
      Text(
        text = "LuminePapers",
        Modifier.weight(1f).padding(start = 20.dp, end = 8.dp),
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = scheme.onBackground,
      )
      IconButton(onClick = { vm.setSearch(true) }) {
        Icon(Icons.Rounded.Search, contentDescription = "Search", tint = scheme.onBackground)
      }
      IconButton(onClick = onSettings) {
        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = scheme.onBackground)
      }
    }
  }
}

@Composable
private fun SearchField(
  vm: FeedViewModel,
  searchFocus: FocusRequester,
  modifier: Modifier = Modifier,
) {
  val scheme = MaterialTheme.colorScheme
  BasicTextField(
    value = vm.query,
    onValueChange = vm::updateQuery,
    singleLine = true,
    textStyle = MaterialTheme.typography.bodyLarge.copy(color = scheme.onSurface),
    cursorBrush = SolidColor(scheme.primary),
    modifier =
      modifier
        .fillMaxWidth()
        .height(46.dp)
        .focusRequester(searchFocus)
        .clip(RoundedCornerShape(50))
        .background(scheme.surfaceVariant)
        .padding(horizontal = 16.dp),
    decorationBox = { inner ->
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
        if (vm.query.isEmpty()) {
          Text(
            text = "Search wallpapers",
            style = MaterialTheme.typography.bodyLarge,
            color = scheme.onSurfaceVariant.copy(alpha = 0.7f),
          )
        }
        inner()
      }
    },
  )
}

@Composable
private fun CategoryTabs(vm: FeedViewModel) {
  val scheme = MaterialTheme.colorScheme
  Row(
    Modifier.fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(26.dp),
  ) {
    TabLabel("All", active = vm.selected == null) { vm.selectCategory(null) }
    vm.categories.forEach { category ->
      TabLabel(category.name, active = vm.selected == category.id) {
        vm.selectCategory(category.id)
      }
    }
  }
}

@Composable
private fun TabLabel(label: String, active: Boolean, onClick: () -> Unit) {
  val scheme = MaterialTheme.colorScheme
  Column(
    Modifier.clickable(onClick = onClick).padding(top = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = label.uppercase(),
      style = MaterialTheme.typography.labelLarge,
      fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
      letterSpacing = 0.8.sp,
      color = if (active) scheme.primary else scheme.onSurfaceVariant.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(7.dp))
    Box(
      Modifier.width(22.dp)
        .height(3.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(if (active) scheme.primary else Color.Transparent)
    )
  }
}
