package com.bloompour.ratio.presentation.gameover

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloompour.ratio.R
import com.bloompour.ratio.core.di.AppViewModelFactory
import com.bloompour.ratio.core.theme.Caramel
import com.bloompour.ratio.core.theme.CeramicSurface
import com.bloompour.ratio.core.theme.CeramicSurfaceAlt
import com.bloompour.ratio.core.theme.EmberWarn
import com.bloompour.ratio.core.theme.Mint
import com.bloompour.ratio.core.theme.RoastBackground
import com.bloompour.ratio.core.theme.TextMuted
import com.bloompour.ratio.core.theme.TextPrimary
import com.bloompour.ratio.core.theme.TextSecondary
import com.bloompour.ratio.core.ui.GrainBackground
import com.bloompour.ratio.core.ui.PrimaryButton
import com.bloompour.ratio.core.ui.SecondaryButton
import com.bloompour.ratio.core.ui.VerdictChip
import com.bloompour.ratio.domain.model.AdjustDirection
import com.bloompour.ratio.domain.model.Verdict

@Composable
fun GameOverScreen(
    onBrewAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GameOverContent(
        state = state,
        onVerdict = viewModel::onVerdict,
        onBrewAgain = {
            viewModel.onBrewAgain()
            onBrewAgain()
        },
        onMenu = {
            viewModel.onLeave()
            onMenu()
        }
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onVerdict: (Verdict) -> Unit,
    onBrewAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val transition = rememberInfiniteTransition()
    val tilt by transition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000),
            repeatMode = RepeatMode.Reverse
        )
    )

    GrainBackground(
        colors = listOf(CeramicSurface, RoastBackground),
        grain = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = stringResource(R.string.cd_cup),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .rotate(tilt)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.result_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(
                    R.string.result_subtitle,
                    state.doseText,
                    state.ratioText,
                    state.timeText
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = state.verdict,
                    transitionSpec = {
                        (fadeIn(tween(260)) + slideInVertically { it / 6 })
                            .togetherWith(fadeOut(tween(200)))
                    }
                ) { verdict ->
                    if (verdict == null) {
                        VerdictPicker(onVerdict = onVerdict)
                    } else {
                        AdviceCard(state = state)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_brew_again),
                onClick = onBrewAgain
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VerdictPicker(onVerdict: (Verdict) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.result_hint),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        VerdictChip(
            label = stringResource(R.string.verdict_sour),
            accent = EmberWarn,
            selected = false,
            stateLabel = stringResource(R.string.verdict_sour),
            onClick = { onVerdict(Verdict.SOUR) }
        )
        VerdictChip(
            label = stringResource(R.string.verdict_balanced),
            accent = Mint,
            selected = false,
            stateLabel = stringResource(R.string.verdict_balanced),
            onClick = { onVerdict(Verdict.BALANCED) }
        )
        VerdictChip(
            label = stringResource(R.string.verdict_bitter),
            accent = TextMuted,
            selected = false,
            stateLabel = stringResource(R.string.verdict_bitter),
            onClick = { onVerdict(Verdict.BITTER) }
        )
    }
}

@Composable
private fun AdviceCard(state: GameOverUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CeramicSurfaceAlt),
        border = BorderStroke(1.dp, Mint.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(40.dp)
                    .alpha(0.5f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.advice_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = Mint
                )
                Spacer(modifier = Modifier.height(10.dp))
                AdviceRow(
                    text = when (state.direction) {
                        AdjustDirection.FINER -> stringResource(
                            R.string.advice_grind_finer,
                            state.grindSteps
                        )

                        AdjustDirection.COARSER -> stringResource(
                            R.string.advice_grind_coarser,
                            state.grindSteps
                        )

                        AdjustDirection.KEEP -> stringResource(R.string.advice_grind_keep)
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                AdviceRow(
                    text = stringResource(R.string.advice_next_ratio, state.newRatioText)
                )
                Spacer(modifier = Modifier.height(6.dp))
                AdviceRow(
                    text = stringResource(R.string.advice_grind_setting, state.newGrindText)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when (state.direction) {
                        AdjustDirection.FINER -> stringResource(R.string.advice_reason_finer)
                        AdjustDirection.COARSER -> stringResource(R.string.advice_reason_coarser)
                        AdjustDirection.KEEP -> stringResource(R.string.advice_reason_keep)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun AdviceRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.bullet),
            style = MaterialTheme.typography.titleLarge,
            color = Caramel
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
    }
}
