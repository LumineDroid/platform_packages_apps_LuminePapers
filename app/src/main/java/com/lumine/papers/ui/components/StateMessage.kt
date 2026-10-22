package com.lumine.papers.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Centred icon + copy, used for loading failures and empty lists. */
@Composable
fun StateMessage(
  icon: ImageVector,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  actionLabel: String? = null,
  onAction: () -> Unit = {},
) {
  val scheme = MaterialTheme.colorScheme
  Column(modifier.padding(horizontal = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(icon, contentDescription = null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
    Spacer(Modifier.height(12.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center,
      color = scheme.onBackground,
    )
    Spacer(Modifier.height(4.dp))
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
      color = scheme.onSurfaceVariant,
    )
    if (actionLabel != null) {
      Spacer(Modifier.height(8.dp))
      TextButton(onClick = onAction) { Text(actionLabel) }
    }
  }
}
