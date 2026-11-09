package com.bloompour.ratio.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val FRAME_MS = 16L
    const val STAGE_PULSE_MS = 300
    const val HAPTIC_GAP_MS = 90L

    const val BLOOM_SECONDS = 45
    const val POUR_SECONDS = 30
    const val SETTLE_SECONDS = 15
    const val DRAWDOWN_SECONDS = 45

    const val RATIO_MIN = 12.0f
    const val RATIO_MAX = 20.0f
    const val RATIO_STEP = 0.5f

    const val DOSE_MIN = 8.0f
    const val DOSE_MAX = 60.0f
    const val DOSE_STEP = 1.0f

    const val WATER_MIN = 120.0f
    const val WATER_MAX = 900.0f
    const val WATER_STEP = 10.0f

    const val BLOOM_MULTIPLIER_MIN = 2.0f
    const val BLOOM_MULTIPLIER_MAX = 3.0f
    const val BLOOM_MULTIPLIER_STEP = 0.5f

    const val GRIND_MIN = 1
    const val GRIND_MAX = 20
    const val GRIND_DEFAULT = 11

    const val LOG_CAPACITY = 20
    const val RECIPE_CAPACITY = 8
    const val LOG_VISIBLE = 5
    const val RECIPE_VISIBLE = 2

    const val GRAMS_PER_OUNCE = 28.3495f
}
