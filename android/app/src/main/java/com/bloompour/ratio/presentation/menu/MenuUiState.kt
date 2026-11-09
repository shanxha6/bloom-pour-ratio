package com.bloompour.ratio.presentation.menu

import com.bloompour.ratio.domain.model.Recipe

data class MenuUiState(
    val recipes: List<Recipe> = emptyList(),
    val selectedRecipe: String = "",
    val brewCount: Int = 0,
    val favouriteRatioText: String = "",
    val hasFavouriteRatio: Boolean = false
)
