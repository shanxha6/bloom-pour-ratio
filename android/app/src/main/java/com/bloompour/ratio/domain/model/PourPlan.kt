package com.bloompour.ratio.domain.model

data class PourPlan(
    val dose: Float,
    val ratio: Float,
    val water: Int,
    val bloomWater: Int,
    val stages: List<PourStage>
) {
    val totalSeconds: Int get() = stages.sumOf { it.seconds }
}
