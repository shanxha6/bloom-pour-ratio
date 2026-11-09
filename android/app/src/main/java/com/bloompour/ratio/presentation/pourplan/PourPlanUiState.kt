package com.bloompour.ratio.presentation.pourplan

import com.bloompour.ratio.domain.model.PourPlan

data class PourPlanUiState(
    val plan: PourPlan? = null,
    val ratioText: String = "",
    val doseText: String = "",
    val waterText: String = "",
    val timeText: String = "",
    val useOunces: Boolean = false
)
