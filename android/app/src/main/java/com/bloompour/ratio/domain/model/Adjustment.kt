package com.bloompour.ratio.domain.model

enum class AdjustDirection {
    FINER,
    KEEP,
    COARSER
}

data class Adjustment(
    val direction: AdjustDirection,
    val grindSteps: Int,
    val newGrind: Int,
    val newRatio: Float
)
