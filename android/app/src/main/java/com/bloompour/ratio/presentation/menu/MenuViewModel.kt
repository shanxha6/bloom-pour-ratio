package com.bloompour.ratio.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.model.Recipe
import com.bloompour.ratio.domain.repository.RecipeRepository
import com.bloompour.ratio.domain.repository.SettingsRepository
import com.bloompour.ratio.domain.session.BrewSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val recipeRepository: RecipeRepository,
    private val settingsRepository: SettingsRepository,
    private val brewSession: BrewSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                recipeRepository.recipes,
                recipeRepository.brewCount
            ) { recipes, brewCount ->
                val favourite = recipeRepository.mostUsedRatio()
                MenuUiState(
                    recipes = recipes.take(GameConfig.RECIPE_VISIBLE),
                    selectedRecipe = _uiState.value.selectedRecipe,
                    brewCount = brewCount,
                    favouriteRatioText = BrewFormat.ratioText(favourite),
                    hasFavouriteRatio = favourite > 0f
                )
            }.collect { state ->
                val selected = if (state.recipes.any { it.name == state.selectedRecipe }) {
                    state.selectedRecipe
                } else {
                    state.recipes.firstOrNull()?.name.orEmpty()
                }
                _uiState.value = state.copy(selectedRecipe = selected)
            }
        }
    }

    fun onSelectRecipe(recipe: Recipe) {
        _uiState.update { it.copy(selectedRecipe = recipe.name) }
    }

    fun onStartBrew() {
        val state = _uiState.value
        val recipe = state.recipes.firstOrNull { it.name == state.selectedRecipe }
        val settings = settingsRepository.settings.value
        if (recipe != null) {
            brewSession.start(
                name = recipe.name,
                ratio = recipe.ratio,
                dose = recipe.dose,
                grind = recipe.grind,
                water = recipe.dose * recipe.ratio
            )
        } else {
            val ratio = settings.defaultRatio
            val dose = recipeRepository.lastDose()
            brewSession.start(
                name = "",
                ratio = ratio,
                dose = dose,
                grind = GameConfig.GRIND_DEFAULT,
                water = dose * ratio
            )
        }
    }
}
