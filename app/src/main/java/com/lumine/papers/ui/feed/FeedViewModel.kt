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

  var query by mutableStateOf("")
    private set

  var searchOpen by mutableStateOf(false)
    private set

  var loading by mutableStateOf(true)
    private set

  init {
    load()
  }

  val visible: List<Wallpaper>
    get() {
      val category = selected
      val needle = query.trim().lowercase()
      return walls.filter { wall ->
        (category == null || wall.category.lowercase() == category) &&
          (needle.isEmpty() ||
            wall.name.lowercase().contains(needle) ||
            wall.category.lowercase().contains(needle) ||
            wall.author.lowercase().contains(needle))
      }
    }

  /** True when the manifest could not be read at all. */
  val failed: Boolean
    get() = !loading && walls.isEmpty()

  fun selectCategory(id: String?) {
    selected = id
  }

  fun updateQuery(value: String) {
    query = value
  }

  fun setSearch(open: Boolean) {
    searchOpen = open
    if (!open) query = ""
  }

  fun reload() {
    loading = true
    load()
  }

  private fun load() {
    viewModelScope.launch {
      walls = WallpaperRepository.load(getApplication())
      categories = WallpaperRepository.categories(walls)
      loading = false
    }
  }
}
