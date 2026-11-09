package com.bloompour.ratio.domain.repository

import com.bloompour.ratio.domain.model.Recipe
import kotlinx.coroutines.flow.StateFlow

interface RecipeRepository {
    val recipes: StateFlow<List<Recipe>>
    val brewCount: StateFlow<Int>
    fun saveRecipe(recipe: Recipe)
    fun registerBrew(ratio: Float, dose: Float)
    fun lastRatio(): Float
    fun lastDose(): Float
    fun mostUsedRatio(): Float
}
