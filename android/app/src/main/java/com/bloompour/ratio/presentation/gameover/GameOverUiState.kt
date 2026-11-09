package com.bloompour.ratio.presentation.gameover

import com.bloompour.ratio.domain.model.AdjustDirection
import com.bloompour.ratio.domain.model.Verdict

data class GameOverUiState(
    val doseText: String = "",
    val ratioText: String = "",
    val timeText: String = "",
    val waterText: String = "",
    val verdict: Verdict? = null,
    val direction: AdjustDirection = AdjustDirection.KEEP,
    val grindSteps: Int = 0,
    val newGrindText: String = "",
    val newRatioText: String = "",
    val useOunces: Boolean = false
)
