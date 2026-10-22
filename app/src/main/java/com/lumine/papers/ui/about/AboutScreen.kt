package com.lumine.papers.ui.about

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumine.papers.theme.ThemeMode
import com.lumine.papers.theme.ThemeState
import com.lumine.papers.theme.WallpaperPalette
import com.lumine.papers.theme.mix
import com.lumine.papers.theme.systemWallpaperSeed
import com.lumine.papers.theme.systemWallpaperStrip

private const val SOURCE_URL = "https://github.com/LumineDroid/platform_packages_apps_LuminePapers"
private const val COMMUNITY_URL = "https://github.com/LumineDroid"
private const val LICENSE_ASSET = "LICENSE-2.0.txt"

private const val DESCRIPTION =
  "LuminePapers is a collection of wallpapers to personalize your device. " +
    "Fully open-source, everyone is welcome to contribute and help the project grow."

/** About page: theme choice up top, hero card showing the live system wallpaper, links below. */
@Composable
fun AboutScreen(
  state: ThemeState,
  onSelectMode: (ThemeMode) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  var showLicenses by remember { mutableStateOf(false) }
  val versionLine = remember(context) { versionLine(context) }
  val wallpaper = remember(context) { systemWallpaperStrip(context) }
  // Live wallpapers hand over no bitmap: fall back to the colours they report.
  val wallpaperColors =
    if (wallpaper == null) remember(context) { systemWallpaperSeed(context) } else null

  Column(
    modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
      .padding(bottom = 32.dp)
  ) {
    Row(
      Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 10.dp, bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "About",
        Modifier.weight(1f),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
      )
      ThemeModeButton(state.mode, onSelectMode)
    }

    HeroCard(
      wallpaper = wallpaper,
      wallpaperColors = wallpaperColors,
      versionLine = versionLine,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
    )

    Text(
      text = DESCRIPTION,
      Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp),
      style = MaterialTheme.typography.bodySmall,
      lineHeight = 17.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Column(
      Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      AboutRow(
        icon = Icons.Rounded.Code,
        title = "Check for source code",
        subtitle = "Check for source code on GitHub",
      ) {
        openUrl(context, SOURCE_URL)
      }
      RowDivider()
      AboutRow(
        icon = Icons.Rounded.Groups,
        title = "Join LumineDroid",
        subtitle = "Join our community",
      ) {
        openUrl(context, COMMUNITY_URL)
      }
      RowDivider()
      AboutRow(
        icon = Icons.Rounded.Description,
        title = "Open Source License",
        subtitle = "View third-party open-source libraries and licenses",
      ) {
        showLicenses = true
      }
    }

    if (showLicenses) LicenseDialog(onDismiss = { showLicenses = false })
  }
}

/** Card filled with the wallpaper the launcher is currently showing. */
@Composable
private fun HeroCard(
  wallpaper: Bitmap?,
  wallpaperColors: WallpaperPalette?,
  versionLine: String,
  modifier: Modifier = Modifier,
) {
  val scheme = MaterialTheme.colorScheme
  val painted = wallpaper != null || wallpaperColors != null
  Box(
    modifier
      .fillMaxWidth()
      .aspectRatio(2.3f)
      .clip(RoundedCornerShape(24.dp))
      .background(scheme.surfaceVariant)
  ) {
    when {
      wallpaper != null -> {
        Image(
          bitmap = wallpaper.asImageBitmap(),
          contentDescription = null,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
        )
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)))
      }
      wallpaperColors != null -> {
        // No bitmap available (live wallpaper): wash the card in its reported colours.
        Box(
          Modifier.fillMaxSize().background(
            Brush.linearGradient(
              listOf(
                wallpaperColors.primary.mix(Color.Black, 0.38f),
                wallpaperColors.secondary.mix(Color.Black, 0.52f),
                wallpaperColors.tertiary.mix(Color.Black, 0.38f),
              )
            )
          )
        )
      }
    }

    val textColor =
      if (painted) Color.White else scheme.onSurface
    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "LuminePapers",
        color = textColor,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = versionLine,
        color = if (painted) Color.White.copy(alpha = 0.85f) else textColor.copy(alpha = 0.75f),
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

