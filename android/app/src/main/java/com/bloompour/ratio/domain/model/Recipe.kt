package com.bloompour.ratio.domain.model

data class Recipe(
    val name: String,
    val ratio: Float,
    val dose: Float,
    val grind: Int,
    val dialledIn: Boolean = false
)
