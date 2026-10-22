package com.lumine.papers.ui.preview

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.data.WallpaperStore
import com.lumine.papers.theme.mix
import com.lumine.papers.ui.components.WallpaperImage
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PreviewScreen(
  walls: List<Wallpaper>,
  startId: String,
  onBack: () -> Unit,
  onApplied: () -> Unit,
  isFavorite: Boolean,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val start = walls.indexOfFirst { it.id == startId }.coerceAtLeast(0)
  val pagerState = rememberPagerState(initialPage = start, pageCount = { walls.size })

  var menuOpen by remember { mutableStateOf(false) }
  var homeMock by remember { mutableStateOf(false) }
  var applying by remember { mutableStateOf(false) }
  var saving by remember { mutableStateOf(false) }
  var pendingSave by remember { mutableStateOf<Wallpaper?>(null) }

  val current = walls.getOrNull(pagerState.currentPage)

  val permissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      val wall = pendingSave
      pendingSave = null
      if (granted && wall != null) {
        scope.launch { saving = true; saveToGallery(context, wall).let { toast(context, it, "saved") }; saving = false }
      }
    }

  fun startSave(wall: Wallpaper) {
    val needsPermission =
      Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
        context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
          PackageManager.PERMISSION_GRANTED
    if (needsPermission) {
      pendingSave = wall
      permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
      scope.launch {
        saving = true
        toast(context, saveToGallery(context, wall), "saved")
        saving = false
      }
    }
  }

  Box(modifier.fillMaxSize().background(Color.Black)) {
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
      WallpaperImage(walls[page], Modifier.fillMaxSize(), maxDim = 1600)
      if (homeMock) HomeMock(Modifier.fillMaxSize())
    }

    Row(
      Modifier.align(Alignment.TopCenter)
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      CircleButton(
        icon = Icons.AutoMirrored.Rounded.ArrowBack,
        contentDescription = "Back",
        background = Color.Black.copy(alpha = 0.35f),
        tint = Color.White,
        size = 42.dp,
        onClick = onBack,
      )

      Column(
        Modifier.weight(1f).padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = current?.name.orEmpty(),
          color = Color.White,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        current?.author?.takeIf { it.isNotBlank() }?.let { author ->
          Text(
            text = author,
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Box {
        CircleButton(
          icon = Icons.Rounded.MoreVert,
          contentDescription = "More options",
          background = Color.Black.copy(alpha = 0.35f),
          tint = Color.White,
          size = 42.dp,
          onClick = { menuOpen = true },
        )
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
          DropdownMenuItem(
            text = { Text("Home screen preview") },
            trailingIcon = {
              if (homeMock) Icon(Icons.Rounded.Check, contentDescription = null)
            },
            onClick = {
              homeMock = !homeMock
              menuOpen = false
            },
          )
        }
      }
    }

    Row(
      Modifier.align(Alignment.BottomCenter)
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp, vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val scheme = MaterialTheme.colorScheme

      if (saving) {
        Box(
          Modifier.size(56.dp).clip(CircleShape).background(scheme.primary),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = scheme.onPrimary,
            strokeWidth = 2.dp,
          )
        }
      } else {
        CircleButton(
          icon = Icons.Rounded.FileDownload,
          contentDescription = "Save to gallery",
          background = scheme.primary,
          tint = scheme.onPrimary,
          onClick = { current?.let { startSave(it) } },
        )
      }

      Spacer(Modifier.width(14.dp))

      SetAsButton(
        enabled = current != null && !applying,
        applying = applying,
        onClick = {
          val wall = current ?: return@SetAsButton
          scope.launch {
            applying = true
            val ok = applyWallpaper(context, wall)
            if (ok) onApplied()
            toast(context, ok, "applied")
            applying = false
          }
        },
        modifier = Modifier.weight(1f),
      )

      Spacer(Modifier.width(14.dp))

      CircleButton(
        icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
        contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
        background = scheme.surface.copy(alpha = 0.94f),
        tint = if (isFavorite) scheme.primary else scheme.onSurfaceVariant,
        onClick = { current?.let { onToggleFavorite(it.id) } },
      )
    }
  }
}

@Composable
private fun SetAsButton(enabled: Boolean, applying: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val scheme = MaterialTheme.colorScheme
  Box(
    modifier
      .height(56.dp)
      .clip(RoundedCornerShape(50))
      .background(
        Brush.horizontalGradient(
          listOf(scheme.primary, scheme.primary.mix(Color.White, 0.22f))
        )
      )
      .clickable(enabled = enabled, onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    if (applying) {
      CircularProgressIndicator(
        modifier = Modifier.size(22.dp),
        color = scheme.onPrimary,
        strokeWidth = 2.dp,
      )
    } else {
      Text(
        text = "SET AS",
        color = scheme.onPrimary,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.4.sp,
      )
    }
  }
}

@Composable
private fun CircleButton(
  icon: ImageVector,
  contentDescription: String,
  background: Color,
  tint: Color,
  size: Dp = 56.dp,
  enabled: Boolean = true,
  onClick: () -> Unit,
) {
  Box(
    Modifier.size(size)
      .clip(CircleShape)
      .background(background)
      .clickable(enabled = enabled, onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(24.dp))
  }
}

/** Fakes a home screen so the wallpaper can be judged behind a clock and icons. */
@Composable
private fun HomeMock(modifier: Modifier = Modifier) {
  Column(
    modifier.padding(top = 110.dp, bottom = 200.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text("09:41", color = Color.White, style = MaterialTheme.typography.displayLarge)
    Text("Tuesday, 22 October", color = Color(0xE6FFFFFF), style = MaterialTheme.typography.titleMedium)

    repeat(2) {
      Row(
        Modifier.padding(top = 26.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
      ) {
        repeat(4) {
          Box(
            Modifier.size(52.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0x33FFFFFF))
          )
        }
      }
    }
  }
}

private fun toast(context: Context, ok: Boolean, verb: String) {
  val message =
    if (ok) if (verb == "saved") "Saved to gallery" else "Wallpaper applied"
    else if (verb == "saved") "Couldn't save" else "Couldn't apply"
  Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

private suspend fun applyWallpaper(context: Context, wall: Wallpaper): Boolean =
  withContext(Dispatchers.IO) {
    val file = WallpaperStore.ensure(context, wall) ?: return@withContext false
    val bitmap = BitmapFactory.decodeFile(file.path) ?: return@withContext false
    try {
      WallpaperManager.getInstance(context).setBitmap(bitmap)
      true
    } catch (_: Exception) {
      false
    }
  }

/** Streams the wallpaper into MediaStore Pictures/LuminePapers. */
private suspend fun saveToGallery(context: Context, wall: Wallpaper): Boolean =
  withContext(Dispatchers.IO) {
    val file: File = WallpaperStore.ensure(context, wall) ?: return@withContext false
    try {
      val resolver = context.contentResolver
      val values =
        ContentValues().apply {
          put(MediaStore.Images.Media.DISPLAY_NAME, wall.file)
          put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LuminePapers")
            put(MediaStore.Images.Media.IS_PENDING, 1)
          }
        }
      val uri =
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
          ?: return@withContext false
      val written =
        resolver.openOutputStream(uri)?.use { out ->
          file.inputStream().use { input -> input.copyTo(out) }
        } ?: return@withContext false
      if (written == 0L) {
        resolver.delete(uri, null, null)
        return@withContext false
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
      }
      true
    } catch (_: Exception) {
      false
    }
  }
