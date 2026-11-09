package com.bloompour.ratio.domain.usecase

import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.domain.model.PourPlan
import com.bloompour.ratio.domain.model.PourStage
import com.bloompour.ratio.domain.model.StageKind

class BuildPourPlanUseCase {

    operator fun invoke(math: BrewMath, ratio: Float): PourPlan {
        val stages = mutableListOf<PourStage>()
        var second = 0
        var cumulative = 0

        stages += PourStage(
            index = 0,
            kind = StageKind.BLOOM,
            pourNumber = 1,
            seconds = GameConfig.BLOOM_SECONDS,
            startSecond = second,
            startWater = cumulative,
            targetWater = math.bloomWater
        )
        second += GameConfig.BLOOM_SECONDS
        cumulative = math.bloomWater

        val pourSeconds = GameConfig.POUR_SECONDS + GameConfig.SETTLE_SECONDS
        for (pour in 1..math.pourCount) {
            val isLast = pour == math.pourCount
            val target = if (isLast) math.water else (cumulative + math.perPour).coerceAtMost(math.water)
            stages += PourStage(
                index = stages.size,
                kind = StageKind.POUR,
                pourNumber = pour + 1,
                seconds = pourSeconds,
                startSecond = second,
                startWater = cumulative,
                targetWater = target
            )
            second += pourSeconds
            cumulative = target
        }

        stages += PourStage(
            index = stages.size,
            kind = StageKind.DRAWDOWN,
            pourNumber = math.pourCount + 1,
            seconds = GameConfig.DRAWDOWN_SECONDS,
            startSecond = second,
            startWater = cumulative,
            targetWater = math.water
        )

        return PourPlan(
            dose = math.dose,
            ratio = ratio,
            water = math.water,
            bloomWater = math.bloomWater,
            stages = stages
        )
    }
}
