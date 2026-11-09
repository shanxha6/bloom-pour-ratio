package com.bloompour.ratio.domain.usecase

import com.bloompour.ratio.core.config.GameConfig
import kotlin.math.roundToInt

data class BrewMath(
    val dose: Float,
    val water: Int,
    val bloomWater: Int,
    val remainingWater: Int,
    val pourCount: Int,
    val perPour: Int
)

class ComputeBrewUseCase {

    fun fromDose(dose: Float, ratio: Float, bloomMultiplier: Float): BrewMath {
        val clampedDose = dose.coerceIn(GameConfig.DOSE_MIN, GameConfig.DOSE_MAX)
        val water = (clampedDose * ratio).roundToInt()
            .coerceIn(GameConfig.WATER_MIN.toInt(), GameConfig.WATER_MAX.toInt())
        return build(clampedDose, water, bloomMultiplier)
    }

    fun fromWater(water: Float, ratio: Float, bloomMultiplier: Float): BrewMath {
        val clampedWater = water.coerceIn(GameConfig.WATER_MIN, GameConfig.WATER_MAX)
        val dose = ((clampedWater / ratio) * 10f).roundToInt() / 10f
        val clampedDose = dose.coerceIn(GameConfig.DOSE_MIN, GameConfig.DOSE_MAX)
        return build(clampedDose, clampedWater.roundToInt(), bloomMultiplier)
    }

    private fun build(dose: Float, water: Int, bloomMultiplier: Float): BrewMath {
        val rawBloom = dose * bloomMultiplier
        val bloom = ((rawBloom / 5f).roundToInt() * 5).coerceIn(10, water - 20)
        val remaining = water - bloom
        val pourCount = if (water <= 300) 2 else 3
        val perPour = ((remaining.toFloat() / pourCount / 5f).roundToInt() * 5).coerceAtLeast(5)
        return BrewMath(
            dose = dose,
            water = water,
            bloomWater = bloom,
            remainingWater = remaining,
            pourCount = pourCount,
            perPour = perPour
        )
    }
}
