package sk.awisoft.sudokuplus.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import sk.awisoft.sudokuplus.R

@Composable
fun PremiumUpsellDialog(
    onWatchAd: () -> Unit,
    onUpgrade: () -> Unit,
    onDismiss: () -> Unit,
    freeHintsRemaining: Int
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.ai_hints_limit_reached))
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.ai_hints_limit_message),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Watch ad
                OutlinedButton(
                    onClick = onWatchAd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(stringResource(R.string.ai_watch_ad_for_hint))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Option 2: Upgrade to premium
                Button(
                    onClick = onUpgrade,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(stringResource(R.string.premium_upgrade))
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
