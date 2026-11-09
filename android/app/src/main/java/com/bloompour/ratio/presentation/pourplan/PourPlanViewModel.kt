package com.bloompour.ratio.presentation.pourplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.repository.SettingsRepository
import com.bloompour.ratio.domain.session.BrewSession
import com.bloompour.ratio.domain.usecase.BuildPourPlanUseCase
import com.bloompour.ratio.domain.usecase.ComputeBrewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PourPlanViewModel(
    private val brewSession: BrewSession,
    settingsRepository: SettingsRepository,
    private val computeBrewUseCase: ComputeBrewUseCase,
    private val buildPourPlanUseCase: BuildPourPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PourPlanUiState())
    val uiState: StateFlow<PourPlanUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                brewSession.draft,
                settingsRepository.settings
            ) { draft, settings ->
                val math = if (draft.byDose) {
                    computeBrewUseCase.fromDose(draft.dose, draft.ratio, settings.bloomMultiplier)
                } else {
                    computeBrewUseCase.fromWater(draft.water, draft.ratio, settings.bloomMultiplier)
                }
                val plan = buildPourPlanUseCase(math, draft.ratio)
                PourPlanUiState(
                    plan = plan,
                    ratioText = BrewFormat.ratioText(plan.ratio),
                    doseText = BrewFormat.doseText(plan.dose),
                    waterText = BrewFormat.weightText(plan.water.toFloat(), settings.useOunces),
                    timeText = BrewFormat.timeText(plan.totalSeconds),
                    useOunces = settings.useOunces
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onStartTimer() {
        val plan = _uiState.value.plan ?: return
        brewSession.setPlan(plan)
    }
}
