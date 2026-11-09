package com.bloompour.ratio.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloompour.ratio.core.di.AppViewModelFactory
import com.bloompour.ratio.core.navigation.Screen
import com.bloompour.ratio.core.theme.AppTheme
import com.bloompour.ratio.presentation.game.GameScreen
import com.bloompour.ratio.presentation.gameover.GameOverScreen
import com.bloompour.ratio.presentation.log.TastingLogScreen
import com.bloompour.ratio.presentation.menu.MenuScreen
import com.bloompour.ratio.presentation.pourplan.PourPlanScreen
import com.bloompour.ratio.presentation.ratio.RatioSetterScreen
import com.bloompour.ratio.presentation.settings.BrewSettingsScreen
import com.bloompour.ratio.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()
    val lightTheme by appViewModel.lightTheme.collectAsStateWithLifecycle()

    AppTheme(useLightTheme = lightTheme) {
        BackHandler(enabled = canGoBack) {
            appViewModel.back()
        }

        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(tween(420)) togetherWith fadeOut(tween(300))
            }
        ) { target ->
            when (target) {
                Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.openMenu() }
                )

                Screen.Menu -> MenuScreen(
                    onStartBrew = { appViewModel.navigate(Screen.RatioSetter) },
                    onOpenLog = { appViewModel.navigate(Screen.TastingLog) },
                    onOpenSettings = { appViewModel.navigate(Screen.Settings) }
                )

                Screen.RatioSetter -> RatioSetterScreen(
                    onBack = { appViewModel.back() },
                    onBuildPlan = { appViewModel.navigate(Screen.PourPlan) }
                )

                Screen.PourPlan -> PourPlanScreen(
                    onBack = { appViewModel.back() },
                    onStartTimer = { appViewModel.navigate(Screen.Game) }
                )

                Screen.Game -> GameScreen(
                    onAbort = { appViewModel.openMenu() },
                    onFinished = {
                        appViewModel.restartFlow(Screen.GameOver(System.currentTimeMillis()))
                    }
                )

                is Screen.GameOver -> GameOverScreen(
                    onBrewAgain = { appViewModel.restartFlow(Screen.RatioSetter) },
                    onMenu = { appViewModel.openMenu() }
                )

                Screen.TastingLog -> TastingLogScreen(
                    onBack = { appViewModel.back() },
                    onStartBrew = { appViewModel.restartFlow(Screen.RatioSetter) }
                )

                Screen.Settings -> BrewSettingsScreen(
                    onBack = { appViewModel.back() }
                )
            }
        }
    }
}
