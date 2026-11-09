package com.bloompour.ratio.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bloompour.ratio.R
import com.bloompour.ratio.core.theme.Caramel
import com.bloompour.ratio.core.theme.CeramicSurfaceAlt
import com.bloompour.ratio.core.theme.EmberWarn
import com.bloompour.ratio.core.theme.TextPrimary
import com.bloompour.ratio.core.theme.TextSecondary

@Composable
fun AbortBrewDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CeramicSurfaceAlt,
        title = {
            Text(
                text = stringResource(R.string.dialog_abort_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_abort_body),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.cta_discard),
                    style = MaterialTheme.typography.labelLarge,
                    color = EmberWarn
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_keep_brewing),
                    style = MaterialTheme.typography.labelLarge,
                    color = Caramel
                )
            }
        }
    )
}
