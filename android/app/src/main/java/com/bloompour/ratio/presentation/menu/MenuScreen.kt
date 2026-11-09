package com.bloompour.ratio.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.bloompour.ratio.core.theme.TextSecondary
import com.bloompour.ratio.core.ui.PrimaryButton
import com.bloompour.ratio.core.ui.StatCard
import com.bloompour.ratio.domain.model.BrewFormat
import com.bloompour.ratio.domain.model.Recipe

@Composable
fun MenuScreen(
    onStartBrew: () -> Unit,
    onOpenLog: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MenuContent(
        state = state,
        onSelectRecipe = viewModel::onSelectRecipe,
        onStartBrew = {
            viewModel.onStartBrew()
            onStartBrew()
        },
        onOpenLog = onOpenLog,
        onOpenSettings = onOpenSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onSelectRecipe: (Recipe) -> Unit,
    onStartBrew: () -> Unit,
    onOpenLog: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var sheetVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { sheetVisible = true }

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
                            Color.Transparent,
                            RoastBackground.copy(alpha = 0.55f),
                            RoastBackground
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                IconButton(onClick = onOpenSettings, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_settings),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onOpenLog,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .background(
                            color = CeramicSurfaceAlt.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = stringResource(R.string.cta_log),
                        style = MaterialTheme.typography.labelSmall,
                        color = Caramel
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(96.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.splash_title_top),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(R.string.splash_title_bottom),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Caramel
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(18.dp))
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_dripper),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(180.dp)
                )
            }

            AnimatedVisibility(
                visible = sheetVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(380))
            ) {
                RecipeSheet(
                    state = state,
                    onSelectRecipe = onSelectRecipe,
                    onStartBrew = onStartBrew
                )
            }
        }
    }
}

@Composable
private fun RecipeSheet(
    state: MenuUiState,
    onSelectRecipe: (Recipe) -> Unit,
    onStartBrew: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = CeramicSurfaceAlt.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Caramel.copy(alpha = 0.22f)),
        shadowElevation = 18.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .width(44.dp)
                    .height(4.dp)
                    .background(color = TextMuted, shape = CircleShape)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (state.recipes.size >= 2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.recipes.forEach { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            selected = recipe.name == state.selectedRecipe,
                            onClick = { onSelectRecipe(recipe) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.brewCount > 0 && state.hasFavouriteRatio) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_brews),
                        value = state.brewCount.toString(),
                        accent = Caramel,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_best),
                        value = stringResource(R.string.ratio_value, state.favouriteRatioText),
                        accent = Mint,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_start_brew),
                onClick = onStartBrew
            )
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CeramicSurface),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Caramel else Caramel.copy(alpha = 0.28f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .height(92.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selected) {
                    Spacer(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = Caramel, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.ratio_value, BrewFormat.ratioText(recipe.ratio)),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.grams_value, BrewFormat.doseText(recipe.dose)),
                style = MaterialTheme.typography.labelSmall,
                color = Caramel
            )
        }
    }
}
