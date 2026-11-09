package com.bloompour.ratio.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.data.sample.SampleData
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.model.BrewLogEntry
import com.bloompour.ratio.domain.model.Recipe
import com.bloompour.ratio.domain.model.Verdict
import com.bloompour.ratio.domain.repository.LogRepository
import com.bloompour.ratio.domain.repository.RecipeRepository
import com.bloompour.ratio.domain.repository.SettingsRepository
import com.bloompour.ratio.domain.session.BrewSession
import com.bloompour.ratio.domain.usecase.SuggestAdjustmentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class GameOverViewModel(
    private val brewSession: BrewSession,
    private val recipeRepository: RecipeRepository,
    private val logRepository: LogRepository,
    private val settingsRepository: SettingsRepository,
    private val suggestAdjustmentUseCase: SuggestAdjustmentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameOverUiState())
    val uiState: StateFlow<GameOverUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                brewSession.draft,
                settingsRepository.settings
            ) { draft, settings ->
                val plan = draft.plan
                GameOverUiState(
                    doseText = BrewFormat.doseText(draft.dose),
                    ratioText = BrewFormat.ratioText(draft.ratio),
                    timeText = if (plan == null) {
                        BrewFormat.timeText(0)
                    } else {
                        BrewFormat.timeText(plan.totalSeconds)
                    },
                    waterText = BrewFormat.weightText(draft.water, settings.useOunces),
                    useOunces = settings.useOunces
                )
            }.collect { state ->
                val current = _uiState.value
                _uiState.value = state.copy(
                    verdict = current.verdict,
                    direction = current.direction,
                    grindSteps = current.grindSteps,
                    newGrindText = current.newGrindText,
                    newRatioText = current.newRatioText
                )
            }
        }
    }

    fun onVerdict(verdict: Verdict) {
        if (_uiState.value.verdict != null) return
        val draft = brewSession.draft.value
        val adjustment = suggestAdjustmentUseCase(verdict, draft.grind, draft.ratio)
        val name = draft.recipeName.ifBlank { SampleData.DEFAULT_RECIPE_NAME }

        logRepository.add(
            BrewLogEntry(
                epochDay = LocalDate.now().toEpochDay(),
                recipeName = name,
                ratio = draft.ratio,
                dose = draft.dose,
                verdict = verdict
            )
        )
        recipeRepository.registerBrew(draft.ratio, draft.dose)
        recipeRepository.saveRecipe(
            Recipe(
                name = name,
                ratio = draft.ratio,
                dose = draft.dose,
                grind = draft.grind,
                dialledIn = verdict == Verdict.BALANCED
            )
        )

        _uiState.value = _uiState.value.copy(
            verdict = verdict,
            direction = adjustment.direction,
            grindSteps = adjustment.grindSteps,
            newGrindText = adjustment.newGrind.toString(),
            newRatioText = BrewFormat.ratioText(adjustment.newRatio)
        )
    }

    fun onBrewAgain() {
        val state = _uiState.value
        val draft = brewSession.draft.value
        if (state.verdict != null) {
            val adjustment = suggestAdjustmentUseCase(state.verdict, draft.grind, draft.ratio)
            brewSession.setGrind(adjustment.newGrind)
            brewSession.setRatio(adjustment.newRatio)
        }
        _uiState.value = _uiState.value.copy(
            verdict = null,
            grindSteps = 0,
            newGrindText = "",
            newRatioText = ""
        )
    }

    fun onLeave() {
        _uiState.value = _uiState.value.copy(
            verdict = null,
            grindSteps = 0,
            newGrindText = "",
            newRatioText = ""
        )
    }
}
