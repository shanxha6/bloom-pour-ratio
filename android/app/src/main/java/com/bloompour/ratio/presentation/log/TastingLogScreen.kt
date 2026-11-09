package com.bloompour.ratio.presentation.log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.bloompour.ratio.core.ui.ScreenHeader
import com.bloompour.ratio.core.ui.SecondaryButton
import com.bloompour.ratio.domain.model.Verdict
import com.bloompour.ratio.presentation.dialog.ClearLogDialog

@Composable
fun TastingLogScreen(
    onBack: () -> Unit,
    onStartBrew: () -> Unit
) {
    val viewModel: TastingLogViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var clearVisible by remember { mutableStateOf(false) }

    TastingLogContent(
        state = state,
        onBack = onBack,
        onStartBrew = onStartBrew,
        onRequestClear = { clearVisible = true }
    )

    if (clearVisible) {
        ClearLogDialog(
            onConfirm = {
                clearVisible = false
                viewModel.onClear()
            },
            onDismiss = { clearVisible = false }
        )
    }
}

@Composable
private fun TastingLogContent(
    state: TastingLogUiState,
    onBack: () -> Unit,
    onStartBrew: () -> Unit,
    onRequestClear: () -> Unit
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.log_title),
                onBack = onBack,
                trailing = {
                    Text(
                        text = state.totalCount.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Caramel
                    )
                }
            )

            if (state.rows.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = stringResource(R.string.cd_cup),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(120.dp)
                            .alpha(0.35f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.log_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
                PrimaryButton(
                    text = stringResource(R.string.cta_start_brew),
                    onClick = onStartBrew
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    state.rows.forEach { row ->
                        LogCard(row = row)
                    }
                }
                SecondaryButton(
                    text = stringResource(R.string.cta_clear_log),
                    onClick = onRequestClear,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LogCard(row: LogRow) {
    val accent = when (row.verdict) {
        Verdict.SOUR -> EmberWarn
        Verdict.BALANCED -> Mint
        Verdict.BITTER -> TextMuted
    }
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
                .padding(horizontal = 14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.recipeName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = stringResource(R.string.log_summary, row.ratioText, row.doseText),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Text(
                text = row.dateText,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.width(10.dp))
            Spacer(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = accent, shape = CircleShape)
            )
        }
    }
}
