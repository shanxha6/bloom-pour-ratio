package com.bloompour.ratio.domain.session

import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.domain.model.PourPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BrewDraft(
    val recipeName: String = "",
    val ratio: Float = 16.0f,
    val dose: Float = 20.0f,
    val grind: Int = GameConfig.GRIND_DEFAULT,
    val byDose: Boolean = true,
    val water: Float = 320.0f,
    val plan: PourPlan? = null,
    val planToken: Long = 0L
)

class BrewSession {

    private val _draft = MutableStateFlow(BrewDraft())
    val draft: StateFlow<BrewDraft> = _draft.asStateFlow()

    fun start(name: String, ratio: Float, dose: Float, grind: Int, water: Float) {
        _draft.update {
            it.copy(
                recipeName = name,
                ratio = ratio,
                dose = dose,
                grind = grind,
                water = water,
                plan = null
            )
        }
    }

    fun setRatio(ratio: Float) {
        _draft.update { it.copy(ratio = ratio, plan = null) }
    }

    fun setDose(dose: Float) {
        _draft.update { it.copy(dose = dose, plan = null) }
    }

    fun setWater(water: Float) {
        _draft.update { it.copy(water = water, plan = null) }
    }

    fun setByDose(byDose: Boolean) {
        _draft.update { it.copy(byDose = byDose) }
    }

    fun setName(name: String) {
        _draft.update { it.copy(recipeName = name) }
    }

    fun setGrind(grind: Int) {
        _draft.update { it.copy(grind = grind) }
    }

    fun setPlan(plan: PourPlan) {
        _draft.update {
            it.copy(
                plan = plan,
                dose = plan.dose,
                ratio = plan.ratio,
                water = plan.water.toFloat(),
                planToken = it.planToken + 1L
            )
        }
    }
}
