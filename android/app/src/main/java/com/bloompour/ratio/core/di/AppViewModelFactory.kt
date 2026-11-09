package com.bloompour.ratio.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bloompour.ratio.presentation.app.AppViewModel
import com.bloompour.ratio.presentation.game.GameViewModel
import com.bloompour.ratio.presentation.gameover.GameOverViewModel
import com.bloompour.ratio.presentation.log.TastingLogViewModel
import com.bloompour.ratio.presentation.menu.MenuViewModel
import com.bloompour.ratio.presentation.pourplan.PourPlanViewModel
import com.bloompour.ratio.presentation.ratio.RatioSetterViewModel
import com.bloompour.ratio.presentation.settings.BrewSettingsViewModel
import com.bloompour.ratio.presentation.splash.SplashViewModel

val AppViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
    initializer {
        AppViewModel(ServiceLocator.settingsRepository)
    }
    initializer {
        SplashViewModel()
    }
    initializer {
        MenuViewModel(
            recipeRepository = ServiceLocator.recipeRepository,
            settingsRepository = ServiceLocator.settingsRepository,
            brewSession = ServiceLocator.brewSession
        )
    }
    initializer {
        RatioSetterViewModel(
            brewSession = ServiceLocator.brewSession,
            settingsRepository = ServiceLocator.settingsRepository,
            recipeRepository = ServiceLocator.recipeRepository,
            computeBrewUseCase = ServiceLocator.computeBrewUseCase
        )
    }
    initializer {
        PourPlanViewModel(
            brewSession = ServiceLocator.brewSession,
            settingsRepository = ServiceLocator.settingsRepository,
            computeBrewUseCase = ServiceLocator.computeBrewUseCase,
            buildPourPlanUseCase = ServiceLocator.buildPourPlanUseCase
        )
    }
    initializer {
        GameViewModel(
            brewSession = ServiceLocator.brewSession,
            settingsRepository = ServiceLocator.settingsRepository
        )
    }
    initializer {
        GameOverViewModel(
            brewSession = ServiceLocator.brewSession,
            recipeRepository = ServiceLocator.recipeRepository,
            logRepository = ServiceLocator.logRepository,
            settingsRepository = ServiceLocator.settingsRepository,
            suggestAdjustmentUseCase = ServiceLocator.suggestAdjustmentUseCase
        )
    }
    initializer {
        TastingLogViewModel(ServiceLocator.logRepository)
    }
    initializer {
        BrewSettingsViewModel(ServiceLocator.settingsRepository)
    }
}