/** One button that cycles the theme: Light → Dark → Follow system → Light. */
@Composable
private fun ThemeModeButton(mode: ThemeMode, onSelect: (ThemeMode) -> Unit) {
  val scheme = MaterialTheme.colorScheme
  val icon =
    when (mode) {
      ThemeMode.Light -> Icons.Rounded.LightMode
      ThemeMode.Dark -> Icons.Rounded.DarkMode
      ThemeMode.System -> Icons.Rounded.BrightnessAuto
    }
  val label =
    when (mode) {
      ThemeMode.Light -> "Light mode"
      ThemeMode.Dark -> "Dark mode"
      ThemeMode.System -> "Follow system"
    }

  Box(
    Modifier.size(40.dp).clip(CircleShape).clickable(role = Role.Button) { onSelect(mode.next()) },
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = scheme.primary,
      modifier = Modifier.size(24.dp),
    )
  }
}

private fun ThemeMode.next(): ThemeMode =
  when (this) {
    ThemeMode.Light -> ThemeMode.Dark
    ThemeMode.Dark -> ThemeMode.System
    ThemeMode.System -> ThemeMode.Light
  }

@Composable
private fun AboutRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
) {
  val scheme = MaterialTheme.colorScheme
  Row(
    Modifier.fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      Modifier.size(40.dp).clip(CircleShape).background(scheme.primary.copy(alpha = 0.14f)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(icon, contentDescription = null, tint = scheme.primary, modifier = Modifier.size(20.dp))
    }
    Spacer(Modifier.width(14.dp))
    Column(Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = scheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = scheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun RowDivider() {
  HorizontalDivider(
    modifier = Modifier.padding(horizontal = 16.dp),
    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
  )
}

@Composable
private fun LicenseDialog(onDismiss: () -> Unit) {
  val context = LocalContext.current
  val text = remember { licensesText(context) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Open Source Licenses") },
    text = {
      Text(
        text = text,
        Modifier.verticalScroll(rememberScrollState()).padding(top = 4.dp),
        style = MaterialTheme.typography.bodySmall,
      )
    },
    confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
  )
}

private fun licensesText(context: Context): String {
  val header =
    buildString {
      appendLine("LuminePapers")
      appendLine("  Licensed under the Apache License, Version 2.0.")
      appendLine()
      appendLine("Third-party libraries")
      appendLine()
      appendLine("  Jetpack Compose .............. Apache License 2.0")
      appendLine("  Material 3 and Material Icons . Apache License 2.0")
      appendLine("  AndroidX Palette ............. Apache License 2.0")
      appendLine("  AndroidX Lifecycle, Activity . Apache License 2.0")
      appendLine("  kotlinx-coroutines .......... Apache License 2.0")
      appendLine("  org.json (Android platform) .. Apache License 2.0")
      appendLine()
      appendLine("-----------------------------------------------")
      appendLine()
    }

  val full =
    runCatching {
        context.assets.open(LICENSE_ASSET).bufferedReader().use { it.readText() }
      }
      .getOrDefault("License text is missing from this build.")

  return header + full
}

private fun versionLine(context: Context): String {
  val debuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
  val name =
    runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
      .getOrNull()
      // Soong/AOSP builds ship without a versionName in the manifest.
      ?: "1.0.0"
  return "v$name (${if (debuggable) "debug" else "released"})"
}

private fun openUrl(context: Context, url: String) {
  try {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    if (context !is android.app.Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  } catch (_: Exception) {
    Toast.makeText(context, "Couldn't open the link", Toast.LENGTH_SHORT).show()
  }
}
