package com.bloompour.ratio.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
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
import com.bloompour.ratio.core.ui.GrainBackground
import com.bloompour.ratio.core.ui.ScreenHeader
import com.bloompour.ratio.core.ui.SecondaryButton

@Composable
fun BrewSettingsScreen(onBack: () -> Unit) {
    val viewModel: BrewSettingsViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BrewSettingsContent(
        state = state,
        onBack = onBack,
        onUnitsChange = viewModel::onUnitsChange,
        onRatioDecrease = viewModel::onRatioDecrease,
        onRatioIncrease = viewModel::onRatioIncrease,
        onBloomDecrease = viewModel::onBloomDecrease,
        onBloomIncrease = viewModel::onBloomIncrease,
        onStageAlertsChange = viewModel::onStageAlertsChange,
        onLightThemeChange = viewModel::onLightThemeChange
    )
}

@Composable
private fun BrewSettingsContent(
    state: BrewSettingsUiState,
    onBack: () -> Unit,
    onUnitsChange: (Boolean) -> Unit,
    onRatioDecrease: () -> Unit,
    onRatioIncrease: () -> Unit,
    onBloomDecrease: () -> Unit,
    onBloomIncrease: () -> Unit,
    onStageAlertsChange: (Boolean) -> Unit,
    onLightThemeChange: (Boolean) -> Unit
) {
    GrainBackground(
        colors = listOf(
            CeramicSurfaceAlt.copy(alpha = 0.9f),
            RoastBackground.copy(alpha = 0.97f)
        ),
        imageRes = R.drawable.bg_game,
        imageAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.settings_title),
                onBack = onBack
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                SettingRow(label = stringResource(R.string.settings_units)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UnitChip(
                            label = stringResource(R.string.unit_grams_full),
                            active = !state.useOunces,
                            onClick = { onUnitsChange(false) }
                        )
                        UnitChip(
                            label = stringResource(R.string.unit_ounces_full),
                            active = state.useOunces,
                            onClick = { onUnitsChange(true) }
                        )
                    }
                }

                SettingRow(label = stringResource(R.string.settings_default_ratio)) {
                    CompactStepper(
                        value = stringResource(R.string.ratio_value, state.defaultRatioText),
                        onDecrease = onRatioDecrease,
                        onIncrease = onRatioIncrease,
                        decreaseEnabled = state.canLowerRatio,
                        increaseEnabled = state.canRaiseRatio
                    )
                }

                SettingRow(label = stringResource(R.string.settings_bloom)) {
                    CompactStepper(
                        value = stringResource(R.string.multiplier_value, state.bloomMultiplierText),
                        onDecrease = onBloomDecrease,
                        onIncrease = onBloomIncrease,
                        decreaseEnabled = state.canLowerBloom,
                        increaseEnabled = state.canRaiseBloom
                    )
                }

                val alertsState = stringResource(
                    if (state.stageAlerts) R.string.state_on else R.string.state_off
                )
                SettingRow(label = stringResource(R.string.settings_alerts)) {
                    Switch(
                        checked = state.stageAlerts,
                        onCheckedChange = onStageAlertsChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoastBackground,
                            checkedTrackColor = Mint,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = CeramicSurfaceAlt
                        ),
                        modifier = Modifier.semantics {
                            stateDescription = alertsState
                        }
                    )
                }

                val themeState = stringResource(
                    if (state.lightTheme) R.string.state_on else R.string.state_off
                )
                SettingRow(label = stringResource(R.string.settings_light_theme)) {
                    Switch(
                        checked = state.lightTheme,
                        onCheckedChange = onLightThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoastBackground,
                            checkedTrackColor = Caramel,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = CeramicSurfaceAlt
                        ),
                        modifier = Modifier.semantics {
                            stateDescription = themeState
                        }
                    )
                }
            }

            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    trailing: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CeramicSurface),
        border = BorderStroke(1.dp, Caramel.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            trailing()
        }
    }
}

@Composable
private fun UnitChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(72.dp)
            .height(48.dp)
            .background(
                color = if (active) Caramel.copy(alpha = 0.22f) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .semantics { stateDescription = label }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) Caramel else TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CompactStepper(
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    decreaseEnabled: Boolean,
    increaseEnabled: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SecondaryButton(
            text = stringResource(R.string.stepper_minus),
            onClick = onDecrease,
            enabled = decreaseEnabled,
            modifier = Modifier.width(52.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Caramel,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(76.dp)
        )
        SecondaryButton(
            text = stringResource(R.string.stepper_plus),
            onClick = onIncrease,
            enabled = increaseEnabled,
            modifier = Modifier.width(52.dp)
        )
    }
}
