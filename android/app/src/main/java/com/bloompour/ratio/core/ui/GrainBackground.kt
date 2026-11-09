package com.bloompour.ratio.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.bloompour.ratio.core.theme.TextPrimary
import kotlin.random.Random

@Composable
fun GrainBackground(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    imageRes: Int? = null,
    imageAlpha: Float = 1f,
    grain: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(colors.first())) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = imageAlpha,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors))
        )
        if (grain) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val random = Random(2026)
                repeat(140) {
                    drawCircle(
                        color = TextPrimary.copy(alpha = 0.03f),
                        radius = 1.4f + random.nextFloat() * 1.6f,
                        center = Offset(
                            random.nextFloat() * size.width,
                            random.nextFloat() * size.height
                        )
                    )
                }
            }
        }
        content()
    }
}
