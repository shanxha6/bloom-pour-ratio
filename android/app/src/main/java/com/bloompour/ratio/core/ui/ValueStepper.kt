package com.bloompour.ratio.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bloompour.ratio.R
import com.bloompour.ratio.core.theme.Caramel
import com.bloompour.ratio.core.theme.TextMuted
import com.bloompour.ratio.core.theme.TextPrimary

@Composable
fun ValueStepper(
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    valueColor: Color = Caramel,
    decreaseEnabled: Boolean = true,
    increaseEnabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        StepperButton(
            label = stringResource(R.string.stepper_minus),
            onClick = onDecrease,
            enabled = decreaseEnabled
        )
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = valueColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        StepperButton(
            label = stringResource(R.string.stepper_plus),
            onClick = onIncrease,
            enabled = increaseEnabled
        )
    }
}

@Composable
private fun StepperButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(1.5.dp, if (enabled) Caramel else TextMuted.copy(alpha = 0.4f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextMuted.copy(alpha = 0.5f)
        ),
        modifier = Modifier.size(48.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.titleLarge)
    }
}
