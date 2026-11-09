package com.bloompour.ratio.presentation.ratio

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
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
import com.bloompour.ratio.core.ui.GrainBackground
import com.bloompour.ratio.core.ui.PrimaryButton
import com.bloompour.ratio.core.ui.ScreenHeader
import com.bloompour.ratio.core.ui.SecondaryButton
import com.bloompour.ratio.core.ui.ValueStepper
import com.bloompour.ratio.domain.model.BrewFormat
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun RatioSetterScreen(
    onBack: () -> Unit,
    onBuildPlan: () -> Unit
) {
    val viewModel: RatioSetterViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RatioSetterContent(
        state = state,
        onBack = onBack,
        onRatioChange = viewModel::onRatioChange,
        onModeChange = viewModel::onModeChange,
        onDecrease = viewModel::onDecrease,
        onIncrease = viewModel::onIncrease,
        onSaveRecipe = viewModel::onSaveRecipe,
        onBuildPlan = onBuildPlan
    )
}

@Composable
private fun RatioSetterContent(
    state: RatioSetterUiState,
    onBack: () -> Unit,
    onRatioChange: (Float) -> Unit,
    onModeChange: (Boolean) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSaveRecipe: () -> Unit,
    onBuildPlan: () -> Unit
) {
    GrainBackground(
        colors = listOf(
            CeramicSurfaceAlt.copy(alpha = 0.92f),
            RoastBackground.copy(alpha = 0.97f)
        ),
        imageRes = R.drawable.bg_game,
        imageAlpha = 0.16f
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.ratio_title),
                onBack = onBack,
                trailing = {
                    Text(
                        text = stringResource(R.string.ratio_value, state.ratioText),
                        style = MaterialTheme.typography.titleLarge,
                        color = Mint
                    )
                }
            )

            RatioWheel(ratio = state.ratio, onRatioChange = onRatioChange)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ModeChip(
                    label = stringResource(R.string.mode_by_dose),
                    active = state.byDose,
                    onClick = { onModeChange(true) },
                    modifier = Modifier.weight(1f)
                )
                ModeChip(
                    label = stringResource(R.string.mode_by_cup),
                    active = !state.byDose,
                    onClick = { onModeChange(false) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CeramicSurface),
                border = BorderStroke(1.dp, Caramel.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = if (state.byDose) {
                            stringResource(R.string.label_dose)
                        } else {
                            stringResource(R.string.label_cup)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ValueStepper(
                        value = state.editableText,
                        onDecrease = onDecrease,
                        onIncrease = onIncrease,
                        decreaseEnabled = state.canDecrease,
                        increaseEnabled = state.canIncrease
                    )
                    Text(
                        text = unitLabel(state.byDose, state.useOunces),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_2),
                    contentDescription = stringResource(R.string.cd_beans),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(
                        if (state.byDose) R.string.derived_water else R.string.derived_coffee,
                        state.derivedText,
                        unitLabel(!state.byDose, state.useOunces)
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Mint
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.cta_build_plan),
                onClick = onBuildPlan
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = if (state.saved) {
                    stringResource(R.string.cta_recipe_saved)
                } else {
                    stringResource(R.string.cta_save_recipe)
                },
                onClick = onSaveRecipe,
                enabled = !state.saved,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun unitLabel(forDose: Boolean, useOunces: Boolean): String = when {
    forDose -> stringResource(R.string.unit_grams)
    useOunces -> stringResource(R.string.unit_ounces)
    else -> stringResource(R.string.unit_millilitres)
}

@Composable
private fun ModeChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(48.dp)
            .background(
                color = if (active) CeramicSurface else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .semantics {
                stateDescription = label
            }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) TextPrimary else TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RatioWheel(
    ratio: Float,
    onRatioChange: (Float) -> Unit
) {
    val count = ((GameConfig.RATIO_MAX - GameConfig.RATIO_MIN) / GameConfig.RATIO_STEP).roundToInt() + 1
    val targetIndex = ((ratio - GameConfig.RATIO_MIN) / GameConfig.RATIO_STEP)
        .roundToInt()
        .coerceIn(0, count - 1)

    var selected by remember { mutableIntStateOf(targetIndex) }
    var drag by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(targetIndex) {
        if (targetIndex != selected) {
            selected = targetIndex
        }
    }

    val density = LocalDensity.current
    val itemWidth = 64.dp
    val itemPx = with(density) { itemWidth.toPx() }
    val haptics = LocalHapticFeedback.current

    val animatedDrag by animateFloatAsState(
        targetValue = drag,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)
    )

    val dragState = rememberDraggableState { delta ->
        var next = drag + delta
        var index = selected
        while (next > itemPx / 2f && index > 0) {
            index -= 1
            next -= itemPx
        }
        while (next < -itemPx / 2f && index < count - 1) {
            index += 1
            next += itemPx
        }
        if (index != selected) {
            selected = index
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onRatioChange(GameConfig.RATIO_MIN + index * GameConfig.RATIO_STEP)
        }
        drag = next.coerceIn(-itemPx, itemPx)
    }

    val currentText = BrewFormat.ratioText(GameConfig.RATIO_MIN + selected * GameConfig.RATIO_STEP)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clipToBounds()
            .draggable(
                state = dragState,
                orientation = Orientation.Horizontal,
                onDragStopped = { drag = 0f }
            )
            .semantics {
                stateDescription = currentText
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset { IntOffset(animatedDrag.roundToInt(), 0) }
        ) {
            for (step in -2..2) {
                val index = selected + step
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(itemWidth)
                        .height(64.dp)
                ) {
                    if (index in 0 until count) {
                        val distance = abs(step)
                        val value = GameConfig.RATIO_MIN + index * GameConfig.RATIO_STEP
                        Text(
                            text = BrewFormat.ratioText(value),
                            style = if (distance == 0) {
                                MaterialTheme.typography.displaySmall
                            } else {
                                MaterialTheme.typography.titleLarge
                            },
                            color = if (distance == 0) TextPrimary else TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.alpha(
                                when (distance) {
                                    0 -> 1f
                                    1 -> 0.6f
                                    else -> 0.3f
                                }
                            )
                        )
                    }
                }
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = Caramel,
                topLeft = Offset(size.width / 2f - 2f, size.height * 0.12f),
                size = Size(4f, size.height * 0.12f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                color = Caramel.copy(alpha = 0.6f),
                topLeft = Offset(size.width / 2f - 2f, size.height * 0.76f),
                size = Size(4f, size.height * 0.12f),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }
}
