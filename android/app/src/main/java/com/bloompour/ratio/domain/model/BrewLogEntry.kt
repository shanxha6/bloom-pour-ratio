package com.bloompour.ratio.domain.model

data class BrewLogEntry(
    val epochDay: Long,
    val recipeName: String,
    val ratio: Float,
    val dose: Float,
    val verdict: Verdict
)
