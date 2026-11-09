package com.bloompour.ratio.data.repository

import com.bloompour.ratio.data.local.BrewPreferences
import com.bloompour.ratio.domain.model.BrewSettings
import com.bloompour.ratio.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(private val preferences: BrewPreferences) : SettingsRepository {

    private val _settings = MutableStateFlow(load())
    override val settings: StateFlow<BrewSettings> = _settings.asStateFlow()

    override fun update(settings: BrewSettings) {
        _settings.value = settings
        preferences.putBoolean(BrewPreferences.KEY_UNITS_OUNCES, settings.useOunces)
        preferences.putFloat(BrewPreferences.KEY_DEFAULT_RATIO, settings.defaultRatio)
        preferences.putFloat(BrewPreferences.KEY_BLOOM_MULTIPLIER, settings.bloomMultiplier)
        preferences.putBoolean(BrewPreferences.KEY_STAGE_ALERTS, settings.stageAlerts)
        preferences.putBoolean(BrewPreferences.KEY_LIGHT_THEME, settings.lightTheme)
    }

    private fun load(): BrewSettings {
        val defaults = BrewSettings.DEFAULT
        return BrewSettings(
            useOunces = preferences.getBoolean(BrewPreferences.KEY_UNITS_OUNCES, defaults.useOunces),
            defaultRatio = preferences.getFloat(BrewPreferences.KEY_DEFAULT_RATIO, defaults.defaultRatio),
            bloomMultiplier = preferences.getFloat(
                BrewPreferences.KEY_BLOOM_MULTIPLIER,
                defaults.bloomMultiplier
            ),
            stageAlerts = preferences.getBoolean(BrewPreferences.KEY_STAGE_ALERTS, defaults.stageAlerts),
            lightTheme = preferences.getBoolean(BrewPreferences.KEY_LIGHT_THEME, defaults.lightTheme)
        )
    }
}
