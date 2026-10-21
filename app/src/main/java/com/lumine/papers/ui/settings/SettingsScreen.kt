package com.lumine.papers.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumine.papers.theme.ThemeState

@Composable
fun SettingsScreen(state: ThemeState, onToggleDark: () -> Unit, onBack: () -> Unit) {
  Column(
    Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
      .padding(bottom = 32.dp)
  ) {
    Row(
      Modifier.fillMaxWidth().padding(start = 4.dp, end = 20.dp, top = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
      Text(
        text = "Settings",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
      )
    }

    Row(
      Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .clickable(onClick = onToggleDark)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(Modifier.weight(1f)) {
        Text("Dark theme", style = MaterialTheme.typography.titleMedium)
        Text(
          text = if (state.darkMode) "Following the wallpaper's darker tones" else "Following its lighter tones",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Switch(checked = state.darkMode, onCheckedChange = { onToggleDark() })
    }
  }
}
