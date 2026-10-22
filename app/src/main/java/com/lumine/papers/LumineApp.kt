package com.lumine.papers

import android.app.Application
import com.lumine.papers.data.FavoritesStore

class LumineApp : Application() {
  override fun onCreate() {
    super.onCreate()
    FavoritesStore.bind(this)
  }
}
