package com.bloompour.ratio.presentation.game

import com.bloompour.ratio.domain.model.StageKind

data class GameUiState(
    val hasPlan: Boolean = false,
    val stageIndex: Int = 0,
    val stageCount: Int = 0,
    val stageKind: StageKind = StageKind.BLOOM,
    val stagePourNumber: Int = 1,
    val stageProgress: Float = 1f,
    val countdownText: String = "0:00",
    val targetText: String = "0",
    val poured: Float = 0f,
    val totalWater: Float = 0f,
    val running: Boolean = false,
    val finished: Boolean = false,
    val lastStage: Boolean = false,
    val stageAlerts: Boolean = true,
    val pulse: Boolean = false,
    val useOunces: Boolean = false
)

sealed interface GameEvent {
    data class StageChanged(val index: Int) : GameEvent
    data object Finished : GameEvent
}
