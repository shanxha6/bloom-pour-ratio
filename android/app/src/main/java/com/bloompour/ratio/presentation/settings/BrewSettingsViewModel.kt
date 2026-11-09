package com.bloompour.ratio.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrewSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrewSettingsUiState())
    val uiState: StateFlow<BrewSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.value = BrewSettingsUiState(
                    useOunces = settings.useOunces,
                    defaultRatioText = BrewFormat.ratioText(settings.defaultRatio),
                    bloomMultiplierText = BrewFormat.multiplierText(settings.bloomMultiplier),
                    stageAlerts = settings.stageAlerts,
                    lightTheme = settings.lightTheme,
                    canLowerRatio = settings.defaultRatio > GameConfig.RATIO_MIN,
                    canRaiseRatio = settings.defaultRatio < GameConfig.RATIO_MAX,
                    canLowerBloom = settings.bloomMultiplier > GameConfig.BLOOM_MULTIPLIER_MIN,
                    canRaiseBloom = settings.bloomMultiplier < GameConfig.BLOOM_MULTIPLIER_MAX
                )
            }
        }
    }

    fun onUnitsChange(useOunces: Boolean) {
        settingsRepository.update(settingsRepository.settings.value.copy(useOunces = useOunces))
    }

    fun onRatioDecrease() {
        val current = settingsRepository.settings.value
        settingsRepository.update(
            current.copy(
                defaultRatio = (current.defaultRatio - GameConfig.RATIO_STEP)
                    .coerceIn(GameConfig.RATIO_MIN, GameConfig.RATIO_MAX)
            )
        )
    }

    fun onRatioIncrease() {
        val current = settingsRepository.settings.value
        settingsRepository.update(
            current.copy(
                defaultRatio = (current.defaultRatio + GameConfig.RATIO_STEP)
                    .coerceIn(GameConfig.RATIO_MIN, GameConfig.RATIO_MAX)
            )
        )
    }

    fun onBloomDecrease() {
        val current = settingsRepository.settings.value
        settingsRepository.update(
            current.copy(
                bloomMultiplier = (current.bloomMultiplier - GameConfig.BLOOM_MULTIPLIER_STEP)
                    .coerceIn(GameConfig.BLOOM_MULTIPLIER_MIN, GameConfig.BLOOM_MULTIPLIER_MAX)
            )
        )
    }

    fun onBloomIncrease() {
        val current = settingsRepository.settings.value
        settingsRepository.update(
            current.copy(
                bloomMultiplier = (current.bloomMultiplier + GameConfig.BLOOM_MULTIPLIER_STEP)
                    .coerceIn(GameConfig.BLOOM_MULTIPLIER_MIN, GameConfig.BLOOM_MULTIPLIER_MAX)
            )
        )
    }

    fun onStageAlertsChange(enabled: Boolean) {
        settingsRepository.update(settingsRepository.settings.value.copy(stageAlerts = enabled))
    }

    fun onLightThemeChange(enabled: Boolean) {
        settingsRepository.update(settingsRepository.settings.value.copy(lightTheme = enabled))
    }
}
