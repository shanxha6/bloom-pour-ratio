package com.bloompour.ratio.presentation.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.repository.LogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TastingLogViewModel(private val logRepository: LogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TastingLogUiState())
    val uiState: StateFlow<TastingLogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            logRepository.entries.collect { entries ->
                _uiState.value = TastingLogUiState(
                    rows = entries.take(GameConfig.LOG_VISIBLE).map { entry ->
                        LogRow(
                            dateText = LocalDate.ofEpochDay(entry.epochDay).toString(),
                            recipeName = entry.recipeName,
                            ratioText = BrewFormat.ratioText(entry.ratio),
                            doseText = BrewFormat.doseText(entry.dose),
                            verdict = entry.verdict
                        )
                    },
                    totalCount = entries.size
                )
            }
        }
    }

    fun onClear() {
        logRepository.clear()
    }
}
