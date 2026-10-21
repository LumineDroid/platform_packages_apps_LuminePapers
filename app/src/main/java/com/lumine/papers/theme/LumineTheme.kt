package com.lumine.papers.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LumineTheme(state: ThemeState, content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = state.palette.scheme(state.darkMode), content = content)
}
