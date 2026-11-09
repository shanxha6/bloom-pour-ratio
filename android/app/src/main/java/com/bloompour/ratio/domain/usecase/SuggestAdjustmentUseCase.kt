package com.bloompour.ratio.domain.usecase

import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.domain.model.AdjustDirection
import com.bloompour.ratio.domain.model.Adjustment
import com.bloompour.ratio.domain.model.Verdict

class SuggestAdjustmentUseCase {

    operator fun invoke(verdict: Verdict, grind: Int, ratio: Float): Adjustment = when (verdict) {
        Verdict.SOUR -> Adjustment(
            direction = AdjustDirection.FINER,
            grindSteps = 2,
            newGrind = (grind - 2).coerceIn(GameConfig.GRIND_MIN, GameConfig.GRIND_MAX),
            newRatio = (ratio + GameConfig.RATIO_STEP).coerceIn(GameConfig.RATIO_MIN, GameConfig.RATIO_MAX)
        )

        Verdict.BALANCED -> Adjustment(
            direction = AdjustDirection.KEEP,
            grindSteps = 0,
            newGrind = grind.coerceIn(GameConfig.GRIND_MIN, GameConfig.GRIND_MAX),
            newRatio = ratio.coerceIn(GameConfig.RATIO_MIN, GameConfig.RATIO_MAX)
        )

        Verdict.BITTER -> Adjustment(
            direction = AdjustDirection.COARSER,
            grindSteps = 2,
            newGrind = (grind + 2).coerceIn(GameConfig.GRIND_MIN, GameConfig.GRIND_MAX),
            newRatio = (ratio - GameConfig.RATIO_STEP).coerceIn(GameConfig.RATIO_MIN, GameConfig.RATIO_MAX)
        )
    }
}
