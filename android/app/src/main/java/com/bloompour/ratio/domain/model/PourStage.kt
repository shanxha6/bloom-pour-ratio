package com.bloompour.ratio.domain.model

enum class StageKind {
    BLOOM,
    POUR,
    DRAWDOWN
}

data class PourStage(
    val index: Int,
    val kind: StageKind,
    val pourNumber: Int,
    val seconds: Int,
    val startSecond: Int,
    val startWater: Int,
    val targetWater: Int
) {
    val endSecond: Int get() = startSecond + seconds
}
