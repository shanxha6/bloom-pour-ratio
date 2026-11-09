package com.bloompour.ratio.presentation.ratio

data class RatioSetterUiState(
    val ratio: Float = 16.0f,
    val ratioText: String = "16",
    val byDose: Boolean = true,
    val dose: Float = 20.0f,
    val water: Float = 320.0f,
    val editableText: String = "20",
    val derivedText: String = "320",
    val useOunces: Boolean = false,
    val recipeName: String = "",
    val saved: Boolean = false,
    val canDecrease: Boolean = true,
    val canIncrease: Boolean = true
)
