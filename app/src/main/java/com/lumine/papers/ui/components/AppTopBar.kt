package com.lumine.papers.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Left-aligned title with optional actions and the settings gear on the right. */
@Composable
fun AppTopBar(
  title: String,
  onSettings: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
) {
  Row(
    Modifier.fillMaxWidth().statusBarsPadding().height(58.dp).padding(horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = title,
      Modifier.weight(1f).padding(start = 20.dp, end = 8.dp),
      textAlign = TextAlign.Start,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onBackground,
    )
    actions()
    if (onSettings != null) {
      IconButton(onClick = onSettings) {
        Icon(
          Icons.Rounded.Settings,
          contentDescription = "Settings",
          tint = MaterialTheme.colorScheme.onBackground,
        )
      }
    }
  }
}
