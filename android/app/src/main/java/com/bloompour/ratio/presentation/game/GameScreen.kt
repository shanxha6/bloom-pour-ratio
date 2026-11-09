package com.bloompour.ratio.presentation.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloompour.ratio.R
import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.core.di.AppViewModelFactory
import com.bloompour.ratio.core.theme.Caramel
import com.bloompour.ratio.core.theme.CeramicSurface
import com.bloompour.ratio.core.theme.CeramicSurfaceAlt
import com.bloompour.ratio.core.theme.Mint
import com.bloompour.ratio.core.theme.RoastBackground
import com.bloompour.ratio.core.theme.TextMuted
import com.bloompour.ratio.core.theme.TextPrimary
import com.bloompour.ratio.core.ui.BrewRing
import com.bloompour.ratio.core.ui.PrimaryButton
import com.bloompour.ratio.core.ui.SecondaryButton
import com.bloompour.ratio.domain.model.StageKind
import com.bloompour.ratio.presentation.dialog.AbortBrewDialog

@Composable
fun GameScreen(
    onAbort: () -> Unit,
    onFinished: () -> Unit
) {
    val viewModel: GameViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    var abortVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.StageChanged ->
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                GameEvent.Finished -> onFinished()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }

    GameContent(
        state = state,
        pulse = state.pulse,
        onRequestAbort = { abortVisible = true },
        onTogglePause = viewModel::togglePause,
        onNextStage = viewModel::nextStage,
        onFinish = viewModel::finishBrew
    )

    if (abortVisible) {
        AbortBrewDialog(
            onConfirm = {
                abortVisible = false
                viewModel.abort()
                onAbort()
            },
            onDismiss = { abortVisible = false }
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    pulse: Boolean,
    onRequestAbort: () -> Unit,
    onTogglePause: () -> Unit,
    onNextStage: () -> Unit,
    onFinish: () -> Unit
) {
    val baseAccent = when {
        !state.running -> TextMuted
        state.stageKind == StageKind.POUR -> Mint
        else -> Caramel
    }
    val accent by animateColorAsState(
        targetValue = if (pulse) TextPrimary else baseAccent,
        animationSpec = tween(GameConfig.STAGE_PULSE_MS)
    )

    Box(modifier = Modifier.fillMaxSize().background(RoastBackground)) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            RoastBackground.copy(alpha = 0.82f),
                            RoastBackground.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 12.dp)
            ) {
                IconButton(onClick = onRequestAbort, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_menu),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = stageLabel(state),
                    style = MaterialTheme.typography.titleLarge,
                    color = accent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
                IconButton(onClick = onTogglePause, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_pause),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val available = if (maxWidth - 64.dp < maxHeight - 24.dp) {
                    maxWidth - 64.dp
                } else {
                    maxHeight - 24.dp
                }
                val ringSize = if (available < 320.dp) available else 320.dp

                BrewRing(
                    progress = state.stageProgress,
                    poured = state.poured,
                    total = state.totalWater,
                    accent = accent,
                    sizeDp = ringSize,
                    showSprite = false
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = state.countdownText,
                            style = MaterialTheme.typography.displayLarge,
                            color = TextPrimary,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(
                                R.string.game_target,
                                state.targetText,
                                if (state.useOunces) {
                                    stringResource(R.string.unit_ounces)
                                } else {
                                    stringResource(R.string.unit_grams)
                                }
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Mint,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CeramicSurfaceAlt.copy(alpha = 0.94f),
                border = BorderStroke(1.dp, Caramel.copy(alpha = 0.25f)),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SecondaryButton(
                            text = if (state.running) {
                                stringResource(R.string.cta_pause)
                            } else {
                                stringResource(R.string.cta_resume)
                            },
                            onClick = onTogglePause,
                            enabled = !state.finished,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.cta_next_pour),
                            onClick = onNextStage,
                            enabled = !state.finished && !state.lastStage,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (index in 0 until state.stageCount) {
                            Spacer(
                                modifier = Modifier
                                    .size(if (index == state.stageIndex) 10.dp else 8.dp)
                                    .background(
                                        color = if (index <= state.stageIndex) {
                                            Caramel
                                        } else {
                                            CeramicSurface
                                        },
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(
                        text = stringResource(R.string.cta_finish_cup),
                        onClick = onFinish,
                        enabled = !state.finished
                    )
                }
            }
        }
    }
}

@Composable
private fun stageLabel(state: GameUiState): String = when (state.stageKind) {
    StageKind.BLOOM -> stringResource(R.string.stage_bloom)
    StageKind.POUR -> stringResource(R.string.stage_pour, state.stagePourNumber)
    StageKind.DRAWDOWN -> stringResource(R.string.stage_drawdown)
}
