package com.bloompour.ratio.presentation.settings

data class BrewSettingsUiState(
    val useOunces: Boolean = false,
    val defaultRatioText: String = "16",
    val bloomMultiplierText: String = "2.0",
    val stageAlerts: Boolean = true,
    val lightTheme: Boolean = false,
    val canLowerRatio: Boolean = true,
    val canRaiseRatio: Boolean = true,
    val canLowerBloom: Boolean = false,
    val canRaiseBloom: Boolean = true
)
