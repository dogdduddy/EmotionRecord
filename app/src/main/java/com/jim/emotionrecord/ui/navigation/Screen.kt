package com.jim.emotionrecord.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Record : Screen("record")
    data object Graph : Screen("graph")
}
