package com.bloompour.ratio.domain.repository

import com.bloompour.ratio.domain.model.BrewSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<BrewSettings>
    fun update(settings: BrewSettings)
}
