package com.bloompour.ratio.presentation.ratio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.data.sample.SampleData
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.model.Recipe
import com.bloompour.ratio.domain.repository.RecipeRepository
import com.bloompour.ratio.domain.repository.SettingsRepository
import com.bloompour.ratio.domain.session.BrewDraft
import com.bloompour.ratio.domain.session.BrewSession
import com.bloompour.ratio.domain.usecase.ComputeBrewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RatioSetterViewModel(
    private val brewSession: BrewSession,
    private val settingsRepository: SettingsRepository,
    private val recipeRepository: RecipeRepository,
    private val computeBrewUseCase: ComputeBrewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatioSetterUiState())
    val uiState: StateFlow<RatioSetterUiState> = _uiState.asStateFlow()

    private var savedFlag = false

    init {
        viewModelScope.launch {
            combine(
                brewSession.draft,
                settingsRepository.settings
            ) { draft, settings ->
                build(draft, settings.useOunces, settings.bloomMultiplier)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onRatioChange(ratio: Float) {
        savedFlag = false
        brewSession.setRatio(ratio.coerceIn(GameConfig.RATIO_MIN, GameConfig.RATIO_MAX))
    }

    fun onModeChange(byDose: Boolean) {
        brewSession.setByDose(byDose)
    }

    fun onDecrease() {
        savedFlag = false
        val state = _uiState.value
        if (state.byDose) {
            brewSession.setDose(
                (state.dose - GameConfig.DOSE_STEP).coerceIn(GameConfig.DOSE_MIN, GameConfig.DOSE_MAX)
            )
        } else {
            brewSession.setWater(
                (state.water - GameConfig.WATER_STEP).coerceIn(GameConfig.WATER_MIN, GameConfig.WATER_MAX)
            )
        }
    }

    fun onIncrease() {
        savedFlag = false
        val state = _uiState.value
        if (state.byDose) {
            brewSession.setDose(
                (state.dose + GameConfig.DOSE_STEP).coerceIn(GameConfig.DOSE_MIN, GameConfig.DOSE_MAX)
            )
        } else {
            brewSession.setWater(
                (state.water + GameConfig.WATER_STEP).coerceIn(GameConfig.WATER_MIN, GameConfig.WATER_MAX)
            )
        }
    }

    fun onSaveRecipe() {
        val draft = brewSession.draft.value
        val name = draft.recipeName.ifBlank { SampleData.DEFAULT_RECIPE_NAME }
        recipeRepository.saveRecipe(
            Recipe(
                name = name,
                ratio = draft.ratio,
                dose = draft.dose,
                grind = draft.grind,
                dialledIn = false
            )
        )
        brewSession.setName(name)
        savedFlag = true
        _uiState.value = _uiState.value.copy(saved = true, recipeName = name)
    }

    private fun build(draft: BrewDraft, useOunces: Boolean, bloomMultiplier: Float): RatioSetterUiState {
        val math = if (draft.byDose) {
            computeBrewUseCase.fromDose(draft.dose, draft.ratio, bloomMultiplier)
        } else {
            computeBrewUseCase.fromWater(draft.water, draft.ratio, bloomMultiplier)
        }
        val editable = if (draft.byDose) {
            BrewFormat.doseText(math.dose)
        } else {
            BrewFormat.weightText(math.water.toFloat(), useOunces)
        }
        val derived = if (draft.byDose) {
            BrewFormat.weightText(math.water.toFloat(), useOunces)
        } else {
            BrewFormat.doseText(math.dose)
        }
        val canDecrease = if (draft.byDose) {
            math.dose > GameConfig.DOSE_MIN
        } else {
            math.water > GameConfig.WATER_MIN
        }
        val canIncrease = if (draft.byDose) {
            math.dose < GameConfig.DOSE_MAX
        } else {
            math.water < GameConfig.WATER_MAX
        }
        return RatioSetterUiState(
            ratio = draft.ratio,
            ratioText = BrewFormat.ratioText(draft.ratio),
            byDose = draft.byDose,
            dose = math.dose,
            water = math.water.toFloat(),
            editableText = editable,
            derivedText = derived,
            useOunces = useOunces,
            recipeName = draft.recipeName,
            saved = savedFlag,
            canDecrease = canDecrease,
            canIncrease = canIncrease
        )
    }
}
