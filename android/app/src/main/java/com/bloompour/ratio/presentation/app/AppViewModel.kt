package com.bloompour.ratio.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.core.navigation.Screen
import com.bloompour.ratio.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppViewModel(settingsRepository: SettingsRepository) : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    val lightTheme: StateFlow<Boolean> = settingsRepository.settings
        .map { it.lightTheme }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = settingsRepository.settings.value.lightTheme
        )

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) {
            backStack.addLast(current)
        }
        _screen.value = target
        syncBack()
    }

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        syncBack()
    }

    fun restartFlow(target: Screen) {
        backStack.clear()
        backStack.addLast(Screen.Menu)
        _screen.value = target
        syncBack()
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        syncBack()
    }

    private fun syncBack() {
        _canGoBack.value = backStack.isNotEmpty()
    }
}
