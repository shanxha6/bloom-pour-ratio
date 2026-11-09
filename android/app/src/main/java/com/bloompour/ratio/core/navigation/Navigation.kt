package com.bloompour.ratio.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object RatioSetter : Screen
    data object PourPlan : Screen
    data object Game : Screen
    data class GameOver(val brewId: Long) : Screen
    data object TastingLog : Screen
    data object Settings : Screen
}
