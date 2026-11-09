package com.bloompour.ratio.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.model.PourPlan
import com.bloompour.ratio.domain.model.PourStage
import com.bloompour.ratio.domain.repository.SettingsRepository
import com.bloompour.ratio.domain.session.BrewSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.ceil

class GameViewModel(
    private val brewSession: BrewSession,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var plan: PourPlan? = null
    private var loopJob: Job? = null
    private var pulseJob: Job? = null
    private var elapsedMs = 0L
    private var currentStage = 0

    init {
        viewModelScope.launch {
            brewSession.draft
                .map { it.planToken to it.plan }
                .distinctUntilChanged()
                .collect { pair ->
                    val next = pair.second
                    if (next != null) {
                        restart(next)
                    }
                }
        }
    }

    fun pause() {
        loopJob?.cancel()
        loopJob = null
        if (_uiState.value.running) {
            _uiState.value = _uiState.value.copy(running = false)
        }
    }

    fun resume() {
        val active = plan ?: return
        if (_uiState.value.finished) return
        if (loopJob != null) return
        _uiState.value = _uiState.value.copy(running = true)
        startLoop(active)
    }

    fun togglePause() {
        if (_uiState.value.running) pause() else resume()
    }

    fun nextStage() {
        val active = plan ?: return
        if (_uiState.value.finished) return
        val stage = stageAt(active, elapsedMs)
        if (stage.index >= active.stages.lastIndex) {
            finishBrew()
            return
        }
        elapsedMs = stage.endSecond * 1000L
        publish(active)
    }

    fun finishBrew() {
        pause()
        val active = plan
        if (active != null) {
            elapsedMs = active.totalSeconds * 1000L
            publish(active)
        }
        _uiState.value = _uiState.value.copy(finished = true, running = false)
        viewModelScope.launch { eventChannel.send(GameEvent.Finished) }
    }

    fun abort() {
        pause()
        plan = null
        _uiState.value = GameUiState()
    }

    private fun restart(active: PourPlan) {
        loopJob?.cancel()
        loopJob = null
        plan = active
        elapsedMs = 0L
        currentStage = 0
        publish(active)
        _uiState.value = _uiState.value.copy(finished = false, running = true)
        startLoop(active)
    }

    private fun startLoop(active: PourPlan) {
        loopJob = viewModelScope.launch {
            var last = System.nanoTime()
            while (isActive) {
                delay(GameConfig.FRAME_MS)
                val now = System.nanoTime()
                elapsedMs += (now - last) / 1_000_000L
                last = now
                val totalMs = active.totalSeconds * 1000L
                if (elapsedMs >= totalMs) {
                    elapsedMs = totalMs
                    publish(active)
                    finishBrew()
                    return@launch
                }
                publish(active)
            }
        }
    }

    private fun publish(active: PourPlan) {
        val settings = settingsRepository.settings.value
        val stage = stageAt(active, elapsedMs)
        val stageElapsedMs = elapsedMs - stage.startSecond * 1000L
        val stageTotalMs = stage.seconds * 1000L
        val remainingMs = (stageTotalMs - stageElapsedMs).coerceAtLeast(0L)
        val fraction = if (stageTotalMs <= 0L) 0f else (remainingMs.toFloat() / stageTotalMs)
        val poured = stage.startWater + (stage.targetWater - stage.startWater) *
            (1f - fraction).coerceIn(0f, 1f)

        if (stage.index != currentStage) {
            currentStage = stage.index
            signalStageChange(stage.index, settings.stageAlerts)
        }

        _uiState.value = _uiState.value.copy(
            hasPlan = true,
            stageIndex = stage.index,
            stageCount = active.stages.size,
            stageKind = stage.kind,
            stagePourNumber = stage.pourNumber,
            stageProgress = fraction.coerceIn(0f, 1f),
            countdownText = BrewFormat.timeText(ceil(remainingMs / 1000.0).toInt()),
            targetText = BrewFormat.weightText(stage.targetWater.toFloat(), settings.useOunces),
            poured = poured,
            totalWater = active.water.toFloat(),
            lastStage = stage.index >= active.stages.lastIndex,
            stageAlerts = settings.stageAlerts,
            useOunces = settings.useOunces
        )
    }

    private fun signalStageChange(index: Int, alerts: Boolean) {
        pulseJob?.cancel()
        pulseJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(pulse = true)
            delay(GameConfig.STAGE_PULSE_MS.toLong())
            _uiState.value = _uiState.value.copy(pulse = false)
        }
        if (!alerts) return
        viewModelScope.launch {
            eventChannel.send(GameEvent.StageChanged(index))
            delay(GameConfig.HAPTIC_GAP_MS)
            eventChannel.send(GameEvent.StageChanged(index))
        }
    }

    private fun stageAt(active: PourPlan, millis: Long): PourStage {
        val seconds = (millis / 1000L).toInt()
        return active.stages.lastOrNull { seconds >= it.startSecond } ?: active.stages.first()
    }

    override fun onCleared() {
        loopJob?.cancel()
        loopJob = null
        pulseJob?.cancel()
        pulseJob = null
        super.onCleared()
    }
}
