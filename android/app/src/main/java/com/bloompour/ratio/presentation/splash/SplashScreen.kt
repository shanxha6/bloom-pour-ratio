package com.bloompour.ratio.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
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
import com.bloompour.ratio.core.theme.Mint
import com.bloompour.ratio.core.theme.RoastDeep
import com.bloompour.ratio.core.theme.TextMuted
import com.bloompour.ratio.core.theme.TextPrimary
import com.bloompour.ratio.core.theme.TextSecondary

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory)
    val finished by viewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }

    val logoScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.82f,
        animationSpec = tween(durationMillis = 620)
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 220)
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 420)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoastDeep)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(CeramicSurface.copy(alpha = 0.85f), RoastDeep),
                        center = Offset(540f, 760f),
                        radius = 1100f
                    )
                )
        )
        SteamStrands(modifier = Modifier.fillMaxSize())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(148.dp)
                    .scale(logoScale)
            )
            Spacer(modifier = Modifier.height(28.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.alpha(titleAlpha)) {
                Text(
                    text = stringResource(R.string.splash_title_top),
                    style = MaterialTheme.typography.displaySmall,
                    color = Caramel.copy(alpha = 0.30f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                )
                Text(
                    text = stringResource(R.string.splash_title_top),
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.splash_title_bottom),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )
            Spacer(modifier = Modifier.height(44.dp))
            LinearProgressIndicator(
                color = Caramel,
                trackColor = CeramicSurface,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .width(168.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.splash_progress),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SteamStrands(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val first by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val second by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val third by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, delayMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val phases = listOf(first, second, third)
        val baseY = size.height * 0.72f
        phases.forEachIndexed { index, phase ->
            val x = size.width * (0.36f + index * 0.14f)
            val travel = size.height * 0.34f
            val y = baseY - travel * phase
            val fade = (1f - phase).coerceIn(0f, 1f) * 0.22f
            drawRoundRect(
                color = Mint.copy(alpha = fade),
                topLeft = Offset(x, y),
                size = Size(5f, travel * 0.42f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
