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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumine.papers.theme.ThemeMode
import com.lumine.papers.theme.ThemeState

@Composable
fun SettingsScreen(
  state: ThemeState,
  onSelectMode: (ThemeMode) -> Unit,
  onBack: () -> Unit,
) {
  val scheme = MaterialTheme.colorScheme
  Column(
    Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
      .padding(bottom = 32.dp)
  ) {
    Row(
      Modifier.fillMaxWidth().padding(start = 4.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
      }
      Text(
        text = "Settings",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = scheme.onBackground,
      )
    }

    Text(
      text = "Appearance",
      Modifier.padding(start = 20.dp, top = 18.dp, bottom = 8.dp),
      style = MaterialTheme.typography.labelLarge,
      fontWeight = FontWeight.SemiBold,
      color = scheme.primary,
    )

    Column(
      Modifier.padding(horizontal = 20.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(scheme.surfaceVariant)
    ) {
      ModeRow(ThemeMode.Light, "Light", "Always bright surfaces", state.mode, onSelectMode)
      HorizontalDivider(color = scheme.onSurfaceVariant.copy(alpha = 0.12f))
      ModeRow(ThemeMode.Dark, "Dark", "Always dimmed surfaces", state.mode, onSelectMode)
      HorizontalDivider(color = scheme.onSurfaceVariant.copy(alpha = 0.12f))
      ModeRow(
        ThemeMode.System,
        "Follow system",
        "Match your device's dark mode",
        state.mode,
        onSelectMode,
      )
    }
  }
}

@Composable
private fun ModeRow(
  mode: ThemeMode,
  title: String,
  subtitle: String,
  selectedMode: ThemeMode,
  onSelect: (ThemeMode) -> Unit,
) {
  val scheme = MaterialTheme.colorScheme
  val isSelected = selectedMode == mode
  Row(
    Modifier.fillMaxWidth()
      .clickable { onSelect(mode) }
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = scheme.onSurface,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = scheme.onSurfaceVariant,
      )
    }
    RadioButton(
      selected = isSelected,
      onClick = { onSelect(mode) },
      colors =
        RadioButtonDefaults.colors(
          selectedColor = scheme.primary,
          unselectedColor = scheme.onSurfaceVariant.copy(alpha = 0.5f),
        ),
    )
  }
}
