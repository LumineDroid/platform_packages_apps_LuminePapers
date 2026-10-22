package com.lumine.papers

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumine.papers.data.FavoritesStore
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.data.WallpaperRepository
import com.lumine.papers.theme.LumineTheme
import com.lumine.papers.theme.ThemeController
import com.lumine.papers.theme.isDark
import com.lumine.papers.ui.Screen
import com.lumine.papers.ui.categories.CategoriesScreen
import com.lumine.papers.ui.components.LumineBottomBar
import com.lumine.papers.ui.favorites.FavoritesScreen
import com.lumine.papers.ui.feed.FeedScreen
import com.lumine.papers.ui.feed.FeedViewModel
import com.lumine.papers.ui.preview.PreviewScreen
import com.lumine.papers.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeController: ThemeController = viewModel()
      val feedVm: FeedViewModel = viewModel()
      val themeState = themeController.state

      var tab by rememberSaveable { mutableStateOf(Screen.Feed) }
      var showSettings by rememberSaveable { mutableStateOf(false) }
      var previewId by rememberSaveable { mutableStateOf("") }

      val previewWall =
        if (previewId.isEmpty()) null else WallpaperRepository.byId(feedVm.walls, previewId)

      fun open(wall: Wallpaper) {
        previewId = wall.id
      }

      // Back closes the top overlay before it leaves the tab.
      BackHandler(enabled = previewWall != null || showSettings) {
        when {
          previewWall != null -> previewId = ""
          else -> showSettings = false
        }
      }

      // Keep system bar icons readable against whatever the theme is showing.
      val view = LocalView.current
      if (!view.isInEditMode) {
        val systemDark = isSystemInDarkTheme()
        SideEffect {
          val window = (view.context as? Activity)?.window ?: return@SideEffect
          val controller = WindowCompat.getInsetsController(window, view)
          val lightBars = !themeState.mode.isDark(systemDark) && previewWall == null
          controller.isAppearanceLightStatusBars = lightBars
          controller.isAppearanceLightNavigationBars = lightBars
        }
      }

      LumineTheme(themeState) {
        val showBottomBar = previewWall == null && !showSettings
        Scaffold(
          containerColor = MaterialTheme.colorScheme.background,
          contentWindowInsets = WindowInsets(0, 0, 0, 0),
          bottomBar = {
            if (showBottomBar) LumineBottomBar(current = tab) { tab = it }
          },
        ) { padding ->
          Surface(Modifier.padding(padding), color = MaterialTheme.colorScheme.background) {
            when {
              showSettings ->
                SettingsScreen(
                  state = themeState,
                  onSelectMode = themeController::setMode,
                  onBack = { showSettings = false },
                )

              previewWall != null ->
                PreviewScreen(
                  walls = feedVm.walls,
                  startId = previewWall.id,
                  onBack = { previewId = "" },
                  onApplied = themeController::refreshWallpaperColors,
                  isFavorite = FavoritesStore.contains(previewWall.id),
                  onToggleFavorite = FavoritesStore::toggle,
                )

              tab == Screen.Feed ->
                FeedScreen(
                  vm = feedVm,
                  onOpen = ::open,
                  onSettings = { showSettings = true },
                )

              tab == Screen.Categories ->
                CategoriesScreen(
                  categories = feedVm.categories,
                  loading = feedVm.loading,
                  onOpen = { category ->
                    feedVm.selectCategory(category.id)
                    tab = Screen.Feed
                  },
                  onSettings = { showSettings = true },
                )

              else ->
                FavoritesScreen(
                  walls = feedVm.walls.filter { FavoritesStore.contains(it.id) },
                  loading = feedVm.loading,
                  onOpen = ::open,
                  onSettings = { showSettings = true },
                )
            }
          }
        }
      }
    }
  }
}
