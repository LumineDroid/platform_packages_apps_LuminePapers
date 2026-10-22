package com.lumine.papers.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Hearted wallpaper ids, persisted and mirrored into Compose state. */
object FavoritesStore {

  private const val FILE = "favorites"
  private const val KEY = "ids"

  private lateinit var prefs: SharedPreferences

  var ids: Set<String> by mutableStateOf(emptySet())
    private set

  fun bind(context: Context) {
    if (::prefs.isInitialized) return
    prefs = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    ids = prefs.getStringSet(KEY, emptySet())?.toSet() ?: emptySet()
  }

  fun contains(id: String): Boolean = id in ids

  fun toggle(id: String) {
    ids = if (id in ids) ids - id else ids + id
    prefs.edit().putStringSet(KEY, ids).apply()
  }
}
