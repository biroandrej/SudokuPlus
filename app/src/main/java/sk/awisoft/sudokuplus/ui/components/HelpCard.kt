package sk.awisoft.sudokuplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.ui.theme.ColorUtils.harmonizeWithPrimary
import sk.awisoft.sudokuplus.ui.theme.SudokuPlusTheme
import sk.awisoft.sudokuplus.ui.util.LightDarkPreview

@Composable
fun HelpCard(
    modifier: Modifier = Modifier,
    title: String,
    details: String,
    painter: Painter?,
    onCloseClicked: () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (painter != null) {
                        Icon(
                            painter = painter,
                            contentDescription = null
                        )
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(onClick = onCloseClicked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_round_close_24),
                        contentDescription = null
                    )
                }
            }
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@LightDarkPreview
@Composable
fun HelpCardPreview() {
    SudokuPlusTheme {
        HelpCard(
            title = "This is the title",
            details = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam tempus arcu vitae elit congue scelerisque. Sed a vestibulum tellus. Suspendisse tristique dui eget nisi dictum tempus",
            painter = painterResource(R.drawable.ic_outline_verified_24),
            onCloseClicked = {}
        )
    }
}

@Composable
fun GrantPermissionCard(
    modifier: Modifier = Modifier,
    title: String,
    details: String,
    painter: Painter?,
    confirmButton: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (painter != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.harmonizeWithPrimary())
                        ) {
                            Icon(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            confirmButton()
        }
    }
}

@LightDarkPreview
@Composable
fun GrantPermissionCardPreview() {
    SudokuPlusTheme {
        GrantPermissionCard(
            title = "This is the title",
            details = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam tempus arcu vitae elit congue scelerisque. Sed a vestibulum tellus. Suspendisse tristique dui eget nisi dictum tempus",
            painter = rememberVectorPainter(image = Icons.Rounded.TipsAndUpdates),
            confirmButton = {
                Button(
                    onClick = { },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Grant")
                }
            }
        )
    }
}

@Composable
fun BackupFailureCard(
    modifier: Modifier = Modifier,
    title: String,
    details: String,
    painter: Painter?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    retryButtonText: String,
    dismissButtonText: String
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (painter != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                painter = painter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(R.drawable.ic_round_close_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onRetry
                ) {
                    Text(retryButtonText)
                }
            }
        }
    }
}
