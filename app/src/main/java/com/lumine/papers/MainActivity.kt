package com.lumine.papers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumine.papers.theme.LumineTheme
import com.lumine.papers.theme.ThemeController
import com.lumine.papers.ui.Screen
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
      var screen by remember { mutableStateOf<Screen>(Screen.Feed) }

      fun open(wall: com.lumine.papers.data.Wallpaper) {
        themeController.select(wall)
        screen = Screen.Preview(wall)
      }

      LumineTheme(themeState) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          when (val current = screen) {
            Screen.Feed ->
              FeedScreen(
                vm = feedVm,
                onOpen = ::open,
                onShuffle = { feedVm.random()?.let(::open) },
                onSettings = { screen = Screen.Settings },
              )

            is Screen.Preview ->
              PreviewScreen(
                walls = feedVm.walls,
                startId = current.wall.id,
                onSelect = themeController::select,
                onBack = { screen = Screen.Feed },
              )

            Screen.Settings ->
              SettingsScreen(
                state = themeState,
                onToggleDark = themeController::toggleDarkMode,
                onBack = { screen = Screen.Feed },
              )
          }
        }
      }
    }
  }
}
