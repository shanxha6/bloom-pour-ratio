package com.bloompour.ratio.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.bloompour.ratio.core.theme.TextPrimary

@Composable
fun VerdictChip(
    label: String,
    accent: Color,
    selected: Boolean,
    stateLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container by animateColorAsState(
        targetValue = if (selected) accent.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = tween(220)
    )
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, accent),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = container,
            contentColor = if (selected) accent else TextPrimary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .semantics { stateDescription = stateLabel }
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}
