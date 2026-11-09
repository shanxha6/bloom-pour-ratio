package com.bloompour.ratio.domain.model

import com.bloompour.ratio.core.config.GameConfig

data class BrewSettings(
    val useOunces: Boolean = false,
    val defaultRatio: Float = 16.0f,
    val bloomMultiplier: Float = 2.0f,
    val stageAlerts: Boolean = true,
    val lightTheme: Boolean = false
) {
    companion object {
        val DEFAULT = BrewSettings(
            useOunces = false,
            defaultRatio = 16.0f,
            bloomMultiplier = GameConfig.BLOOM_MULTIPLIER_MIN,
            stageAlerts = true,
            lightTheme = false
        )
    }
}
