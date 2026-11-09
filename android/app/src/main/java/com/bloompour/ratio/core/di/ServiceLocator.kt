package com.bloompour.ratio.core.di

import android.content.Context
import com.bloompour.ratio.data.local.BrewPreferences
import com.bloompour.ratio.data.repository.LogRepositoryImpl
import com.bloompour.ratio.data.repository.RecipeRepositoryImpl
import com.bloompour.ratio.data.repository.SettingsRepositoryImpl
import com.bloompour.ratio.domain.repository.LogRepository
import com.bloompour.ratio.domain.repository.RecipeRepository
import com.bloompour.ratio.domain.repository.SettingsRepository
import com.bloompour.ratio.domain.session.BrewSession
import com.bloompour.ratio.domain.usecase.BuildPourPlanUseCase
import com.bloompour.ratio.domain.usecase.ComputeBrewUseCase
import com.bloompour.ratio.domain.usecase.SuggestAdjustmentUseCase

object ServiceLocator {

    private var recipes: RecipeRepository? = null
    private var logs: LogRepository? = null
    private var preferencesSettings: SettingsRepository? = null

    val brewSession: BrewSession = BrewSession()
    val computeBrewUseCase: ComputeBrewUseCase = ComputeBrewUseCase()
    val buildPourPlanUseCase: BuildPourPlanUseCase = BuildPourPlanUseCase()
    val suggestAdjustmentUseCase: SuggestAdjustmentUseCase = SuggestAdjustmentUseCase()

    val recipeRepository: RecipeRepository
        get() = checkNotNull(recipes)

    val logRepository: LogRepository
        get() = checkNotNull(logs)

    val settingsRepository: SettingsRepository
        get() = checkNotNull(preferencesSettings)

    fun init(context: Context) {
        if (recipes != null) return
        val preferences = BrewPreferences(context)
        recipes = RecipeRepositoryImpl(preferences)
        logs = LogRepositoryImpl(preferences)
        preferencesSettings = SettingsRepositoryImpl(preferences)
    }
}
