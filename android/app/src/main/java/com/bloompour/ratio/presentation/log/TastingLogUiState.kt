package com.bloompour.ratio.presentation.log

import com.bloompour.ratio.domain.model.Verdict

data class LogRow(
    val dateText: String,
    val recipeName: String,
    val ratioText: String,
    val doseText: String,
    val verdict: Verdict
)

data class TastingLogUiState(
    val rows: List<LogRow> = emptyList(),
    val totalCount: Int = 0
)
