package com.lumine.papers.ui.feed

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lumine.papers.data.WallCategory
import com.lumine.papers.data.Wallpaper
import com.lumine.papers.data.WallpaperRepository
import kotlinx.coroutines.launch

class FeedViewModel(app: Application) : AndroidViewModel(app) {

  var walls by mutableStateOf<List<Wallpaper>>(emptyList())
    private set

  var categories by mutableStateOf<List<WallCategory>>(emptyList())
    private set

  var selected by mutableStateOf<String?>(null)
    private set

  var loading by mutableStateOf(true)
    private set

  init {
    viewModelScope.launch {
      walls = WallpaperRepository.load(app)
      categories = WallpaperRepository.categories(walls)
      loading = false
    }
  }

  val visible: List<Wallpaper>
    get() = selected?.let { id -> walls.filter { it.category.lowercase() == id } } ?: walls

  fun toggleCategory(id: String) {
    selected = if (selected == id) null else id
  }

  fun random(): Wallpaper? = visible.randomOrNull()
}
