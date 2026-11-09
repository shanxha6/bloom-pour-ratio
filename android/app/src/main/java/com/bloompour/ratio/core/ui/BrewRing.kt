package com.bloompour.ratio.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bloompour.ratio.R
import com.bloompour.ratio.core.theme.CeramicSurface
import com.bloompour.ratio.core.theme.Mint

@Composable
fun BrewRing(
    progress: Float,
    poured: Float,
    total: Float,
    accent: Color,
    sizeDp: Dp,
    modifier: Modifier = Modifier,
    showSprite: Boolean = true,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val sweep = progress.coerceIn(0f, 1f)
    val fill = if (total <= 0f) 0f else (poured / total).coerceIn(0f, 1f)
    val strokeDp = sizeDp * 0.056f

    Box(modifier = modifier.size(sizeDp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeDp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = CeramicSurface,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = -360f * sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            val innerRadius = size.minDimension / 2f - stroke - 6.dp.toPx()
            if (innerRadius > 0f) {
                val circle = Path().apply {
                    addOval(
                        androidx.compose.ui.geometry.Rect(
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = innerRadius
                        )
                    )
                }
                clipPath(circle) {
                    val levelHeight = innerRadius * 2f * fill
                    drawRect(
                        color = Mint.copy(alpha = 0.28f),
                        topLeft = Offset(
                            size.width / 2f - innerRadius,
                            size.height / 2f + innerRadius - levelHeight
                        ),
                        size = Size(innerRadius * 2f, levelHeight)
                    )
                }
            }
        }

        if (showSprite) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(R.string.cd_dripper),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(sizeDp * 0.46f)
            )
        }

        content()
    }
}
