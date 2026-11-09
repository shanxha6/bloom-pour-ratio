package com.bloompour.ratio.presentation.pourplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloompour.ratio.R
import com.bloompour.ratio.core.di.AppViewModelFactory
import com.bloompour.ratio.core.theme.Caramel
import com.bloompour.ratio.core.theme.CeramicSurface
import com.bloompour.ratio.core.theme.CeramicSurfaceAlt
import com.bloompour.ratio.core.theme.Mint
import com.bloompour.ratio.core.theme.RoastBackground
import com.bloompour.ratio.core.theme.TextMuted
import com.bloompour.ratio.core.theme.TextPrimary
import com.bloompour.ratio.core.ui.BrewRing
import com.bloompour.ratio.core.ui.GrainBackground
import com.bloompour.ratio.core.ui.PrimaryButton
import com.bloompour.ratio.core.ui.ScreenHeader
import com.bloompour.ratio.core.ui.SecondaryButton
import com.bloompour.ratio.core.ui.StatCard
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.model.PourStage
import com.bloompour.ratio.domain.model.StageKind

@Composable
fun PourPlanScreen(
    onBack: () -> Unit,
    onStartTimer: () -> Unit
) {
    val viewModel: PourPlanViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PourPlanContent(
        state = state,
        onBack = onBack,
        onStartTimer = {
            viewModel.onStartTimer()
            onStartTimer()
        },
        onEditRatio = onBack
    )
}

@Composable
private fun PourPlanContent(
    state: PourPlanUiState,
    onBack: () -> Unit,
    onStartTimer: () -> Unit,
    onEditRatio: () -> Unit
) {
    GrainBackground(
        colors = listOf(
            CeramicSurfaceAlt.copy(alpha = 0.92f),
            RoastBackground.copy(alpha = 0.97f)
        ),
        imageRes = R.drawable.bg_game,
        imageAlpha = 0.16f,
        grain = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.plan_title),
                onBack = onBack,
                trailing = {
                    Text(
                        text = stringResource(R.string.ratio_value, state.ratioText),
                        style = MaterialTheme.typography.titleLarge,
                        color = Mint
                    )
                }
            )

            val plan = state.plan
            if (plan == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.plan_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BrewRing(
                        progress = 1f,
                        poured = 0f,
                        total = plan.water.toFloat(),
                        accent = Caramel,
                        sizeDp = 88.dp,
                        showSprite = true
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        StatCard(
                            label = stringResource(R.string.stat_dose),
                            value = state.doseText,
                            accent = Caramel,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.stat_water),
                            value = state.waterText,
                            accent = Mint,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.stat_time),
                            value = state.timeText,
                            accent = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    plan.stages.forEach { stage ->
                        StageRow(stage = stage, useOunces = state.useOunces)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_start_timer),
                onClick = onStartTimer,
                enabled = state.plan != null
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_edit_ratio),
                onClick = onEditRatio,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StageRow(stage: PourStage, useOunces: Boolean) {
    val accent = if (stage.kind == StageKind.BLOOM) Mint else Caramel
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CeramicSurface),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(color = accent, shape = CircleShape)
            ) {
                Text(
                    text = (stage.index + 1).toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF120D0B)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stageName(stage),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(
                        R.string.stage_window,
                        BrewFormat.timeText(stage.startSecond),
                        BrewFormat.timeText(stage.endSecond)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            if (stage.kind == StageKind.POUR && stage.pourNumber == 2) {
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = stringResource(R.string.cd_kettle),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = BrewFormat.weightText(stage.targetWater.toFloat(), useOunces),
                style = MaterialTheme.typography.titleMedium,
                color = accent
            )
        }
    }
}

@Composable
private fun stageName(stage: PourStage): String = when (stage.kind) {
    StageKind.BLOOM -> stringResource(R.string.stage_bloom)
    StageKind.POUR -> stringResource(R.string.stage_pour, stage.pourNumber)
    StageKind.DRAWDOWN -> stringResource(R.string.stage_drawdown)
}
